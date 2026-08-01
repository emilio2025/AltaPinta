package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.dto.ProductoTallaRequest;
import com.backend.AltaPinta.model.Producto;
import com.backend.AltaPinta.model.ProductoImagen;
import com.backend.AltaPinta.model.ProductoTalla;
import com.backend.AltaPinta.model.Talla;
import com.backend.AltaPinta.repository.ProductoImagenRepository;
import com.backend.AltaPinta.repository.ProductoRepository;
import com.backend.AltaPinta.repository.ProductoTallaRepository;
import com.backend.AltaPinta.repository.TallaRepository;
import com.backend.AltaPinta.service.ImagenService;
import com.backend.AltaPinta.service.AuditoriaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
@CrossOrigin("*")
public class ProductoController {

    private final ProductoRepository repo;
    private final ProductoTallaRepository productoTallaRepo;
    private final ProductoImagenRepository productoImagenRepo;
    private final TallaRepository tallaRepo;
    private final ImagenService imagenService;
    private final AuditoriaService auditoriaService;

    public ProductoController(ProductoRepository repo, ProductoTallaRepository productoTallaRepo,
                               ProductoImagenRepository productoImagenRepo, TallaRepository tallaRepo,
                               ImagenService imagenService, AuditoriaService auditoriaService) {
        this.repo = repo;
        this.productoTallaRepo = productoTallaRepo;
        this.productoImagenRepo = productoImagenRepo;
        this.tallaRepo = tallaRepo;
        this.imagenService = imagenService;
        this.auditoriaService = auditoriaService;
    }

    // Subida de imagen de producto desde el PC (RF administrativo)
    @PostMapping("/imagen")
    public Map<String, String> subirImagen(@RequestParam("file") MultipartFile file) {
        String url = imagenService.guardar(file);
        return Map.of("url", url);
    }

    // RF001 Registrar producto
    @PostMapping
    public Producto crear(@Valid @RequestBody Producto p, Authentication auth) {
        Producto guardado = repo.save(p);
        auditoriaService.registrar("Producto", guardado.getId(), "CREAR", auth.getName(), guardado.getNombre());
        return guardado;
    }

    // RF002 Editar producto
    @PutMapping("/{id}")
    public Producto editar(@PathVariable Long id, @Valid @RequestBody Producto p, Authentication auth){
        p.setId(id);
        Producto guardado = repo.save(p);
        auditoriaService.registrar("Producto", id, "EDITAR", auth.getName(), guardado.getNombre());
        return guardado;
    }

    // RF003 Eliminar producto
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id, Authentication auth) {
        repo.deleteById(id);
        auditoriaService.registrar("Producto", id, "ELIMINAR", auth.getName(), null);
    }

    // RF004 Listar productos (paginado, con filtros opcionales)
    //
    // Devuelve una página, no la lista entera: el catálogo ya supera los 700
    // productos y enviarlos todos en cada carga era la petición más pesada de
    // la aplicación. Los filtros que antes se aplicaban en el navegador
    // (nombre, categoría, tipo y talla) se resuelven ahora en la consulta,
    // así que la búsqueda cubre el catálogo completo y no solo lo descargado.
    //
    // Los parámetros vacíos se normalizan a null para que la consulta los
    // ignore: el navegador manda "" cuando el usuario borra el buscador.
    @GetMapping
    public Page<Producto> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String talla,
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        return repo.buscar(
                normalizar(nombre),
                normalizar(categoria),
                normalizar(tipo),
                normalizar(talla),
                pageable
        );
    }

    /** Convierte a null los parámetros ausentes o en blanco. */
    private String normalizar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    // *** CLIENTE RF007 RF008 RF009 ***
    @GetMapping("/categoria/{nombre}")
    public List<Producto> filtrarPorCategoria(@PathVariable String nombre){
        return repo.findByCategoriaNombre(nombre);
    }

    @GetMapping("/tipo/{nombre}")
    public List<Producto> filtrarPorTipo(@PathVariable String nombre){
        return repo.findByTipoPrendaNombre(nombre);
    }

    @GetMapping("/talla/{nombre}")
    public List<Producto> filtrarPorTalla(@PathVariable String nombre){
        return repo.findByTallaNombre(nombre);
    }

    /*@GetMapping("/{id}")
    public Producto obtener(@PathVariable Long id){
        return repo.findById(id).orElseThrow(() -> new RuntimeException("No existe"));
    }*/

    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // Tallas múltiples: reemplaza por completo las tallas/stock disponibles de un producto
    @PutMapping("/{id}/tallas")
    @Transactional
    public List<ProductoTalla> sincronizarTallas(@PathVariable Long id,
                                                  @Valid @RequestBody List<ProductoTallaRequest> tallas,
                                                  Authentication auth) {
        Producto producto = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        productoTallaRepo.deleteByProductoId(id);
        productoTallaRepo.flush(); // asegura que el DELETE se ejecute antes de los INSERT nuevos

        List<ProductoTalla> nuevas = tallas.stream().map(req -> {
            Talla talla = tallaRepo.findById(req.getTallaId())
                    .orElseThrow(() -> new RuntimeException("Talla no encontrada"));

            ProductoTalla pt = new ProductoTalla();
            pt.setProducto(producto);
            pt.setTalla(talla);
            pt.setStock(req.getStock());
            return pt;
        }).toList();

        List<ProductoTalla> guardadas = productoTallaRepo.saveAll(nuevas);
        auditoriaService.registrar("Producto", id, "ACTUALIZAR_TALLAS", auth.getName(), producto.getNombre());
        return guardadas;
    }

    // Galería de imágenes: reemplaza por completo las fotos del producto (2-3 recomendadas).
    // La primera url de la lista queda también como imagenUrl (portada usada en las tarjetas).
    @PutMapping("/{id}/imagenes")
    @Transactional
    public List<ProductoImagen> sincronizarImagenes(@PathVariable Long id,
                                                      @RequestBody List<String> urls,
                                                      Authentication auth) {
        Producto producto = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        productoImagenRepo.deleteByProductoId(id);
        productoImagenRepo.flush();

        List<ProductoImagen> nuevas = new java.util.ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            ProductoImagen img = new ProductoImagen();
            img.setProducto(producto);
            img.setUrl(urls.get(i));
            img.setOrden(i);
            nuevas.add(img);
        }
        List<ProductoImagen> guardadas = productoImagenRepo.saveAll(nuevas);

        producto.setImagenUrl(urls.isEmpty() ? null : urls.get(0));
        repo.save(producto);

        auditoriaService.registrar("Producto", id, "ACTUALIZAR_IMAGENES", auth.getName(), producto.getNombre());
        return guardadas;
    }

}
