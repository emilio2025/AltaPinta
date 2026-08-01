package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByClienteCorreo(String correo);
    Optional<Direccion> findByIdAndClienteCorreo(Long id, String correo);
}
