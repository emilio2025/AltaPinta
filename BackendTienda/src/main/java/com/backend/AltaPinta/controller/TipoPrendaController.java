package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.model.TipoPrenda;
import com.backend.AltaPinta.repository.ProductoRepository;
import com.backend.AltaPinta.repository.TipoPrendaRepository;
import org.springframework.web.bind.annotation.*;
import com.backend.AltaPinta.model.Producto;


import java.util.List;

@RestController
@RequestMapping("/tipos")
public class TipoPrendaController {

    private final TipoPrendaRepository repo;
    private final ProductoRepository productoRepo;

    public TipoPrendaController(TipoPrendaRepository repo, ProductoRepository productoRepo) {
        this.repo = repo;
        this.productoRepo = productoRepo;
    }


    @PostMapping public TipoPrenda crear(@RequestBody TipoPrenda t){
        return repo.save(t); }

    @GetMapping public List<TipoPrenda> listar(){
        return repo.findAll(); }

    @GetMapping("/categoria/{categoria}")
    public List<TipoPrenda> porCategoria(@PathVariable String categoria) {
        return productoRepo.findTiposByCategoria(categoria);
    }
}
