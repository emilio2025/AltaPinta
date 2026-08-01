package com.backend.AltaPinta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "direccion")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Cliente cliente;

    @NotBlank(message = "La etiqueta es requerida (ej. Casa, Trabajo)")
    private String etiqueta;

    @NotBlank(message = "La dirección completa es requerida")
    private String direccionCompleta;

    private String referencia;
    private String distrito;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public String getDireccionCompleta() { return direccionCompleta; }
    public void setDireccionCompleta(String direccionCompleta) { this.direccionCompleta = direccionCompleta; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
}
