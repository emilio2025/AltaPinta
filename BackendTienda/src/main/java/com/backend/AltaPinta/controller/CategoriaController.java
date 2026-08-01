package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.model.Categoria;
import com.backend.AltaPinta.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@CrossOrigin("*")
public class CategoriaController {
    private final CategoriaRepository repo;
    public CategoriaController(CategoriaRepository repo){this.repo = repo;}

    @PostMapping public Categoria crear(@RequestBody Categoria c){ return repo.save(c); }
    @GetMapping
    public List<Categoria> listar(){ return repo.findAll(); }
}

