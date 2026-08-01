package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.model.Envio;
import com.backend.AltaPinta.repository.EnvioRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/envio")
@CrossOrigin(origins = "http://localhost:4200")
public class EnvioController {

    private final EnvioRepository envioRepo;

    public EnvioController(EnvioRepository envioRepo) {
        this.envioRepo = envioRepo;
    }

    @GetMapping
    public List<Envio> listarEnvios() {
        return envioRepo.findAll();
    }
}
