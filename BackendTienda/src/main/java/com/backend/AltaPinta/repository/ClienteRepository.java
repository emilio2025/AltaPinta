package com.backend.AltaPinta.repository;

import com.backend.AltaPinta.model.Cliente;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreo(String correo);

    /**
     * Busca el cliente bloqueando su fila hasta el final de la transacción.
     *
     * Se usa al confirmar un pedido para que dos peticiones del mismo
     * cliente —el clásico doble clic en "pagar"— se resuelvan una detrás de
     * otra en lugar de a la vez.
     *
     * Antes esto se hacía con un synchronized sobre un mapa en memoria, que
     * tenía tres problemas: solo protegía dentro de una instancia, el mapa
     * crecía sin límite, y sobre todo el bloqueo se tomaba dentro de la
     * transacción, así que el segundo hilo seguía viendo su instantánea
     * anterior y encontraba el carrito lleno igualmente.
     *
     * Al ser una lectura con bloqueo, InnoDB devuelve la última versión
     * confirmada y no la de la instantánea: el segundo hilo ve el carrito
     * ya vaciado por el primero.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cliente c WHERE c.correo = :correo")
    Optional<Cliente> findByCorreoBloqueando(@Param("correo") String correo);
    boolean existsByCorreo(String correo);
    boolean existsByDni(String dni);
    Optional<Cliente> findByTokenResetPassword(String token);
}
