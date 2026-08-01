package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.TipoPrenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TipoPrendaRepository extends JpaRepository<TipoPrenda, Long> {

}
