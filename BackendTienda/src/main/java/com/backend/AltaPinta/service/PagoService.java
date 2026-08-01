package com.backend.AltaPinta.service;

import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.model.Pago;
import com.backend.AltaPinta.model.Pedido;
import com.backend.AltaPinta.model.Tarjeta;
import com.backend.AltaPinta.repository.PagoRepository;
import com.backend.AltaPinta.repository.TarjetaRepository;
import org.springframework.stereotype.Service;

@Service
public class PagoService {

    private final TarjetaRepository tarjetaRepo;
    private final PagoRepository pagoRepo;

    public PagoService(TarjetaRepository tarjetaRepo, PagoRepository pagoRepo) {
        this.tarjetaRepo = tarjetaRepo;
        this.pagoRepo = pagoRepo;
    }

    public boolean procesarPago(
            Pedido pedido,
            Cliente cliente,
            String numero,
            String cvv,
            String fechaVencimiento,
            Double monto
    ) {

        Tarjeta t = tarjetaRepo
                .findByNumeroAndClienteId(numero, cliente.getId())
                .orElseThrow(() -> new RuntimeException("Tarjeta no pertenece al cliente"));

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setTarjeta(t);
        pago.setMonto(monto);

        if (!t.getActiva() || t.getSaldo() < monto) {
            pago.setEstado("RECHAZADO");
            pagoRepo.save(pago);
            return false;
        }

        // descuento
        t.setSaldo(t.getSaldo() - monto);
        tarjetaRepo.save(t);

        pago.setEstado("APROBADO");
        pagoRepo.save(pago);

        return true;
    }

}
