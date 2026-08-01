package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
    List<PedidoDetalle> findByPedidoId(Long pedidoId);
}
