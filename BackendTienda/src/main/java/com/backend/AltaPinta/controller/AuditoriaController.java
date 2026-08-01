package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.model.Auditoria;
import com.backend.AltaPinta.repository.AuditoriaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequestMapping("/auditoria")
@CrossOrigin("*")
public class AuditoriaController {

    private final AuditoriaRepository repo;

    public AuditoriaController(AuditoriaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Auditoria> listar() {
        return repo.findAllByOrderByFechaDesc();
    }
}
