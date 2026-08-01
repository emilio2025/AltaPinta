package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.model.*;
import com.backend.AltaPinta.dto.CarritoDTO;
import com.backend.AltaPinta.dto.CarritoItemDTO;
import com.backend.AltaPinta.repository.CarritoItemRepository;
import com.backend.AltaPinta.repository.CarritoRepository;
import com.backend.AltaPinta.repository.ClienteRepository;
import com.backend.AltaPinta.repository.ProductoRepository;
import com.backend.AltaPinta.repository.ProductoTallaRepository;
import com.backend.AltaPinta.repository.TallaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrito")
@CrossOrigin("*")
public class CarritoController {

    private final CarritoRepository carritoRepo;
    private final CarritoItemRepository itemRepo;
    private final ProductoRepository productoRepo;
    private final ProductoTallaRepository productoTallaRepo;
    private final TallaRepository tallaRepo;
    private final ClienteRepository clienteRepo;

    public CarritoController(
            CarritoRepository carritoRepo,
            CarritoItemRepository itemRepo,
            ProductoRepository productoRepo,
            ProductoTallaRepository productoTallaRepo,
            TallaRepository tallaRepo,
            ClienteRepository clienteRepo) {
        this.carritoRepo = carritoRepo;
        this.itemRepo = itemRepo;
        this.productoRepo = productoRepo;
        this.productoTallaRepo = productoTallaRepo;
        this.tallaRepo = tallaRepo;
        this.clienteRepo = clienteRepo;
    }

    // MÉTODO INTERNO
    private Carrito obtenerCarritoEntidad(Authentication auth) {

        String correo = auth.getName();

        Cliente cliente = clienteRepo
                .findByCorreo(correo)
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado"));

        return carritoRepo
                .findByClienteId(cliente.getId())
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setCliente(cliente);
                    return carritoRepo.save(nuevo);
                });
    }

    private CarritoDTO toDto(Carrito c) {
        CarritoDTO dto = new CarritoDTO();
        dto.setId(c.getId());
        dto.setTotal(c.getTotal());

        dto.setItems(
                c.getItems().stream().map(i -> {
                    CarritoItemDTO d = new CarritoItemDTO();
                    d.setProductoId(i.getProducto().getId());
                    d.setNombre(i.getProducto().getNombre());
                    d.setPrecio(i.getProducto().getPrecio());
                    d.setCantidad(i.getCantidad());
                    d.setImagenUrl(i.getProducto().getImagenUrl());
                    if (i.getTalla() != null) {
                        d.setTallaId(i.getTalla().getId());
                        d.setTallaNombre(i.getTalla().getNombre());
                    }
                    return d;
                }).toList()
        );

        return dto;
    }

    // RF013 – VER CARRITO
    @GetMapping
    public CarritoDTO obtenerCarrito(Authentication auth) {
        return toDto(obtenerCarritoEntidad(auth));
    }

    // RF010 – AGREGAR PRODUCTO (con talla seleccionada por el cliente)
    @PostMapping("/agregar/{productoId}")
    public void agregar(@PathVariable Long productoId,
                        @RequestParam int cantidad,
                        @RequestParam Long tallaId,
                        Authentication auth) {

        Carrito carrito = obtenerCarritoEntidad(auth);
        Producto p = productoRepo.findById(productoId).orElseThrow();
        Talla talla = tallaRepo.findById(tallaId)
                .orElseThrow(() -> new RuntimeException("Talla no encontrada"));

        ProductoTalla disponible = productoTallaRepo.findByProductoIdAndTallaId(productoId, tallaId)
                .orElseThrow(() -> new RuntimeException("Esa talla no está disponible para este producto"));

        if (disponible.getStock() <= 0) {
            throw new RuntimeException("No hay stock disponible para la talla seleccionada");
        }

        CarritoItem item = itemRepo
                .findByCarritoIdAndProductoIdAndTallaId(carrito.getId(), productoId, tallaId)
                .orElseGet(() -> {
                    CarritoItem i = new CarritoItem();
                    i.setCarrito(carrito);
                    i.setProducto(p);
                    i.setTalla(talla);
                    i.setCantidad(0);
                    return i;
                });

        item.setCantidad(item.getCantidad() + cantidad);
        itemRepo.save(item);
    }

    // RF011 – MODIFICAR CANTIDAD
    @PutMapping("/actualizar/{productoId}")
    public void actualizar(@PathVariable Long productoId,
                           @RequestParam int cantidad,
                           @RequestParam Long tallaId,
                           Authentication auth) {

        Carrito carrito = obtenerCarritoEntidad(auth);

        CarritoItem item = itemRepo
                .findByCarritoIdAndProductoIdAndTallaId(carrito.getId(), productoId, tallaId)
                .orElseThrow();

        item.setCantidad(cantidad);
        itemRepo.save(item);
    }

    // RF012 – ELIMINAR PRODUCTO
    @DeleteMapping("/eliminar/{productoId}")
    public void eliminar(@PathVariable Long productoId,
                          @RequestParam Long tallaId,
                          Authentication auth) {
        Carrito carrito = obtenerCarritoEntidad(auth);
        itemRepo.findByCarritoIdAndProductoIdAndTallaId(carrito.getId(), productoId, tallaId)
                .ifPresent(itemRepo::delete);
    }
}
