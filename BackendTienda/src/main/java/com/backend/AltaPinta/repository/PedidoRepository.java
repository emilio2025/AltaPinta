package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {


    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByClienteCorreo(String correo);
    Optional<Pedido> findByIdAndClienteCorreo(Long id, String correo);

    // Total vendido
    @Query("""
        SELECT COALESCE(SUM(p.total),0)
        FROM Pedido p
        WHERE p.estado = 'PAGADO'
    """)
    Double totalVendido();

    // Ventas por día
    @Query("""
        SELECT COALESCE(SUM(p.total),0)
        FROM Pedido p
        WHERE p.estado = 'PAGADO'
        AND DATE(p.fecha) = :fecha
    """)
    Double ventasPorDia(LocalDate fecha);

    // Ventas por mes
    @Query("""
        SELECT COALESCE(SUM(p.total),0)
        FROM Pedido p
        WHERE p.estado = 'PAGADO'
        AND MONTH(p.fecha) = :mes
        AND YEAR(p.fecha) = :anio
    """)
    Double ventasPorMes(int mes, int anio);

    // Ventas por año
    @Query("""
        SELECT COALESCE(SUM(p.total),0)
        FROM Pedido p
        WHERE p.estado = 'PAGADO'
        AND YEAR(p.fecha) = :anio
    """)
    Double ventasPorAnio(int anio);

    // Cantidad de pedidos pagados
    @Query("""
        SELECT COUNT(p)
        FROM Pedido p
        WHERE p.estado = 'PAGADO'
    """)
    Long totalPedidosPagados();
}
