package com.backend.AltaPinta.Config;

import com.backend.AltaPinta.model.Deporte;
import com.backend.AltaPinta.model.Producto;
import com.backend.AltaPinta.repository.DeporteRepository;
import com.backend.AltaPinta.repository.ProductoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Siembra el catálogo de deportes y asigna uno a los productos que aún no
 * lo tienen.
 *
 * El catálogo original se cargó antes de que existiera este campo, así que
 * los productos vienen sin deporte. La asignación se deduce del nombre de la
 * prenda con reglas sencillas y, cuando ninguna encaja, se usa Training como
 * valor genérico. Es una aproximación para que la tienda tenga datos con los
 * que trabajar: desde el panel de administración se puede corregir cualquier
 * producto.
 *
 * El proceso solo toca productos con deporte nulo, así que es seguro
 * ejecutarlo varias veces y nunca pisa una asignación hecha a mano.
 */
@Component
public class DeporteSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DeporteSeeder.class);

    /** Deportes iniciales, con el icono de PrimeIcons que usa la tienda. */
    private static final Map<String, String> DEPORTES = new LinkedHashMap<>() {{
        put("Running",    "pi-forward");
        put("Training",   "pi-bolt");
        put("Fútbol",     "pi-circle");
        put("Básquet",    "pi-star");
        put("Yoga",       "pi-heart");
        put("Outdoor",    "pi-compass");
    }};

    /**
     * Palabras del nombre del producto que sugieren un deporte.
     * El orden importa: gana la primera que aparezca.
     */
    private static final Map<String, String> PISTAS = new LinkedHashMap<>() {{
        put("short",     "Running");
        put("legging",   "Yoga");
        put("licra",     "Yoga");
        put("top",       "Yoga");
        put("croptop",   "Yoga");
        put("casaca",    "Outdoor");
        put("cortavient","Outdoor");
        put("chaleco",   "Outdoor");
        put("polera",    "Training");
        put("buzo",      "Training");
        put("jogger",    "Training");
        put("pantalon",  "Training");
        put("pantalón",  "Training");
        put("camiseta",  "Fútbol");
        put("polo",      "Fútbol");
        put("bividi",    "Básquet");
        put("bibidi",    "Básquet");
        put("musculosa", "Básquet");
    }};

    private static final String DEPORTE_POR_DEFECTO = "Training";

    private final DeporteRepository deporteRepo;
    private final ProductoRepository productoRepo;

    public DeporteSeeder(DeporteRepository deporteRepo, ProductoRepository productoRepo) {
        this.deporteRepo = deporteRepo;
        this.productoRepo = productoRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        crearDeportesQueFalten();
        asignarDeporteAProductosSinEl();
    }

    private void crearDeportesQueFalten() {
        DEPORTES.forEach((nombre, icono) -> {
            if (!deporteRepo.existsByNombreIgnoreCase(nombre)) {
                Deporte d = new Deporte();
                d.setNombre(nombre);
                d.setIcono(icono);
                deporteRepo.save(d);
                log.info("Deporte creado: {}", nombre);
            }
        });
    }

    private void asignarDeporteAProductosSinEl() {
        List<Producto> sinDeporte = productoRepo.findAll().stream()
                .filter(p -> p.getDeporte() == null)
                .toList();

        if (sinDeporte.isEmpty()) {
            return;
        }

        for (Producto p : sinDeporte) {
            p.setDeporte(deducirDeporte(p.getNombre()));
        }
        productoRepo.saveAll(sinDeporte);

        log.info("Se asignó deporte a {} productos que no lo tenían", sinDeporte.size());
    }

    /** Busca la primera pista que aparezca en el nombre; si no hay, usa el valor por defecto. */
    private Deporte deducirDeporte(String nombreProducto) {
        String nombre = nombreProducto == null ? "" : nombreProducto.toLowerCase(Locale.ROOT);

        String elegido = PISTAS.entrySet().stream()
                .filter(pista -> nombre.contains(pista.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEPORTE_POR_DEFECTO);

        return deporteRepo.findByNombreIgnoreCase(elegido).orElse(null);
    }
}
