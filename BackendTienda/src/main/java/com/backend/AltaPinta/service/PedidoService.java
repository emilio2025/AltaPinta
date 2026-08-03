package com.backend.AltaPinta.service;

import com.backend.AltaPinta.dto.ConfirmarPedidoDTO;
import com.backend.AltaPinta.dto.PedidoResponse;
import com.backend.AltaPinta.model.*;
import com.backend.AltaPinta.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepo;
    private final PedidoDetalleRepository detalleRepo;
    private final ProductoTallaRepository productoTallaRepo;
    private final ClienteRepository clienteRepo;
    private final EnvioRepository envioRepo;
    private final CarritoItemRepository carritoItemRepo;
    private final CuentaTiendaRepository cuentaTiendaRepo;
    private final TarjetaRepository tarjetaRepo;
    private final EmailService emailService;
    private final FacturaRepository facturaRepository;
    private final FacturaPdfService facturaPdfService;

    public PedidoService(
            PedidoRepository pedidoRepo,
            PedidoDetalleRepository detalleRepo,
            ProductoTallaRepository productoTallaRepo,
            ClienteRepository clienteRepo,
            EnvioRepository envioRepo,
            CarritoItemRepository carritoItemRepo,
            CuentaTiendaRepository cuentaTiendaRepo,
            TarjetaRepository tarjetaRepo,
            EmailService emailService,
            FacturaRepository facturaRepository,
            FacturaPdfService facturaPdfService
    ) {
        this.pedidoRepo = pedidoRepo;
        this.detalleRepo = detalleRepo;
        this.productoTallaRepo = productoTallaRepo;
        this.clienteRepo = clienteRepo;
        this.envioRepo = envioRepo;
        this.carritoItemRepo = carritoItemRepo;
        this.cuentaTiendaRepo = cuentaTiendaRepo;
        this.tarjetaRepo = tarjetaRepo;
        this.emailService = emailService;
        this.facturaRepository = facturaRepository;
        this.facturaPdfService = facturaPdfService;
    }

    public PedidoResponse confirmarPedido(String correoCliente, ConfirmarPedidoDTO dto) {

        // RF045: un mismo cliente no puede confirmar dos pedidos a la vez.
        //
        // La fila del cliente se bloquea en la base de datos hasta que
        // termina la transacción, así que un segundo intento simultáneo
        // —el doble clic en "pagar"— espera aquí y, cuando entra, ya ve el
        // carrito vacío y no puede duplicar la compra.
        //
        // Esto sustituye a un synchronized sobre un mapa en memoria, que
        // solo protegía dentro de una instancia y, al tomarse dentro de la
        // transacción, dejaba al segundo hilo con su instantánea anterior.
        Cliente cliente = clienteRepo.findByCorreoBloqueando(correoCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        return confirmarPedidoInterno(cliente, dto);
    }

    private PedidoResponse confirmarPedidoInterno(Cliente cliente, ConfirmarPedidoDTO dto) {

        List<CarritoItem> itemsCarrito = carritoItemRepo.findByCarritoClienteId(cliente.getId());

        if (itemsCarrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        // Validar stock (por talla) y calcular subtotal
        for (CarritoItem item : itemsCarrito) {
            Producto producto = item.getProducto();

            ProductoTalla stockTalla = productoTallaRepo
                    .findByProductoIdAndTallaId(producto.getId(), item.getTalla().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "La talla " + item.getTalla().getNombre() + " ya no está disponible para " + producto.getNombre()));

            if (stockTalla.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente: " + producto.getNombre() + " (talla " + item.getTalla().getNombre() + ")");
            }

            subtotal = subtotal.add(
                    producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
        }

        // Envío (opcional)
        Envio envio = null;
        BigDecimal costoEnvio = BigDecimal.ZERO;
        if (dto.getEnvioId() != null) {
            envio = envioRepo.findById(dto.getEnvioId())
                    .orElseThrow(() -> new RuntimeException("Envío inválido"));
            costoEnvio = envio.getCosto();
        }

        BigDecimal total = subtotal.add(costoEnvio);

        // Validar tarjeta y descontar saldo
        Tarjeta tarjeta = tarjetaRepo
                .findByIdAndClienteId(dto.getTarjetaId(), cliente.getId())
                .orElseThrow(() -> new RuntimeException("La tarjeta no pertenece al cliente"));

        // compareTo y no equals: equals distingue 10.0 de 10.00, compareTo no.
        if (tarjeta.getSaldo().compareTo(total) < 0) {
            throw new RuntimeException("Saldo insuficiente en la tarjeta");
        }

        tarjeta.setSaldo(tarjeta.getSaldo().subtract(total));
        tarjetaRepo.save(tarjeta);

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEnvio(envio);
        pedido.setTotal(total);
        pedido.setEstado("PAGADO");
        pedido.setFecha(LocalDateTime.now());
        pedidoRepo.save(pedido);

        // Crear detalles de pedido y descontar stock de la talla comprada
        List<PedidoDetalle> detalles = new ArrayList<>();
        for (CarritoItem item : itemsCarrito) {
            Producto producto = item.getProducto();

            ProductoTalla stockTalla = productoTallaRepo
                    .findByProductoIdAndTallaId(producto.getId(), item.getTalla().getId())
                    .orElseThrow(() -> new RuntimeException("La talla ya no está disponible para " + producto.getNombre()));
            stockTalla.setStock(stockTalla.getStock() - item.getCantidad());
            productoTallaRepo.save(stockTalla);

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setTalla(item.getTalla());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());

            detalleRepo.save(detalle);
            detalles.add(detalle);
        }

        // Registrar ingreso en cuenta de la tienda
        CuentaTienda cuenta = cuentaTiendaRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("Cuenta de la tienda no configurada"));
        cuenta.setSaldo(cuenta.getSaldo().add(total));
        cuentaTiendaRepo.save(cuenta);

        // Vaciar carrito
        carritoItemRepo.deleteByCarritoClienteId(cliente.getId());

        // RF044: la factura PDF y el correo son efectos secundarios no críticos —
        // si fallan (SMTP caído, disco lleno, etc.) no deben revertir una compra
        // ya válida en base de datos (stock, saldo y pedido ya confirmados arriba).
        try {
            Factura factura = new Factura();
            factura.setNumero("FAC-" + System.currentTimeMillis());
            factura.setFecha(LocalDateTime.now());
            factura.setSubtotal(subtotal);
            factura.setEnvio(costoEnvio);
            factura.setTotal(total);
            factura.setPedido(pedido);

            String urlPdf = facturaPdfService.generarFactura(pedido, detalles);
            factura.setUrlPdf(urlPdf);

            facturaRepository.save(factura);

            emailService.sendCompraDetalle(cliente.getCorreo(), pedido, detalles);
        } catch (Exception ex) {
            log.error("No se pudo generar la factura o enviar el correo del pedido {}", pedido.getId(), ex);
        }

        // Preparar respuesta
        PedidoResponse response = new PedidoResponse();
        response.setPedidoId(pedido.getId());
        response.setTotal(total);
        response.setEstado(pedido.getEstado());
        response.setTiempoEntrega(envio != null ? calcularTiempoEntrega(envio.getLugar()) : "Recojo en tienda");

        return response;
    }

    // RF043: Cancelación de pedidos (solo antes del envío)
    public PedidoResponse cancelarPedido(String correoCliente, Long pedidoId) {

        Pedido pedido = pedidoRepo.findByIdAndClienteCorreo(pedidoId, correoCliente)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!"PAGADO".equals(pedido.getEstado())) {
            throw new RuntimeException("El pedido ya no puede cancelarse");
        }

        // Restaurar stock de la talla comprada en cada línea del pedido
        List<PedidoDetalle> detalles = detalleRepo.findByPedidoId(pedido.getId());
        for (PedidoDetalle detalle : detalles) {
            productoTallaRepo.findByProductoIdAndTallaId(detalle.getProducto().getId(), detalle.getTalla().getId())
                    .ifPresent(stockTalla -> {
                        stockTalla.setStock(stockTalla.getStock() + detalle.getCantidad());
                        productoTallaRepo.save(stockTalla);
                    });
        }

        // Revertir el ingreso registrado en la cuenta de la tienda
        CuentaTienda cuenta = cuentaTiendaRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("Cuenta de la tienda no configurada"));
        cuenta.setSaldo(cuenta.getSaldo().subtract(pedido.getTotal()));
        cuentaTiendaRepo.save(cuenta);

        pedido.setEstado("CANCELADO");
        pedidoRepo.save(pedido);

        emailService.sendPedidoEstado(pedido.getCliente().getCorreo(), pedido);

        PedidoResponse response = new PedidoResponse();
        response.setPedidoId(pedido.getId());
        response.setTotal(pedido.getTotal());
        response.setEstado(pedido.getEstado());
        response.setTiempoEntrega(pedido.getEnvio() != null ? calcularTiempoEntrega(pedido.getEnvio().getLugar()) : "Recojo en tienda");

        return response;
    }

    public String calcularTiempoEntrega(String lugar) {
        if (lugar == null) return "No definido";

        return switch (lugar.toLowerCase()) {
            case "lima" -> "24 a 48 horas";
            case "callao" -> "48 horas";
            default -> "3 a 5 días hábiles";
        };
    }
}
