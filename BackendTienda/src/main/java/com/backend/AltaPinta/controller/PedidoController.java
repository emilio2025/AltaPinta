package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.dto.ConfirmarPedidoDTO;
import com.backend.AltaPinta.dto.PedidoResponse;
import com.backend.AltaPinta.model.Pedido;
import com.backend.AltaPinta.model.PedidoDetalle;
import com.backend.AltaPinta.repository.PedidoDetalleRepository;
import com.backend.AltaPinta.repository.PedidoRepository;
import com.backend.AltaPinta.service.EmailService;
import com.backend.AltaPinta.service.FacturaPdfService;
import com.backend.AltaPinta.service.PedidoService;
import com.backend.AltaPinta.service.AuditoriaService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepo;
    private final PedidoDetalleRepository detalleRepo;
    private final FacturaPdfService facturaPdfService;
    private final EmailService emailService;
    private final AuditoriaService auditoriaService;

    public PedidoController(
            PedidoService pedidoService,
            PedidoRepository pedidoRepo,
            PedidoDetalleRepository detalleRepo,
            FacturaPdfService facturaPdfService, EmailService emailService,
            AuditoriaService auditoriaService
    ) {
        this.pedidoService = pedidoService;
        this.pedidoRepo = pedidoRepo;
        this.detalleRepo = detalleRepo;
        this.facturaPdfService = facturaPdfService;
        this.emailService = emailService;
        this.auditoriaService = auditoriaService;
    }

    // RF014–RF016: Confirmar pedido
    @PostMapping("/confirmar")
    public ResponseEntity<PedidoResponse> confirmar(
            @RequestBody ConfirmarPedidoDTO dto,
            Authentication auth
    ) {
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        PedidoResponse response = pedidoService.confirmarPedido(auth.getName(), dto);
        return ResponseEntity.ok(response);
    }

    // RF027: Descargar factura
    @GetMapping("/{id}/factura")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable Long id) {
        Pedido pedido = pedidoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // RF026:  El sistema debe generar una factura o comprobante
        List<PedidoDetalle> detalles = detalleRepo.findByPedidoId(id);
        String rutaPdf = facturaPdfService.generarFactura(pedido, detalles); // Devuelve path del archivo

        try {
            File pdfFile = new File(rutaPdf);
            if (!pdfFile.exists()) {
                throw new RuntimeException("Archivo de factura no encontrado");
            }

            byte[] contenido = Files.readAllBytes(pdfFile.toPath());

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=factura_" + id + ".pdf")
                    .header("Content-Type", "application/pdf")
                    .body(contenido);

        } catch (IOException e) {
            throw new RuntimeException("Error leyendo archivo de factura", e);
        }
    }

    @GetMapping("/mis-pedidos")
    public List<Pedido> misPedidos(Authentication auth) {
        if (auth == null) {
            throw new RuntimeException("No autorizado");
        }
        return pedidoRepo.findByClienteCorreo(auth.getName());
    }

    // RF043: Cancelar pedido propio (antes del envío)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        PedidoResponse response = pedidoService.cancelarPedido(auth.getName(), id);
        auditoriaService.registrar("Pedido", id, "CANCELAR", auth.getName(), null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/todos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Pedido> listarTodos() {
        return pedidoRepo.findAll(); // Incluye cliente, total y estado
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado,
            Authentication auth
    ) {
        Pedido pedido = pedidoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        List<String> estadosValidos = List.of("PAGADO", "ENVIADO", "EN TRANSITO", "EN DESTINO", "ENTREGADO");
        if (!estadosValidos.contains(nuevoEstado.toUpperCase())) {
            return ResponseEntity.badRequest().build();
        }

        pedido.setEstado(nuevoEstado.toUpperCase());
        pedidoRepo.save(pedido);
        auditoriaService.registrar("Pedido", id, "CAMBIAR_ESTADO", auth.getName(), pedido.getEstado());

        // Enviar correo al cliente notificando el nuevo estado
        emailService.sendPedidoEstado(pedido.getCliente().getCorreo(), pedido);

        PedidoResponse response = new PedidoResponse(
                pedido.getId(),
                pedido.getTotal(),
                pedido.getEstado(),
                pedido.getEnvio() != null ? pedidoService.calcularTiempoEntrega(pedido.getEnvio().getLugar()) : "Recojo en tienda"
        );

        return ResponseEntity.ok(response);
    }

}

