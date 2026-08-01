package com.backend.AltaPinta.dto;

public class PedidoResponse {

    private Long pedidoId;
    private Double total;
    private String estado;
    private String tiempoEntrega;

    public PedidoResponse() {
    }

    public PedidoResponse(Long pedidoId, Double total, String estado, String tiempoEntrega) {
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

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
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
