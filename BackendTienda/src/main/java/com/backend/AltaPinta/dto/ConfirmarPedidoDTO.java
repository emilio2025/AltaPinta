package com.backend.AltaPinta.dto;

import jakarta.validation.constraints.NotNull;

public class ConfirmarPedidoDTO {

    /** Opcional a proposito: sin envio, el pedido es un recojo en tienda. */
    private Long envioId;

    /**
     * Sin tarjeta no hay con que cobrar. Antes, un cuerpo vacio llegaba hasta
     * PedidoService y salia como "La tarjeta no pertenece al cliente", que
     * apunta a un problema de permisos y no a un campo que falta.
     */
    @NotNull(message = "Debes elegir una tarjeta para pagar")
    private Long tarjetaId;

    public Long getEnvioId() {
        return envioId;
    }

    public void setEnvioId(Long envioId) {
        this.envioId = envioId;
    }

    public Long getTarjetaId() {
        return tarjetaId;
    }

    public void setTarjetaId(Long tarjetaId) {
        this.tarjetaId = tarjetaId;
    }
}
