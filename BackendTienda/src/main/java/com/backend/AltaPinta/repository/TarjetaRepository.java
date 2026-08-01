package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TarjetaRepository extends JpaRepository<Tarjeta, Long> {
    Optional<Tarjeta> findByNumero(String numero);
    boolean existsByNumero(String numero);
    Optional<Tarjeta> findByIdAndClienteId(Long id, Long clienteId);
    Optional<Tarjeta> findByNumeroAndCvvAndFechaVencimiento(
            String numero,
            String cvv,
            String fechaVencimiento
    );
    Optional<Tarjeta> findByNumeroAndClienteId(String numero, Long clienteId);
}
