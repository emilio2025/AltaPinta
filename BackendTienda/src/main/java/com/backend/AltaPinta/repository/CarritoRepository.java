package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByClienteId(Long clienteId);
}

