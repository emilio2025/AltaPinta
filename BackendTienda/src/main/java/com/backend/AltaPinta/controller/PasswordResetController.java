package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.service.EmailService;
import com.backend.AltaPinta.service.PasswordValidator;
import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/password")
public class PasswordResetController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Solicitar código
    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitar(@RequestParam String correo) {

        Cliente cliente = clienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Correo no registrado"));

        String codigo = UUID.randomUUID().toString().substring(0, 6);

        cliente.setTokenResetPassword(codigo);
        cliente.setTokenResetExpira(LocalDateTime.now().plusMinutes(10));

        clienteRepository.save(cliente);

        emailService.sendPasswordResetCode(correo, codigo);

        return ResponseEntity.ok("Código enviado al correo");
    }

    // Validar código
    @PostMapping("/validar")
    public ResponseEntity<?> validar(@RequestParam String codigo) {

        Cliente cliente = clienteRepository.findByTokenResetPassword(codigo)
                .orElseThrow(() -> new RuntimeException("Código inválido"));

        if (cliente.getTokenResetExpira().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Código expirado");
        }

        return ResponseEntity.ok("Código válido");
    }

    // Cambiar contraseña
    @PostMapping("/cambiar")
    public ResponseEntity<?> cambiar(
            @RequestParam String codigo,
            @RequestParam String nuevaPassword
    ) {

        Cliente cliente = clienteRepository.findByTokenResetPassword(codigo)
                .orElseThrow(() -> new RuntimeException("Código inválido"));

        // RF035: Validar criterios de seguridad de la contraseña
        if (!PasswordValidator.esValida(nuevaPassword)) {
            return ResponseEntity.badRequest().body(PasswordValidator.MENSAJE_ERROR);
        }

        cliente.setPassword(passwordEncoder.encode(nuevaPassword));
        cliente.setTokenResetPassword(null);
        cliente.setTokenResetExpira(null);

        clienteRepository.save(cliente);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }
}
