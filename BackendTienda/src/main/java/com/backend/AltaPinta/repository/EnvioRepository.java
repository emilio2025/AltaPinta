package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnvioRepository extends JpaRepository<Envio, Long> {

    //List<Envio> findByClienteId(Long clienteId);
    //Envio findByLugar(String lugar);
}
