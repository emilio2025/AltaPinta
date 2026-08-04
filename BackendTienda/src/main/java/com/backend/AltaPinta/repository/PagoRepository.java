package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Pago;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    /** Intentos de cobro de un cliente, del mas reciente al mas antiguo. */
    List<Pago> findByClienteIdOrderByFechaDesc(Long clienteId);

    /** Intentos de un estado concreto: APROBADO o RECHAZADO. */
    List<Pago> findByEstadoOrderByFechaDesc(String estado);

    long countByEstado(String estado);
}
