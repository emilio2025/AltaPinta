package com.backend.AltaPinta.dto;

public record TarjetaResponse(
        Long id,
        String numero,
        String titular,
        Double saldo
) {}

