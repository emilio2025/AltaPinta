package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.repository.PedidoRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequestMapping("/reportes")
@CrossOrigin("*")
public class ReporteController {

    private final PedidoRepository pedidoRepo;

    public ReporteController(PedidoRepository pedidoRepo) {
        this.pedidoRepo = pedidoRepo;
    }

    // TOTAL GENERAL
    @GetMapping("/total")
    public Map<String, Object> totalVendido() {
        Map<String, Object> res = new HashMap<>();
        res.put("totalVendido", pedidoRepo.totalVendido());
        res.put("totalPedidos", pedidoRepo.totalPedidosPagados());
        return res;
    }

    // POR DÍA
    @GetMapping("/dia")
    public Map<String, Object> ventasPorDia(@RequestParam String fecha) {
        LocalDate f = LocalDate.parse(fecha);

        Map<String, Object> res = new HashMap<>();
        res.put("fecha", fecha);
        res.put("total", pedidoRepo.ventasPorDia(f));
        return res;
    }

    // POR MES
    @GetMapping("/mes")
    public Map<String, Object> ventasPorMes(
            @RequestParam int mes,
            @RequestParam int anio
    ) {
        Map<String, Object> res = new HashMap<>();
        res.put("mes", mes);
        res.put("anio", anio);
        res.put("total", pedidoRepo.ventasPorMes(mes, anio));
        return res;
    }

    // POR AÑO
    @GetMapping("/anio")
    public Map<String, Object> ventasPorAnio(@RequestParam int anio) {
        Map<String, Object> res = new HashMap<>();
        res.put("anio", anio);
        res.put("total", pedidoRepo.ventasPorAnio(anio));
        return res;
    }
}
