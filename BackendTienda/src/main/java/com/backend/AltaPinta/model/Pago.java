package com.backend.AltaPinta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Resultado de un intento de cobro (RF020).
 *
 * Se registra tanto el cobro aprobado como el rechazado. El rechazado es el
 * que de verdad importa: sin él no queda rastro de que alguien intento pagar
 * y no pudo, y el requisito RNF018 pide justamente eso, que el intento
 * fallido se registre sin que el stock se vea afectado.
 *
 * Ojo con el pedido: en un cobro rechazado NO hay pedido, porque la
 * transaccion que lo habria creado se revierte entera. Por eso pedido_id
 * admite nulos. Un pago sin pedido es exactamente un intento fallido.
 *
 * Nota historica: existio antes una entidad Pago que era un cascaron vacio
 * -ninguna referencia y cero filas- residuo del endpoint /pago/procesar que
 * se retiro por un fallo de control de acceso. Se elimino en la migracion V2
 * y esta es una implementacion nueva, no su rescate.
 */
@Entity
@Table(name = "pago")
public class Pago {

    /** Estados posibles del intento de cobro. */
    public static final String APROBADO = "APROBADO";
    public static final String RECHAZADO = "RECHAZADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nulo cuando el cobro fue rechazado: en ese caso el pedido nunca llego
     * a existir porque la transaccion se revirtio.
     */
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @JsonIgnore
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonIgnore
    private Cliente cliente;

    /**
     * Se guarda el identificador y no la entidad a proposito: el registro del
     * intento debe sobrevivir aunque la tarjeta se elimine despues, y no debe
     * arrastrar el numero de tarjeta a esta tabla.
     */
    @Column(name = "tarjeta_id")
    private Long tarjetaId;

    @NotNull
    @PositiveOrZero
    @Column(precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(length = 20)
    private String estado;

    /** Motivo del rechazo, tal como lo explico la logica de negocio. */
    @Column(length = 255)
    private String motivo;

    private LocalDateTime fecha;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Long getTarjetaId() { return tarjetaId; }
    public void setTarjetaId(Long tarjetaId) { this.tarjetaId = tarjetaId; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public boolean fueAprobado() { return APROBADO.equals(estado); }
}
