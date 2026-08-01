package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Producto;
import com.backend.AltaPinta.model.Talla;
import com.backend.AltaPinta.model.TipoPrenda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
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
