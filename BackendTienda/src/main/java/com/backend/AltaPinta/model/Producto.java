package com.backend.AltaPinta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto")
public class Producto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RF051: Consistencia de stock (locking optimista)
    @Version
    private Long version;

    @NotBlank(message = "El nombre del producto es requerido")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es requerido")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private Double precio;

    @Lob
    private String imagenUrl;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "tipo_prenda_id")
    private TipoPrenda tipoPrenda;

    // Deporte al que va dirigida la prenda. Admite null: los productos
    // cargados antes de introducir este campo no lo tienen asignado.
    @ManyToOne
    @JoinColumn(name = "deporte_id")
    private Deporte deporte;

    // Tallas disponibles para este producto, cada una con su propio stock.
    // Se gestionan explícitamente vía ProductoTallaRepository (ver ProductoController),
    // no por cascade, para evitar sorpresas al guardar/editar el producto base.
    @OneToMany(mappedBy = "producto")
    private List<ProductoTalla> tallasDisponibles = new ArrayList<>();

    // Galería de imágenes del producto (2-3 fotos mostrando la prenda completa desde
    // distintos ángulos). Se gestiona explícitamente vía ProductoImagenRepository
    // (ver ProductoController), igual que tallasDisponibles.
    @OneToMany(mappedBy = "producto")
    @OrderBy("orden ASC")
    private List<ProductoImagen> imagenes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public TipoPrenda getTipoPrenda() {
        return tipoPrenda;
    }

    public void setTipoPrenda(TipoPrenda tipoPrenda) {
        this.tipoPrenda = tipoPrenda;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public void setDeporte(Deporte deporte) {
        this.deporte = deporte;
    }

    public List<ProductoTalla> getTallasDisponibles() {
        return tallasDisponibles;
    }

    public void setTallasDisponibles(List<ProductoTalla> tallasDisponibles) {
        this.tallasDisponibles = tallasDisponibles;
    }

    public List<ProductoImagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ProductoImagen> imagenes) {
        this.imagenes = imagenes;
    }
}
