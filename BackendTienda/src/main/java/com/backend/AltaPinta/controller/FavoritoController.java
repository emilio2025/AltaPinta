package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.model.Favorito;
import com.backend.AltaPinta.model.Producto;
import com.backend.AltaPinta.repository.FavoritoRepository;
import com.backend.AltaPinta.repository.ProductoRepository;
import com.backend.AltaPinta.repository.ClienteRepository;
import com.backend.AltaPinta.model.Cliente;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favoritos")
@CrossOrigin("*")
public class FavoritoController {

    private final FavoritoRepository favRepo;
    private final ProductoRepository productoRepo;
    private final ClienteRepository usuarioRepo;

    public FavoritoController(FavoritoRepository favRepo,
                              ProductoRepository productoRepo,
                              ClienteRepository usuarioRepo) {
        this.favRepo = favRepo;
        this.productoRepo = productoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // ==============================
    // OBTENER FAVORITOS
    // ==============================
    @GetMapping
    public List<Producto> listarFavoritos(Authentication auth) {

        if (auth == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Cliente u = usuarioRepo.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return favRepo.findByUsuarioId(u.getId())
                .stream()
                .map(Favorito::getProducto)
                .toList();
    }

    // ==============================
    // TOGGLE FAVORITO (AGREGAR / QUITAR)
    // ==============================
    @PostMapping("/{productoId}")
    public boolean toggleFavorito(@PathVariable Long productoId,
                                  Authentication auth) {

        if (auth == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Cliente u = usuarioRepo.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Producto p = productoRepo.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return favRepo.findByUsuarioIdAndProductoId(u.getId(), productoId)
                .map(f -> {
                    // YA EXISTE → QUITAR
                    favRepo.delete(f);
                    return false; // despintado
                })
                .orElseGet(() -> {
                    // NO EXISTE → AGREGAR
                    Favorito nuevo = new Favorito();
                    nuevo.setUsuarioId(u.getId());
                    nuevo.setProducto(p);
                    favRepo.save(nuevo);
                    return true; // pintado
                });
    }

    // ==============================
    // CONSULTAR SI ES FAVORITO
    // ==============================
    @GetMapping("/check/{productoId}")
    public boolean esFavorito(@PathVariable Long productoId,
                              Authentication auth) {

        if (auth == null) {
            return false;
        }

        Cliente u = usuarioRepo.findByCorreo(auth.getName())
                .orElse(null);

        if (u == null) return false;

        return favRepo.findByUsuarioIdAndProductoId(u.getId(), productoId).isPresent();
    }

    // ==============================
    // ELIMINAR FAVORITO POR ID (Para la vista de Favoritos)
    // ==============================
    @DeleteMapping("/{productoId}")
    public void eliminarFavorito(@PathVariable Long productoId, Authentication auth) {
        if (auth == null) throw new RuntimeException("No autenticado");

        Cliente u = usuarioRepo.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        favRepo.findByUsuarioIdAndProductoId(u.getId(), productoId)
                .ifPresent(favRepo::delete);
    }
}
