package com.backend.AltaPinta.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "tarjeta")
public class Tarjeta {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "cliente_id")
        @JsonIgnore
        @JsonBackReference
        private Cliente cliente;

        private String numero;
        private String titular;
        private String fechaVencimiento;

        // El CVV NO se guarda, a proposito.
        //
        // PCI DSS prohibe almacenar el codigo de verificacion despues de
        // autorizar el pago, y no admite excepciones: tampoco vale cifrarlo
        // ni guardar su hash. Su unica funcion es viajar hasta la pasarela
        // en el momento del cobro y desaparecer.
        //
        // Si en el futuro se integra una pasarela real, el CVV debe llegar
        // como campo de la peticion y usarse en memoria, nunca persistirse.

        @Column(precision = 12, scale = 2)
        private BigDecimal saldo;
        private Boolean activa = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
