package com.backend.AltaPinta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "talla")
public class Talla {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Las tallas son etiquetas cortas: S, M, L, XL, 38, 40...
    @NotBlank(message = "El nombre de la talla es requerido")
    @Size(max = 10, message = "El nombre de la talla no puede pasar de 10 caracteres")
    private String nombre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
