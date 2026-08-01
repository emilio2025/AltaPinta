package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.CuentaTienda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaTiendaRepository
        extends JpaRepository<CuentaTienda, Long> {
}
