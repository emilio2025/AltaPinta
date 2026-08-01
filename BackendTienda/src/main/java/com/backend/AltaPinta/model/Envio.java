package com.backend.AltaPinta.model;

import jakarta.persistence.*;

@Entity
@Table(name = "envio")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lugar;
    private Double costo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }
}
