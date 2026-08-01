package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.dto.ActualizarPerfilRequest;
import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.repository.ClienteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente")
@CrossOrigin("*")
public class ClienteController {

    private final ClienteRepository clienteRepo;

    public ClienteController(ClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    // PERFIL DEL USUARIO LOGUEADO
    @GetMapping("/me")
    public Cliente perfil(Authentication auth) {
        return clienteRepo.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    // ACTUALIZAR PERFIL (SIN CORREO NI PASSWORD)
    @PutMapping("/actualizar")
    public Cliente actualizar(@RequestBody ActualizarPerfilRequest req, Authentication auth) {

        Cliente db = clienteRepo.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // campos permitidos
        db.setNombre(req.getNombre());
        db.setDireccion(req.getDireccion());
        db.setDni(req.getDni());
        db.setRuc(req.getRuc());
        db.setRazonSocial(req.getRazonSocial());

        return clienteRepo.save(db);
    }
}
