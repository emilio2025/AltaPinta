package com.backend.AltaPinta.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "cuenta_tienda")
public class CuentaTienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 12, scale = 2)

    private BigDecimal saldo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}

