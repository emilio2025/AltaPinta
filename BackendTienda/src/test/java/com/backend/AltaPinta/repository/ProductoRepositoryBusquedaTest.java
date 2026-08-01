package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la búsqueda paginada del catálogo.
 *
 * Cubren las dos cosas que hacen fácil equivocarse aquí: que cada filtro se
 * ignore cuando no se envía, y que el total de resultados sea correcto pese
 * al LEFT JOIN con las tallas, que duplica filas.
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalogo;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductoRepositoryBusquedaTest {

    @Autowired private ProductoRepository productoRepo;
    @Autowired private CategoriaRepository categoriaRepo;
    @Autowired private TipoPrendaRepository tipoRepo;
    @Autowired private TallaRepository tallaRepo;
    @Autowired private ProductoTallaRepository productoTallaRepo;
    @Autowired private DeporteRepository deporteRepo;

    private Categoria mujer;
    private Categoria varon;
    private TipoPrenda polo;
    private TipoPrenda pantalon;
    private Talla tallaM;
    private Talla tallaL;

    @BeforeEach
    void setUp() {
        mujer = categoria("Mujer");
        varon = categoria("Varón");
        polo = tipo("Polo");
        pantalon = tipo("Pantalón");
        tallaM = talla("M");
        tallaL = talla("L");
    }

    private Categoria categoria(String nombre) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        return categoriaRepo.save(c);
    }

    private TipoPrenda tipo(String nombre) {
        TipoPrenda t = new TipoPrenda();
        t.setNombre(nombre);
        return tipoRepo.save(t);
    }

    private Talla talla(String nombre) {
        Talla t = new Talla();
        t.setNombre(nombre);
        return tallaRepo.save(t);
    }

    /** Crea un producto y, si se indican tallas, su stock para cada una. */
    private Producto producto(String nombre, Categoria cat, TipoPrenda tip, Talla... tallas) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(100.0);
        p.setCategoria(cat);
        p.setTipoPrenda(tip);
        p = productoRepo.save(p);

        for (Talla t : tallas) {
            ProductoTalla pt = new ProductoTalla();
            pt.setProducto(p);
            pt.setTalla(t);
            pt.setStock(5);
            productoTallaRepo.save(pt);
        }
        return p;
    }

    private Page<Producto> buscar(String nombre, String categoria, String tipo, String talla, int pagina, int tamano) {
        return productoRepo.buscar(nombre, categoria, tipo, null, talla,
                PageRequest.of(pagina, tamano, Sort.by("id")));
    }

    private Page<Producto> buscarPorDeporte(String deporte) {
        return productoRepo.buscar(null, null, null, deporte, null,
                PageRequest.of(0, 20, Sort.by("id")));
    }

    // ============================================================
    @Nested
    @DisplayName("Filtro por deporte")
    class FiltroDeporte {

        private Deporte running;
        private Deporte yoga;

        @BeforeEach
        void catalogo() {
            running = deporte("Running");
            yoga = deporte("Yoga");

            conDeporte(producto("Short ligero", mujer, pantalon, tallaM), running);
            conDeporte(producto("Camiseta transpirable", varon, polo, tallaM), running);
            conDeporte(producto("Legging alta compresion", mujer, pantalon, tallaL), yoga);
            // Producto sin deporte asignado: los del catalogo original estan asi.
            producto("Prenda sin clasificar", mujer, polo, tallaM);
        }

        private Deporte deporte(String nombre) {
            Deporte d = new Deporte();
            d.setNombre(nombre);
            return deporteRepo.save(d);
        }

        private void conDeporte(Producto p, Deporte d) {
            p.setDeporte(d);
            productoRepo.save(p);
        }

        @Test
        @DisplayName("Filtra los productos de un deporte")
        void filtraPorDeporte() {
            assertEquals(2, buscarPorDeporte("Running").getTotalElements());
            assertEquals(1, buscarPorDeporte("Yoga").getTotalElements());
        }

        @Test
        @DisplayName("No distingue mayusculas")
        void sinDistinguirMayusculas() {
            assertEquals(2, buscarPorDeporte("running").getTotalElements());
            assertEquals(2, buscarPorDeporte("RUNNING").getTotalElements());
        }

        @Test
        @DisplayName("Sin filtro de deporte aparecen tambien los productos sin asignar")
        void sinFiltroSalenTodos() {
            // El LEFT JOIN con deporte es lo que mantiene visible la prenda
            // sin clasificar; con un INNER JOIN desapareceria del catalogo.
            assertEquals(4, buscarPorDeporte(null).getTotalElements());
        }

        @Test
        @DisplayName("Un deporte sin productos devuelve pagina vacia")
        void deporteSinProductos() {
            assertEquals(0, buscarPorDeporte("Basquet").getTotalElements());
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Paginacion")
    class Paginacion {

        @BeforeEach
        void catalogo() {
            for (int i = 1; i <= 25; i++) {
                producto("Producto " + i, mujer, polo);
            }
        }

        @Test
        @DisplayName("Devuelve solo el tamaño de pagina pedido")
        void respetaElTamanoDePagina() {
            Page<Producto> pagina = buscar(null, null, null, null, 0, 12);

            assertEquals(12, pagina.getContent().size());
        }

        @Test
        @DisplayName("El total refleja todo el catalogo, no solo la pagina")
        void elTotalEsDelCatalogoCompleto() {
            Page<Producto> pagina = buscar(null, null, null, null, 0, 12);

            assertEquals(25, pagina.getTotalElements());
            assertEquals(3, pagina.getTotalPages());
        }

        @Test
        @DisplayName("La ultima pagina trae solo lo que queda")
        void ultimaPaginaParcial() {
            Page<Producto> pagina = buscar(null, null, null, null, 2, 12);

            assertEquals(1, pagina.getContent().size());
            assertTrue(pagina.isLast());
        }

        @Test
        @DisplayName("Paginas distintas devuelven productos distintos")
        void paginasSinSolapamiento() {
            List<Long> primera = buscar(null, null, null, null, 0, 10)
                    .getContent().stream().map(Producto::getId).toList();
            List<Long> segunda = buscar(null, null, null, null, 1, 10)
                    .getContent().stream().map(Producto::getId).toList();

            assertTrue(primera.stream().noneMatch(segunda::contains),
                    "Una pagina no debe repetir productos de la anterior");
        }

        @Test
        @DisplayName("Una pagina mas alla del final sale vacia, no falla")
        void paginaFueraDeRango() {
            Page<Producto> pagina = buscar(null, null, null, null, 99, 12);

            assertTrue(pagina.getContent().isEmpty());
            assertEquals(25, pagina.getTotalElements());
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Filtros")
    class Filtros {

        @BeforeEach
        void catalogo() {
            producto("Polo deportivo azul", mujer, polo, tallaM);
            producto("Polo casual blanco", mujer, polo, tallaL);
            producto("Pantalón cargo", mujer, pantalon, tallaM);
            producto("Polo running", varon, polo, tallaM, tallaL);
        }

        @Test
        @DisplayName("Sin filtros devuelve todo el catalogo")
        void sinFiltrosDevuelveTodo() {
            assertEquals(4, buscar(null, null, null, null, 0, 20).getTotalElements());
        }

        @Test
        @DisplayName("Busca por parte del nombre, sin distinguir mayusculas")
        void buscaPorNombre() {
            assertEquals(3, buscar("polo", null, null, null, 0, 20).getTotalElements());
            assertEquals(3, buscar("POLO", null, null, null, 0, 20).getTotalElements());
            assertEquals(1, buscar("cargo", null, null, null, 0, 20).getTotalElements());
        }

        @Test
        @DisplayName("Filtra por categoria")
        void filtraPorCategoria() {
            assertEquals(3, buscar(null, "Mujer", null, null, 0, 20).getTotalElements());
            assertEquals(1, buscar(null, "Varón", null, null, 0, 20).getTotalElements());
        }

        @Test
        @DisplayName("Filtra por tipo de prenda")
        void filtraPorTipo() {
            assertEquals(3, buscar(null, null, "Polo", null, 0, 20).getTotalElements());
            assertEquals(1, buscar(null, null, "Pantalón", null, 0, 20).getTotalElements());
        }

        @Test
        @DisplayName("Filtra por talla disponible")
        void filtraPorTalla() {
            assertEquals(3, buscar(null, null, null, "M", 0, 20).getTotalElements());
            assertEquals(2, buscar(null, null, null, "L", 0, 20).getTotalElements());
        }

        @Test
        @DisplayName("Los filtros se combinan entre si")
        void filtrosCombinados() {
            // Polos de mujer en talla M: solo el azul.
            Page<Producto> pagina = buscar("polo", "Mujer", "Polo", "M", 0, 20);

            assertEquals(1, pagina.getTotalElements());
            assertEquals("Polo deportivo azul", pagina.getContent().get(0).getNombre());
        }

        @Test
        @DisplayName("Una combinacion sin resultados devuelve pagina vacia")
        void sinResultados() {
            Page<Producto> pagina = buscar("inexistente", null, null, null, 0, 20);

            assertEquals(0, pagina.getTotalElements());
            assertTrue(pagina.getContent().isEmpty());
        }

        @Test
        @DisplayName("Un producto sin tallas asignadas sigue apareciendo")
        void productoSinTallasApareceEnElCatalogo() {
            // Con rutas implicitas (pt.talla.nombre) HQL genera INNER JOIN y
            // estos productos desaparecian del catalogo entero.
            producto("Gorra sin tallas", mujer, polo);

            assertEquals(5, buscar(null, null, null, null, 0, 20).getTotalElements());
            assertEquals(1, buscar("gorra", null, null, null, 0, 20).getTotalElements());
        }

        @Test
        @DisplayName("Un producto sin categoria ni tipo tampoco se pierde")
        void productoSinCategoriaApareceEnElCatalogo() {
            producto("Articulo suelto", null, null);

            assertEquals(5, buscar(null, null, null, null, 0, 20).getTotalElements());
            assertEquals(1, buscar("suelto", null, null, null, 0, 20).getTotalElements());
        }

        @Test
        @DisplayName("Un producto con varias tallas cuenta una sola vez")
        void sinDuplicadosPorElJoin() {
            // "Polo running" tiene talla M y L. Sin DISTINCT y sin
            // COUNT(DISTINCT p) apareceria dos veces y el total saldria inflado.
            Page<Producto> pagina = buscar("running", null, null, null, 0, 20);

            assertEquals(1, pagina.getTotalElements(), "el total no debe contar el producto dos veces");
            assertEquals(1, pagina.getContent().size(), "el producto no debe aparecer repetido");
        }
    }
}
