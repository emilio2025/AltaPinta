package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.ProductoTalla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoTallaRepository extends JpaRepository<ProductoTalla, Long> {
    List<ProductoTalla> findByProductoId(Long productoId);
    Optional<ProductoTalla> findByProductoIdAndTallaId(Long productoId, Long tallaId);
    void deleteByProductoId(Long productoId);
}
