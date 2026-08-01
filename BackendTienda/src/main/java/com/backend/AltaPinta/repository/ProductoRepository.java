package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Producto;
import com.backend.AltaPinta.model.Talla;
import com.backend.AltaPinta.model.TipoPrenda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Búsqueda paginada del catálogo con filtros opcionales.
     *
     * Cada filtro se ignora cuando llega null, así que la misma consulta sirve
     * para el listado completo y para cualquier combinación de criterios. Antes
     * el frontend se traía el catálogo entero y filtraba en el navegador.
     *
     * La consulta de conteo se declara aparte porque el LEFT JOIN con las tallas
     * duplica filas: sin COUNT(DISTINCT p) el total de páginas saldría inflado.
     *
     * Todas las asociaciones se recorren con LEFT JOIN explícito. Escribirlas
     * como rutas (p.categoria.nombre, pt.talla.nombre) parece equivalente pero
     * no lo es: HQL las traduce a INNER JOIN, y eso dejaba fuera del catálogo
     * a los productos sin tallas, sin categoría o sin tipo asignados.
     */
    @Query(value = """
        SELECT DISTINCT p
        FROM Producto p
        LEFT JOIN p.categoria c
        LEFT JOIN p.tipoPrenda tp
        LEFT JOIN p.tallasDisponibles pt
        LEFT JOIN pt.talla t
        WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
          AND (:categoria IS NULL OR LOWER(c.nombre) = LOWER(:categoria))
          AND (:tipo IS NULL OR LOWER(tp.nombre) = LOWER(:tipo))
          AND (:talla IS NULL OR t.nombre = :talla)
        """,
        countQuery = """
        SELECT COUNT(DISTINCT p)
        FROM Producto p
        LEFT JOIN p.categoria c
        LEFT JOIN p.tipoPrenda tp
        LEFT JOIN p.tallasDisponibles pt
        LEFT JOIN pt.talla t
        WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
          AND (:categoria IS NULL OR LOWER(c.nombre) = LOWER(:categoria))
          AND (:tipo IS NULL OR LOWER(tp.nombre) = LOWER(:tipo))
          AND (:talla IS NULL OR t.nombre = :talla)
        """)
    Page<Producto> buscar(
            @Param("nombre") String nombre,
            @Param("categoria") String categoria,
            @Param("tipo") String tipo,
            @Param("talla") String talla,
            Pageable pageable
    );

    List<Producto> findByCategoriaNombre(String categoria);
    List<Producto> findByTipoPrendaNombre(String tipoPrenda);

    @Query("""
        SELECT DISTINCT p.tipoPrenda
        FROM Producto p
        JOIN p.categoria c
        WHERE LOWER(c.nombre) = LOWER(:categoria)
    """)
    List<TipoPrenda> findTiposByCategoria(@Param("categoria") String categoria);

    // Tallas disponibles entre los productos de una categoría (para el filtro de catálogo)
    @Query("""
      SELECT DISTINCT pt.talla
      FROM ProductoTalla pt
      WHERE pt.producto.categoria.nombre = :categoria
    """)
    List<Talla> findTallasByCategoria(@Param("categoria") String categoria);

    // Productos que ofrecen una talla específica (en cualquier categoría)
    @Query("""
      SELECT DISTINCT pt.producto
      FROM ProductoTalla pt
      WHERE pt.talla.nombre = :talla
    """)
    List<Producto> findByTallaNombre(@Param("talla") String talla);
}
