package com.backend.AltaPinta.dto;

import java.math.BigDecimal;

public class CarritoItemDTO {

    private Long productoId;
    private String nombre;
    private BigDecimal precio;
    private Integer cantidad;
    private String imagenUrl;
    private Long tallaId;
    private String tallaNombre;

    public Long getTallaId() {
        return tallaId;
    }

    public void setTallaId(Long tallaId) {
        this.tallaId = tallaId;
    }

    public String getTallaNombre() {
        return tallaNombre;
    }

    public void setTallaNombre(String tallaNombre) {
        this.tallaNombre = tallaNombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
