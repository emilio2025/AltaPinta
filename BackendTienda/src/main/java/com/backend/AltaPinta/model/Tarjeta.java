package com.backend.AltaPinta.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

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

        @NotBlank(message = "El número de tarjeta es requerido")
        @Pattern(regexp = "\\d{13,19}",
                 message = "El número de tarjeta debe tener entre 13 y 19 dígitos, sin espacios")
        private String numero;

        @NotBlank(message = "El titular es requerido")
        @Size(max = 100, message = "El titular no puede pasar de 100 caracteres")
        private String titular;

        // Se aceptan MM/AA y MM/AAAA a proposito.
        //
        // El formulario actual pide MM/AAAA, pero en la base de datos hay
        // tarjetas antiguas guardadas como "12/25". Un patron que solo
        // admitiera cuatro digitos las rompería al confirmar un pedido: ahi
        // se descuenta el saldo y se guarda la tarjeta, y Hibernate valida
        // la entidad antes de escribirla. El cliente no podria comprar y el
        // error no señalaria a su tarjeta.
        @NotBlank(message = "La fecha de vencimiento es requerida")
        @Pattern(regexp = "(0[1-9]|1[0-2])/(\\d{2}|\\d{4})",
                 message = "El vencimiento debe tener el formato MM/AA o MM/AAAA")
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

        @NotNull(message = "El saldo es requerido")
        @PositiveOrZero(message = "El saldo no puede ser negativo")
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
