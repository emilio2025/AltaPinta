package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuarioId(Long usuarioId);
    Optional<Favorito> findByUsuarioIdAndProductoId(Long usuarioId, Long productoId);
    void deleteByUsuarioIdAndProductoId(Long usuarioId, Long productoId);
}

