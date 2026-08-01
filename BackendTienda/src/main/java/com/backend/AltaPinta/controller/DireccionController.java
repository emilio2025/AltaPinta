package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.model.Direccion;
import com.backend.AltaPinta.repository.ClienteRepository;
import com.backend.AltaPinta.repository.DireccionRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// RF046: Registro de direcciones de envío del cliente
@RestController
@RequestMapping("/direcciones")
@CrossOrigin("*")
public class DireccionController {

    private final DireccionRepository repo;
    private final ClienteRepository clienteRepo;

    public DireccionController(DireccionRepository repo, ClienteRepository clienteRepo) {
        this.repo = repo;
        this.clienteRepo = clienteRepo;
    }

    @GetMapping
    public List<Direccion> listar(Authentication auth) {
        return repo.findByClienteCorreo(auth.getName());
    }

    @PostMapping
    public Direccion crear(@Valid @RequestBody Direccion d, Authentication auth) {
        Cliente cliente = clienteRepo.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        d.setId(null);
        d.setCliente(cliente);
        return repo.save(d);
    }

    @PutMapping("/{id}")
    public Direccion editar(@PathVariable Long id, @Valid @RequestBody Direccion d, Authentication auth) {
        Direccion existente = repo.findByIdAndClienteCorreo(id, auth.getName())
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        existente.setEtiqueta(d.getEtiqueta());
        existente.setDireccionCompleta(d.getDireccionCompleta());
        existente.setReferencia(d.getReferencia());
        existente.setDistrito(d.getDistrito());
        return repo.save(existente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id, Authentication auth) {
        Direccion existente = repo.findByIdAndClienteCorreo(id, auth.getName())
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        repo.delete(existente);
    }
}
