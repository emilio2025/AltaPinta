package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findAllByOrderByFechaDesc();
}
