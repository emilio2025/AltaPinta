package com.backend.AltaPinta.dto;

import java.util.List;

public class CarritoDTO {

    private Long id;
    private Double total;
    private List<CarritoItemDTO> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<CarritoItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CarritoItemDTO> items) {
        this.items = items;
    }
}
