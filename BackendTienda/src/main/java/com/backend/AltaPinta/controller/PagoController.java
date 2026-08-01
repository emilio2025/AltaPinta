package com.backend.AltaPinta.controller;


import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.model.Pedido;
import com.backend.AltaPinta.repository.ClienteRepository;
import com.backend.AltaPinta.repository.PedidoRepository;
import com.backend.AltaPinta.service.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pago")
@CrossOrigin("*")
public class PagoController {

    private final PagoService pagoService;
    private final PedidoRepository pedidoRepo;
    private final ClienteRepository clienteRepo;

    public PagoController(PagoService pagoService, PedidoRepository pedidoRepo, ClienteRepository clienteRepo) {
        this.pagoService = pagoService;
        this.pedidoRepo = pedidoRepo;
        this.clienteRepo = clienteRepo;
    }

    @PostMapping("/procesar")
    public ResponseEntity<?> pagar(
            @RequestParam Long pedidoId,
            @RequestParam String numeroTarjeta,
            @RequestParam String cvv,
            @RequestParam String fechaVencimiento,
            Authentication auth
    ) {

        Pedido pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Cliente cliente = clienteRepo.findByCorreo(auth.getName())
                .orElseThrow();

        boolean aprobado = pagoService.procesarPago(
                pedido,
                cliente,
                numeroTarjeta,
                cvv,
                fechaVencimiento,
                pedido.getTotal()
        );

        pedido.setEstado(aprobado ? "PAGADO" : "RECHAZADO");
        pedidoRepo.save(pedido);

        if (!aprobado) {
            return ResponseEntity.badRequest()
                    .body("Saldo insuficiente o tarjeta inválida");
        }

        return ResponseEntity.ok("Pago aprobado");
    }
}