package com.backend.AltaPinta.service;

import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.model.Pago;
import com.backend.AltaPinta.model.Pedido;
import com.backend.AltaPinta.repository.PagoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registro del resultado de cada intento de cobro (RF020, RNF018).
 *
 * Los dos metodos existen por separado por su propagacion transaccional, que
 * es lo unico que los diferencia de verdad:
 *
 *  - El cobro APROBADO se registra dentro de la transaccion del pedido. Si
 *    algo posterior la revirtiera, el registro debe irse con ella: un pago
 *    aprobado sin su pedido seria una mentira en la base de datos.
 *
 *  - El cobro RECHAZADO se registra en una transaccion PROPIA. Tiene que
 *    ser asi: cuando el cobro falla, la transaccion del pedido se revierte
 *    entera, y un registro escrito dentro de ella desapareceria con el
 *    fallo. Justo el caso que RNF018 pide conservar.
 */
@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepo;

    public PagoService(PagoRepository pagoRepo) {
        this.pagoRepo = pagoRepo;
    }

    /**
     * Deja constancia de un cobro que prospero.
     *
     * Se une a la transaccion en curso (propagacion por defecto): el registro
     * y el pedido se confirman o se revierten juntos.
     */
    @Transactional
    public Pago registrarAprobado(Cliente cliente, Pedido pedido, Long tarjetaId,
                                  BigDecimal monto) {
        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setPedido(pedido);
        pago.setTarjetaId(tarjetaId);
        pago.setMonto(monto);
        pago.setEstado(Pago.APROBADO);
        pago.setFecha(LocalDateTime.now());
        return pagoRepo.save(pago);
    }

    /**
     * Deja constancia de un cobro que no prospero.
     *
     * REQUIRES_NEW abre una transaccion independiente que se confirma por su
     * cuenta. Sin ella, este registro viviria dentro de la transaccion del
     * pedido, que esta a punto de revertirse por el propio fallo, y no
     * quedaria ni rastro del intento.
     *
     * No se propaga ninguna excepcion desde aqui: si el registro fallara, el
     * cliente debe seguir recibiendo el motivo real por el que no pudo pagar,
     * no un error de auditoria que no le dice nada.
     *
     * @param pedido queda a null a proposito: en un rechazo el pedido nunca
     *               llego a crearse.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarRechazado(Cliente cliente, Long tarjetaId,
                                   BigDecimal monto, String motivo) {
        try {
            Pago pago = new Pago();
            pago.setCliente(cliente);
            pago.setPedido(null);
            pago.setTarjetaId(tarjetaId);
            pago.setMonto(monto != null ? monto : BigDecimal.ZERO);
            pago.setEstado(Pago.RECHAZADO);
            pago.setMotivo(recortar(motivo));
            pago.setFecha(LocalDateTime.now());
            pagoRepo.save(pago);
        } catch (Exception ex) {
            log.error("No se pudo registrar el intento de cobro rechazado del cliente {}",
                    cliente != null ? cliente.getId() : null, ex);
        }
    }

    /** La columna admite 255; un mensaje mas largo se corta antes de guardarlo. */
    private String recortar(String motivo) {
        if (motivo == null) return null;
        return motivo.length() <= 255 ? motivo : motivo.substring(0, 252) + "...";
    }
}
