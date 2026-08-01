package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByDni(String dni);
    Optional<Cliente> findByTokenResetPassword(String token);
}
