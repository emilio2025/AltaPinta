package com.backend.AltaPinta.model;

import jakarta.persistence.*;

/**
 * Deporte al que va dirigida una prenda (Running, Fútbol, Gym...).
 *
 * Es una dimensión distinta de la categoría: la categoría dice para quién es
 * la prenda (Mujer, Varón, Niños) y el deporte para qué se usa. Un mismo
 * producto tiene una de cada.
 */
@Entity
@Table(name = "deporte")
public class Deporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    /** Nombre del icono de PrimeIcons que usa la tienda para este deporte. */
    private String icono;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }
}
