package com.backend.AltaPinta.dto;

import java.math.BigDecimal;

public class PedidoResponse {

    private Long pedidoId;
    private BigDecimal total;
    private String estado;
    private String tiempoEntrega;

    public PedidoResponse() {
    }

    public PedidoResponse(Long pedidoId, BigDecimal total, String estado, String tiempoEntrega) {
        this.pedidoId = pedidoId;
        this.total = total;
        this.estado = estado;
        this.tiempoEntrega = tiempoEntrega;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTiempoEntrega() {
        return tiempoEntrega;
    }

    public void setTiempoEntrega(String tiempoEntrega) {
        this.tiempoEntrega = tiempoEntrega;
    }
}
