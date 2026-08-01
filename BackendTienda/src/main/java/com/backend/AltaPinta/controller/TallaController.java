package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.model.Producto;
import com.backend.AltaPinta.model.Talla;
import com.backend.AltaPinta.repository.ProductoRepository;
import com.backend.AltaPinta.repository.TallaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tallas")
@CrossOrigin("*")
public class TallaController {

    private final TallaRepository tallaRepo;
    private final ProductoRepository productoRepo;

    public TallaController(TallaRepository tallaRepo,
                           ProductoRepository productoRepo) {
        this.tallaRepo = tallaRepo;
        this.productoRepo = productoRepo;
    }

    @GetMapping("/categoria/{nombre}")
    public List<Talla> porCategoria(@PathVariable String nombre) {
        return productoRepo.findTallasByCategoria(nombre);
    }

    @PostMapping
    public Talla crear(@RequestBody Talla t){
        return tallaRepo.save(t);
    }

    @GetMapping
    public List<Talla> listar(){
        return tallaRepo.findAll();
    }

    @GetMapping("/talla/{nombre}")
    public List<Producto> porTalla(@PathVariable String nombre) {
        return productoRepo.findByTallaNombre(nombre);
    }

}

