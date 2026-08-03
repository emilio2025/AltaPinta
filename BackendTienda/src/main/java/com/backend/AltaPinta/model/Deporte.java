package com.backend.AltaPinta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    // La columna ya es NOT NULL, pero eso solo lo comprueba la base de datos
    // al insertar: el error llega como una excepcion de integridad, no como
    // un mensaje que el cliente pueda entender. @NotBlank lo corta antes,
    // ademas de rechazar la cadena vacia, que para la columna es un valor
    // perfectamente valido.
    @NotBlank(message = "El nombre del deporte es requerido")
    @Size(max = 50, message = "El nombre del deporte no puede pasar de 50 caracteres")
    @Column(nullable = false, unique = true)
    private String nombre;

    /** Nombre del icono de PrimeIcons que usa la tienda para este deporte. */
    @Size(max = 50, message = "El nombre del icono no puede pasar de 50 caracteres")
    private String icono;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }
}
