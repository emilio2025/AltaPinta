package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.model.Pedido;
import com.backend.AltaPinta.model.Rol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.backend.AltaPinta.Importes.assertImporte;
import static com.backend.AltaPinta.Importes.imp;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas de las consultas que alimentan el panel de administracion.
 *
 * Se ejecutan sobre una base de datos H2 en memoria que se crea y se
 * destruye con cada clase, asi que no tocan la base de datos de desarrollo.
 *
 * H2 corre en modo de compatibilidad con MySQL porque las consultas usan
 * DATE(), MONTH() y YEAR(): sin ese modo el dialecto no las resuelve igual
 * y las pruebas no probarian lo que corre en produccion.
 *
 * Todas las consultas filtran por estado = 'PAGADO', asi que cada bloque
 * incluye un pedido CANCELADO para comprobar que no se cuela en las cifras.
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:reportes;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PedidoRepositoryReportesTest {

    @Autowired private PedidoRepository pedidoRepo;
    @Autowired private ClienteRepository clienteRepo;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setNombre("Cliente de prueba");
        cliente.setCorreo("cliente@unamba.edu.pe");
        cliente.setPassword("irrelevante");
        cliente.setRol(Rol.USER);
        cliente = clienteRepo.save(cliente);
    }

    /** Crea y guarda un pedido con la fecha, el total y el estado indicados. */
    private Pedido pedido(String estado, double total, LocalDateTime fecha) {
        // El fixture sigue recibiendo double por comodidad; el importe se
        // convierte aqui para no repetir la conversion en cada llamada.
        Pedido p = new Pedido();
        p.setCliente(cliente);
        p.setEstado(estado);
        p.setTotal(BigDecimal.valueOf(total));
        p.setFecha(fecha);
        return pedidoRepo.save(p);
    }

    // ============================================================
    @Nested
    @DisplayName("totalVendido")
    class TotalVendido {

        @Test
        @DisplayName("Suma solo los pedidos pagados")
        void sumaSoloPagados() {
            pedido("PAGADO", 100.0, LocalDateTime.now());
            pedido("PAGADO", 215.5, LocalDateTime.now());
            pedido("CANCELADO", 999.0, LocalDateTime.now());

            assertImporte("315.5", pedidoRepo.totalVendido());
        }

        @Test
        @DisplayName("Sin ventas devuelve 0, no null")
        void sinVentasDevuelveCero() {
            // El COALESCE de la consulta existe justamente para esto: si
            // devolviera null, el panel de administracion reventaria.
            assertImporte("0.0", pedidoRepo.totalVendido());
        }

        @Test
        @DisplayName("Con solo pedidos cancelados devuelve 0")
        void soloCanceladosDevuelveCero() {
            pedido("CANCELADO", 500.0, LocalDateTime.now());

            assertImporte("0.0", pedidoRepo.totalVendido());
        }
    }

    // ============================================================
    @Nested
    @DisplayName("totalPedidosPagados")
    class TotalPedidosPagados {

        @Test
        @DisplayName("Cuenta solo los pedidos pagados")
        void cuentaSoloPagados() {
            pedido("PAGADO", 10.0, LocalDateTime.now());
            pedido("PAGADO", 20.0, LocalDateTime.now());
            pedido("CANCELADO", 30.0, LocalDateTime.now());
            pedido("RECHAZADO", 40.0, LocalDateTime.now());

            assertEquals(2L, pedidoRepo.totalPedidosPagados());
        }

        @Test
        @DisplayName("Sin pedidos devuelve 0")
        void sinPedidosDevuelveCero() {
            assertEquals(0L, pedidoRepo.totalPedidosPagados());
        }
    }

    // ============================================================
    @Nested
    @DisplayName("ventasPorDia")
    class VentasPorDia {

        @Test
        @DisplayName("Suma solo lo vendido ese dia, sin importar la hora")
        void sumaSoloEseDia() {
            LocalDate hoy = LocalDate.of(2026, 3, 15);

            // Mismo dia, horas distintas: los dos deben contar.
            pedido("PAGADO", 100.0, hoy.atTime(0, 1));
            pedido("PAGADO", 50.0, hoy.atTime(23, 59));
            // Dias contiguos: no deben contar.
            pedido("PAGADO", 777.0, hoy.minusDays(1).atTime(23, 59));
            pedido("PAGADO", 888.0, hoy.plusDays(1).atTime(0, 1));

            assertImporte("150.0", pedidoRepo.ventasPorDia(hoy));
        }

        @Test
        @DisplayName("Excluye los pedidos cancelados de ese dia")
        void excluyeCancelados() {
            LocalDate hoy = LocalDate.of(2026, 3, 15);
            pedido("PAGADO", 100.0, hoy.atTime(12, 0));
            pedido("CANCELADO", 900.0, hoy.atTime(12, 0));

            assertImporte("100.0", pedidoRepo.ventasPorDia(hoy));
        }

        @Test
        @DisplayName("Un dia sin ventas devuelve 0")
        void diaSinVentas() {
            pedido("PAGADO", 100.0, LocalDateTime.of(2026, 3, 15, 12, 0));

            assertImporte("0.0", pedidoRepo.ventasPorDia(LocalDate.of(2026, 3, 16)));
        }

        @Test
        @DisplayName("Los limites exactos de medianoche caen en el dia correcto")
        void limitesDeMedianoche() {
            LocalDate dia = LocalDate.of(2026, 3, 15);

            // El rango es [00:00 del dia, 00:00 del dia siguiente): el primer
            // instante cuenta y el ultimo pertenece ya al dia siguiente.
            pedido("PAGADO", 100.0, dia.atStartOfDay());
            pedido("PAGADO", 500.0, dia.plusDays(1).atStartOfDay());

            assertImporte("100.0", pedidoRepo.ventasPorDia(dia));
            assertImporte("500.0", pedidoRepo.ventasPorDia(dia.plusDays(1)));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("ventasPorMes")
    class VentasPorMes {

        @Test
        @DisplayName("Suma solo lo vendido en ese mes y año")
        void sumaSoloEseMes() {
            pedido("PAGADO", 100.0, LocalDateTime.of(2026, 3, 1, 0, 0));
            pedido("PAGADO", 200.0, LocalDateTime.of(2026, 3, 31, 23, 59));
            // Mes anterior y posterior
            pedido("PAGADO", 777.0, LocalDateTime.of(2026, 2, 28, 12, 0));
            pedido("PAGADO", 888.0, LocalDateTime.of(2026, 4, 1, 12, 0));
            // Mismo mes pero de otro año: el filtro de año debe excluirlo.
            pedido("PAGADO", 999.0, LocalDateTime.of(2025, 3, 15, 12, 0));

            assertImporte("300.0", pedidoRepo.ventasPorMes(3, 2026));
        }

        @Test
        @DisplayName("Excluye los cancelados")
        void excluyeCancelados() {
            pedido("PAGADO", 100.0, LocalDateTime.of(2026, 3, 10, 12, 0));
            pedido("CANCELADO", 900.0, LocalDateTime.of(2026, 3, 10, 12, 0));

            assertImporte("100.0", pedidoRepo.ventasPorMes(3, 2026));
        }

        @Test
        @DisplayName("Un mes sin ventas devuelve 0")
        void mesSinVentas() {
            assertImporte("0.0", pedidoRepo.ventasPorMes(7, 2026));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("ventasPorAnio")
    class VentasPorAnio {

        @Test
        @DisplayName("Suma todos los meses del año indicado")
        void sumaTodoElAnio() {
            pedido("PAGADO", 100.0, LocalDateTime.of(2026, 1, 1, 0, 0));
            pedido("PAGADO", 200.0, LocalDateTime.of(2026, 6, 15, 12, 0));
            pedido("PAGADO", 300.0, LocalDateTime.of(2026, 12, 31, 23, 59));
            // Años contiguos
            pedido("PAGADO", 777.0, LocalDateTime.of(2025, 12, 31, 23, 59));
            pedido("PAGADO", 888.0, LocalDateTime.of(2027, 1, 1, 0, 1));

            assertImporte("600.0", pedidoRepo.ventasPorAnio(2026));
        }

        @Test
        @DisplayName("Excluye los cancelados")
        void excluyeCancelados() {
            pedido("PAGADO", 100.0, LocalDateTime.of(2026, 5, 10, 12, 0));
            pedido("CANCELADO", 900.0, LocalDateTime.of(2026, 5, 10, 12, 0));

            assertImporte("100.0", pedidoRepo.ventasPorAnio(2026));
        }

        @Test
        @DisplayName("Un año sin ventas devuelve 0")
        void anioSinVentas() {
            assertImporte("0.0", pedidoRepo.ventasPorAnio(2020));
        }
    }
}
