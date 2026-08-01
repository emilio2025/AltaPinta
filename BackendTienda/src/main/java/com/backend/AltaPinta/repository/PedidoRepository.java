package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    // Se consulta por rango [inicio, fin) en vez de con DATE(p.fecha):
    //   - DATE() no es una función estándar de HQL, así que Hibernate la
    //     pasaba tal cual al SQL y solo funcionaba en MySQL.
    //   - Aplicar una función sobre la columna impide usar un índice sobre
    //     fecha; comparar por rango sí lo aprovecha.
    @Query("""
        SELECT COALESCE(SUM(p.total),0)
        FROM Pedido p
        WHERE p.estado = 'PAGADO'
        AND p.fecha >= :inicio
        AND p.fecha < :fin
    """)
    Double ventasEntre(LocalDateTime inicio, LocalDateTime fin);

    /** Ventas de un día completo, de las 00:00 inclusive a las 00:00 del día siguiente. */
    default Double ventasPorDia(LocalDate fecha) {
        return ventasEntre(fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
    }

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
