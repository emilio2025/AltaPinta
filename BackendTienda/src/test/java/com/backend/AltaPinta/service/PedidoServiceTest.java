package com.backend.AltaPinta.service;

import com.backend.AltaPinta.dto.ConfirmarPedidoDTO;
import com.backend.AltaPinta.dto.PedidoResponse;
import com.backend.AltaPinta.model.*;
import com.backend.AltaPinta.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.backend.AltaPinta.Importes.assertImporte;
import static com.backend.AltaPinta.Importes.imp;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas del flujo de compra: confirmacion y cancelacion de pedidos.
 *
 * Todo se prueba con dobles (Mockito), sin base de datos: lo que interesa
 * aqui son las reglas de negocio y, sobre todo, que un pedido invalido no
 * deje efectos a medias (stock descontado sin cobrar, o al reves).
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepo;
    @Mock private PedidoDetalleRepository detalleRepo;
    @Mock private ProductoTallaRepository productoTallaRepo;
    @Mock private ClienteRepository clienteRepo;
    @Mock private EnvioRepository envioRepo;
    @Mock private CarritoItemRepository carritoItemRepo;
    @Mock private CuentaTiendaRepository cuentaTiendaRepo;
    @Mock private TarjetaRepository tarjetaRepo;
    @Mock private EmailService emailService;
    @Mock private FacturaRepository facturaRepository;
    @Mock private FacturaPdfService facturaPdfService;
    @Mock private PagoService pagoService;

    @InjectMocks private PedidoService pedidoService;

    private static final String CORREO = "cliente@unamba.edu.pe";

    private Cliente cliente;
    private Producto producto;
    private Talla talla;
    private ProductoTalla stockTalla;
    private CarritoItem itemCarrito;
    private Tarjeta tarjeta;
    private Envio envio;
    private CuentaTienda cuenta;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCorreo(CORREO);

        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Polo deportivo");
        producto.setPrecio(imp("100.0"));

        talla = new Talla();
        talla.setId(5L);
        talla.setNombre("M");

        stockTalla = new ProductoTalla();
        stockTalla.setProducto(producto);
        stockTalla.setTalla(talla);
        stockTalla.setStock(10);

        itemCarrito = new CarritoItem();
        itemCarrito.setProducto(producto);
        itemCarrito.setTalla(talla);
        itemCarrito.setCantidad(2);          // subtotal = 2 x 100 = 200

        tarjeta = new Tarjeta();
        tarjeta.setId(7L);
        tarjeta.setSaldo(imp("500.0"));

        envio = new Envio();
        envio.setId(3L);
        envio.setLugar("Lima");
        envio.setCosto(imp("15.0"));                // total = 200 + 15 = 215

        cuenta = new CuentaTienda();
        cuenta.setId(1L);
        cuenta.setSaldo(imp("1000.0"));
    }

    private ConfirmarPedidoDTO dtoConEnvio() {
        ConfirmarPedidoDTO dto = new ConfirmarPedidoDTO();
        dto.setEnvioId(3L);
        dto.setTarjetaId(7L);
        return dto;
    }

    /** Deja los dobles listos para que la compra llegue hasta el final. */
    private void escenarioCompraCorrecta() {
        when(clienteRepo.findByCorreoBloqueando(CORREO)).thenReturn(Optional.of(cliente));
        when(carritoItemRepo.findByCarritoClienteId(1L)).thenReturn(List.of(itemCarrito));
        when(productoTallaRepo.findByProductoIdAndTallaId(10L, 5L)).thenReturn(Optional.of(stockTalla));
        when(envioRepo.findById(3L)).thenReturn(Optional.of(envio));
        when(tarjetaRepo.findByIdAndClienteId(7L, 1L)).thenReturn(Optional.of(tarjeta));
        when(cuentaTiendaRepo.findById(1L)).thenReturn(Optional.of(cuenta));
    }

    // ============================================================
    @Nested
    @DisplayName("confirmarPedido")
    class ConfirmarPedido {

        @Test
        @DisplayName("Los importes con centimos salen exactos, sin arrastre de coma flotante")
        void importesExactosConCentimos() {
            // Con double este escenario daba 59.970000000000006 y el saldo
            // quedaba en 440.02999999999997. Son los valores que acababan
            // impresos en la factura y guardados en la tarjeta.
            producto.setPrecio(imp("19.99"));
            itemCarrito.setCantidad(3);          // 19.99 x 3 = 59.97
            envio.setCosto(imp("10.01"));        // total = 69.98

            escenarioCompraCorrecta();

            PedidoResponse respuesta = pedidoService.confirmarPedido(CORREO, dtoConEnvio());

            assertImporte("69.98", respuesta.getTotal());
            assertImporte("430.02", tarjeta.getSaldo(), "500 - 69.98");
            assertImporte("1069.98", cuenta.getSaldo(), "1000 + 69.98");
        }

        @Test
        @DisplayName("Un precio con muchos decimales no se redondea por el camino")
        void sumaDeTercios() {
            // 0.10 no tiene representacion exacta en binario: sumarlo diez
            // veces con double da 0.9999999999999999, no 1.
            producto.setPrecio(imp("0.10"));
            itemCarrito.setCantidad(10);
            envio.setCosto(BigDecimal.ZERO);

            escenarioCompraCorrecta();

            PedidoResponse respuesta = pedidoService.confirmarPedido(CORREO, dtoConEnvio());

            assertImporte("1.00", respuesta.getTotal());
        }

        @Test
        @DisplayName("RF045: bloquea la fila del cliente antes de tocar nada")
        void bloqueaAlCliente() {
            escenarioCompraCorrecta();

            pedidoService.confirmarPedido(CORREO, dtoConEnvio());

            // Lo que impide el doble pago por doble clic es este bloqueo en
            // la base de datos. Si alguien lo cambia por la busqueda normal,
            // dos peticiones simultaneas del mismo cliente volverian a
            // colarse: la proteccion se pierde sin que falle nada mas.
            verify(clienteRepo).findByCorreoBloqueando(CORREO);
            verify(clienteRepo, never()).findByCorreo(anyString());
        }

        @Test
        @DisplayName("Compra correcta: cobra, descuenta stock, abona a la tienda y vacia el carrito")
        void compraCorrecta() {
            escenarioCompraCorrecta();

            PedidoResponse respuesta = pedidoService.confirmarPedido(CORREO, dtoConEnvio());

            // Total = subtotal (200) + envio (15)
            assertImporte("215.0", respuesta.getTotal());
            assertEquals("PAGADO", respuesta.getEstado());
            assertEquals("24 a 48 horas", respuesta.getTiempoEntrega());

            // Efectos sobre el estado del sistema
            assertImporte("285.0", tarjeta.getSaldo(), "500 - 215");
            assertEquals(8, stockTalla.getStock(), "10 - 2 unidades");
            assertImporte("1215.0", cuenta.getSaldo(), "1000 + 215");

            verify(carritoItemRepo).deleteByCarritoClienteId(1L);
            verify(pedidoRepo).save(any(Pedido.class));
            verify(detalleRepo).save(any(PedidoDetalle.class));
        }

        @Test
        @DisplayName("Sin envio: no cobra costo de envio y marca recojo en tienda")
        void sinEnvio() {
            when(clienteRepo.findByCorreoBloqueando(CORREO)).thenReturn(Optional.of(cliente));
            when(carritoItemRepo.findByCarritoClienteId(1L)).thenReturn(List.of(itemCarrito));
            when(productoTallaRepo.findByProductoIdAndTallaId(10L, 5L)).thenReturn(Optional.of(stockTalla));
            when(tarjetaRepo.findByIdAndClienteId(7L, 1L)).thenReturn(Optional.of(tarjeta));
            when(cuentaTiendaRepo.findById(1L)).thenReturn(Optional.of(cuenta));

            ConfirmarPedidoDTO dto = new ConfirmarPedidoDTO();
            dto.setEnvioId(null);
            dto.setTarjetaId(7L);

            PedidoResponse respuesta = pedidoService.confirmarPedido(CORREO, dto);

            assertImporte("200.0", respuesta.getTotal(), "solo el subtotal");
            assertEquals("Recojo en tienda", respuesta.getTiempoEntrega());
            verifyNoInteractions(envioRepo);
        }

        @Test
        @DisplayName("Carrito vacio: no se crea pedido")
        void carritoVacio() {
            when(clienteRepo.findByCorreoBloqueando(CORREO)).thenReturn(Optional.of(cliente));
            when(carritoItemRepo.findByCarritoClienteId(1L)).thenReturn(List.of());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> pedidoService.confirmarPedido(CORREO, dtoConEnvio()));

            assertEquals("El carrito está vacío", ex.getMessage());
            verify(pedidoRepo, never()).save(any());
        }

        @Test
        @DisplayName("Cliente inexistente: falla antes de tocar nada")
        void clienteInexistente() {
            when(clienteRepo.findByCorreoBloqueando("fantasma@ejemplo.com")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> pedidoService.confirmarPedido("fantasma@ejemplo.com", dtoConEnvio()));

            verify(pedidoRepo, never()).save(any());
            verifyNoInteractions(carritoItemRepo);
        }

        @Test
        @DisplayName("Stock insuficiente: no cobra la tarjeta ni descuenta stock")
        void stockInsuficiente() {
            itemCarrito.setCantidad(20);      // pide 20, solo hay 10
            when(clienteRepo.findByCorreoBloqueando(CORREO)).thenReturn(Optional.of(cliente));
            when(carritoItemRepo.findByCarritoClienteId(1L)).thenReturn(List.of(itemCarrito));
            when(productoTallaRepo.findByProductoIdAndTallaId(10L, 5L)).thenReturn(Optional.of(stockTalla));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> pedidoService.confirmarPedido(CORREO, dtoConEnvio()));

            assertTrue(ex.getMessage().contains("Stock insuficiente"), ex.getMessage());
            assertImporte("500.0", tarjeta.getSaldo(), "la tarjeta no debe tocarse");
            assertEquals(10, stockTalla.getStock(), "el stock no debe tocarse");
            verify(pedidoRepo, never()).save(any());
            verify(carritoItemRepo, never()).deleteByCarritoClienteId(any());
        }

        @Test
        @DisplayName("Talla ya no disponible: falla con mensaje claro")
        void tallaNoDisponible() {
            when(clienteRepo.findByCorreoBloqueando(CORREO)).thenReturn(Optional.of(cliente));
            when(carritoItemRepo.findByCarritoClienteId(1L)).thenReturn(List.of(itemCarrito));
            when(productoTallaRepo.findByProductoIdAndTallaId(10L, 5L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> pedidoService.confirmarPedido(CORREO, dtoConEnvio()));

            assertTrue(ex.getMessage().contains("ya no está disponible"), ex.getMessage());
            verify(pedidoRepo, never()).save(any());
        }

        @Test
        @DisplayName("Saldo insuficiente: no descuenta stock ni vacia el carrito")
        void saldoInsuficiente() {
            tarjeta.setSaldo(imp("10.0"));           // hacen falta 215
            // No se usa el escenario completo a proposito: la ejecucion falla
            // en la comprobacion de saldo y nunca llega a la cuenta de la tienda.
            when(clienteRepo.findByCorreoBloqueando(CORREO)).thenReturn(Optional.of(cliente));
            when(carritoItemRepo.findByCarritoClienteId(1L)).thenReturn(List.of(itemCarrito));
            when(productoTallaRepo.findByProductoIdAndTallaId(10L, 5L)).thenReturn(Optional.of(stockTalla));
            when(envioRepo.findById(3L)).thenReturn(Optional.of(envio));
            when(tarjetaRepo.findByIdAndClienteId(7L, 1L)).thenReturn(Optional.of(tarjeta));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> pedidoService.confirmarPedido(CORREO, dtoConEnvio()));

            assertEquals("Saldo insuficiente en la tarjeta", ex.getMessage());
            assertEquals(10, stockTalla.getStock(), "el stock no debe descontarse");
            assertImporte("1000.0", cuenta.getSaldo(), "la tienda no debe cobrar");
            verify(carritoItemRepo, never()).deleteByCarritoClienteId(any());
            verify(pedidoRepo, never()).save(any());
        }

        @Test
        @DisplayName("Tarjeta de otro cliente: se rechaza")
        void tarjetaAjena() {
            when(clienteRepo.findByCorreoBloqueando(CORREO)).thenReturn(Optional.of(cliente));
            when(carritoItemRepo.findByCarritoClienteId(1L)).thenReturn(List.of(itemCarrito));
            when(productoTallaRepo.findByProductoIdAndTallaId(10L, 5L)).thenReturn(Optional.of(stockTalla));
            when(envioRepo.findById(3L)).thenReturn(Optional.of(envio));
            when(tarjetaRepo.findByIdAndClienteId(7L, 1L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> pedidoService.confirmarPedido(CORREO, dtoConEnvio()));

            assertEquals("La tarjeta no pertenece al cliente", ex.getMessage());
            verify(pedidoRepo, never()).save(any());
        }

        @Test
        @DisplayName("RF044: si falla la factura o el correo, la compra sigue siendo valida")
        void fallaFacturaPeroLaCompraSeMantiene() {
            escenarioCompraCorrecta();
            when(facturaPdfService.generarFactura(any(), any()))
                    .thenThrow(new RuntimeException("disco lleno"));

            PedidoResponse respuesta = pedidoService.confirmarPedido(CORREO, dtoConEnvio());

            // El pedido se confirma igual: el PDF y el correo son secundarios.
            assertEquals("PAGADO", respuesta.getEstado());
            assertImporte("285.0", tarjeta.getSaldo());
            assertEquals(8, stockTalla.getStock());
            verify(carritoItemRepo).deleteByCarritoClienteId(1L);
        }

        @Test
        @DisplayName("Varias lineas de carrito: suma correcta y descuento por linea")
        void variasLineas() {
            Producto zapatilla = new Producto();
            zapatilla.setId(11L);
            zapatilla.setNombre("Zapatilla running");
            zapatilla.setPrecio(imp("250.0"));

            Talla talla42 = new Talla();
            talla42.setId(6L);
            talla42.setNombre("42");

            ProductoTalla stockZapatilla = new ProductoTalla();
            stockZapatilla.setProducto(zapatilla);
            stockZapatilla.setTalla(talla42);
            stockZapatilla.setStock(4);

            CarritoItem item2 = new CarritoItem();
            item2.setProducto(zapatilla);
            item2.setTalla(talla42);
            item2.setCantidad(1);

            when(clienteRepo.findByCorreoBloqueando(CORREO)).thenReturn(Optional.of(cliente));
            when(carritoItemRepo.findByCarritoClienteId(1L)).thenReturn(List.of(itemCarrito, item2));
            when(productoTallaRepo.findByProductoIdAndTallaId(10L, 5L)).thenReturn(Optional.of(stockTalla));
            when(productoTallaRepo.findByProductoIdAndTallaId(11L, 6L)).thenReturn(Optional.of(stockZapatilla));
            when(envioRepo.findById(3L)).thenReturn(Optional.of(envio));
            when(tarjetaRepo.findByIdAndClienteId(7L, 1L)).thenReturn(Optional.of(tarjeta));
            when(cuentaTiendaRepo.findById(1L)).thenReturn(Optional.of(cuenta));

            PedidoResponse respuesta = pedidoService.confirmarPedido(CORREO, dtoConEnvio());

            // (2 x 100) + (1 x 250) + 15 de envio
            assertImporte("465.0", respuesta.getTotal());
            assertEquals(8, stockTalla.getStock());
            assertEquals(3, stockZapatilla.getStock());
            verify(detalleRepo, times(2)).save(any(PedidoDetalle.class));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("cancelarPedido (RF043)")
    class CancelarPedido {

        private Pedido pedidoPagado;
        private PedidoDetalle detalle;

        @BeforeEach
        void prepararPedido() {
            pedidoPagado = new Pedido();
            pedidoPagado.setId(50L);
            pedidoPagado.setCliente(cliente);
            pedidoPagado.setEnvio(envio);
            pedidoPagado.setTotal(imp("215.0"));
            pedidoPagado.setEstado("PAGADO");

            detalle = new PedidoDetalle();
            detalle.setPedido(pedidoPagado);
            detalle.setProducto(producto);
            detalle.setTalla(talla);
            detalle.setCantidad(2);
            detalle.setPrecioUnitario(imp("100.0"));
        }

        @Test
        @DisplayName("Cancelacion valida: devuelve stock, revierte el ingreso y avisa al cliente")
        void cancelacionValida() {
            when(pedidoRepo.findByIdAndClienteCorreo(50L, CORREO)).thenReturn(Optional.of(pedidoPagado));
            when(detalleRepo.findByPedidoId(50L)).thenReturn(List.of(detalle));
            when(productoTallaRepo.findByProductoIdAndTallaId(10L, 5L)).thenReturn(Optional.of(stockTalla));
            when(cuentaTiendaRepo.findById(1L)).thenReturn(Optional.of(cuenta));

            PedidoResponse respuesta = pedidoService.cancelarPedido(CORREO, 50L);

            assertEquals("CANCELADO", respuesta.getEstado());
            assertEquals(12, stockTalla.getStock(), "10 + 2 devueltas");
            assertImporte("785.0", cuenta.getSaldo(), "1000 - 215");
            verify(emailService).sendPedidoEstado(CORREO, pedidoPagado);
            verify(pedidoRepo).save(pedidoPagado);
        }

        @Test
        @DisplayName("Un pedido ya cancelado no se puede cancelar otra vez")
        void noSeCancelaDosVeces() {
            pedidoPagado.setEstado("CANCELADO");
            when(pedidoRepo.findByIdAndClienteCorreo(50L, CORREO)).thenReturn(Optional.of(pedidoPagado));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> pedidoService.cancelarPedido(CORREO, 50L));

            assertEquals("El pedido ya no puede cancelarse", ex.getMessage());
            assertEquals(10, stockTalla.getStock(), "no debe devolver stock dos veces");
            assertImporte("1000.0", cuenta.getSaldo());
        }

        @Test
        @DisplayName("No se puede cancelar el pedido de otro cliente")
        void noCancelaPedidoAjeno() {
            when(pedidoRepo.findByIdAndClienteCorreo(50L, "otro@ejemplo.com"))
                    .thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> pedidoService.cancelarPedido("otro@ejemplo.com", 50L));

            assertEquals("Pedido no encontrado", ex.getMessage());
            verify(pedidoRepo, never()).save(any());
        }
    }

    // ============================================================
    @Nested
    @DisplayName("calcularTiempoEntrega")
    class TiempoEntrega {

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({
                "Lima,        24 a 48 horas",
                "LIMA,        24 a 48 horas",
                "lima,        24 a 48 horas",
                "Callao,      48 horas",
                "CALLAO,      48 horas",
                "Cusco,       3 a 5 días hábiles",
                "Abancay,     3 a 5 días hábiles"
        })
        @DisplayName("Devuelve el plazo segun el destino, sin importar mayusculas")
        void plazoPorLugar(String lugar, String esperado) {
            assertEquals(esperado, pedidoService.calcularTiempoEntrega(lugar));
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Lugar nulo devuelve 'No definido'")
        void lugarNulo(String lugar) {
            assertEquals("No definido", pedidoService.calcularTiempoEntrega(lugar));
        }
    }
}
