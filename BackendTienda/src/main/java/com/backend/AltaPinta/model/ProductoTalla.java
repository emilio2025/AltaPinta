package com.backend.AltaPinta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "producto_talla", uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "talla_id"}))
public class ProductoTalla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    @JsonIgnore
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "talla_id")
    private Talla talla;

    @NotNull(message = "El stock es requerido")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Talla getTalla() { return talla; }
    public void setTalla(Talla talla) { this.talla = talla; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
