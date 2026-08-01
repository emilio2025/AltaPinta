package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Deporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeporteRepository extends JpaRepository<Deporte, Long> {
    Optional<Deporte> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
