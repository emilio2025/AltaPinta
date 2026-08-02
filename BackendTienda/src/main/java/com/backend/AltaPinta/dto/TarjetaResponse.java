package com.backend.AltaPinta.dto;

import java.math.BigDecimal;

public record TarjetaResponse(
        Long id,
        String numero,
        String titular,
        BigDecimal saldo
) {}

