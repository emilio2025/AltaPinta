package com.backend.AltaPinta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "categoria")
public class Categoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La columna admite 255, pero un nombre de categoria que no cabe en la
    // interfaz no sirve de nada: el limite util es mas corto que el fisico.
    @NotBlank(message = "El nombre de la categoría es requerido")
    @Size(max = 50, message = "El nombre de la categoría no puede pasar de 50 caracteres")
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
