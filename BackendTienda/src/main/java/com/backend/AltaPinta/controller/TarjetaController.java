package com.backend.AltaPinta.controller;

import java.math.BigDecimal;

import com.backend.AltaPinta.dto.TarjetaResponse;
import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.model.Tarjeta;
import com.backend.AltaPinta.repository.ClienteRepository;
import com.backend.AltaPinta.repository.TarjetaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarjetas")
@CrossOrigin("*")
public class TarjetaController {

    private final TarjetaRepository tarjetaRepo;
    private final ClienteRepository clienteRepo;

    public TarjetaController(TarjetaRepository tarjetaRepo, ClienteRepository clienteRepo) {
        this.tarjetaRepo = tarjetaRepo;
        this.clienteRepo = clienteRepo;
    }

    // REGISTRAR TARJETA (CLIENTE)
    @PostMapping
    public TarjetaResponse registrar(@RequestBody Tarjeta tarjeta, Authentication auth) {

        Cliente cliente = clienteRepo.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (tarjetaRepo.existsByNumero(tarjeta.getNumero())) {
            throw new RuntimeException("La tarjeta ya está registrada");
        }

        tarjeta.setCliente(cliente);
        tarjeta.setSaldo(new BigDecimal("500.00"));
        tarjeta.setActiva(true);

        Tarjeta guardada = tarjetaRepo.save(tarjeta);

        return new TarjetaResponse(
                guardada.getId(),
                guardada.getNumero(),
                guardada.getTitular(),
                guardada.getSaldo()
        );
    }

    // LISTAR TARJETAS DEL CLIENTE
    @GetMapping
    public List<TarjetaResponse> misTarjetas(Authentication auth) {

        Cliente cliente = clienteRepo.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        return cliente.getTarjetas()
                .stream()
                .map(t -> new TarjetaResponse(
                        t.getId(),
                        t.getNumero(),
                        t.getTitular(),
                        t.getSaldo()
                ))
                .toList();
    }

    //  RECARGAR TARJETA (ADMIN)
    @PutMapping("/admin/recargar")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public TarjetaResponse recargar(
            @RequestParam String numero,
            @RequestParam BigDecimal monto
    ) {
        Tarjeta t = tarjetaRepo.findByNumero(numero)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));

        t.setSaldo(t.getSaldo().add(monto));

        Tarjeta actualizada = tarjetaRepo.save(t);

        return new TarjetaResponse(
                actualizada.getId(),
                actualizada.getNumero(),
                actualizada.getTitular(),
                actualizada.getSaldo()
        );
    }
}
