package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {
    List<ProductoImagen> findByProductoIdOrderByOrden(Long productoId);
    void deleteByProductoId(Long productoId);
}
