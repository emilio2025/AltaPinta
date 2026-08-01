package com.backend.AltaPinta.dto;

public class PedidoConfirmarRequest {

    private String numeroTarjeta;
    private String cvv;
    private String fechaVencimiento;
    private Long envioId;

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Long getEnvioId() {
        return envioId;
    }

    public void setEnvioId(Long envioId) {
        this.envioId = envioId;
    }
}

