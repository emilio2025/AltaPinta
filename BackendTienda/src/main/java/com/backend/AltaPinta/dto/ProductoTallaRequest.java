package com.backend.AltaPinta.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductoTallaRequest {

    @NotNull(message = "La talla es requerida")
    private Long tallaId;

    @NotNull(message = "El stock es requerido")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    public Long getTallaId() { return tallaId; }
    public void setTallaId(Long tallaId) { this.tallaId = tallaId; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
