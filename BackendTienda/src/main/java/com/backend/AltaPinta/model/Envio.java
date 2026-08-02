package com.backend.AltaPinta.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "envio")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lugar;
    @Column(precision = 12, scale = 2)
    private BigDecimal costo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }
}
