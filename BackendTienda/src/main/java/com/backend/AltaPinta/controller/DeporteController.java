package com.backend.AltaPinta.controller;

import jakarta.validation.Valid;

import com.backend.AltaPinta.model.Deporte;
import com.backend.AltaPinta.repository.DeporteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Catálogo de deportes.
 *
 * La lectura es pública (la tienda se navega sin cuenta) y la escritura queda
 * restringida a ROLE_ADMIN en SecurityConfig, igual que categorías y tallas.
 */
@RestController
@RequestMapping("/deportes")
public class DeporteController {

    private final DeporteRepository repo;

    public DeporteController(DeporteRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Deporte> listar() {
        return repo.findAll();
    }

    @PostMapping
    public Deporte crear(@Valid @RequestBody Deporte deporte) {
        return repo.save(deporte);
    }

    @PutMapping("/{id}")
    public Deporte actualizar(@PathVariable Long id, @Valid @RequestBody Deporte deporte) {
        Deporte db = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado"));
        db.setNombre(deporte.getNombre());
        db.setIcono(deporte.getIcono());
        return repo.save(db);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
