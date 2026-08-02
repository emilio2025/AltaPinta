package com.backend.AltaPinta.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reglas sobre qué datos de tarjeta se pueden guardar.
 *
 * Estas pruebas miran la propia estructura de la entidad en vez de su
 * comportamiento, que es raro, pero aquí lo que hay que proteger es
 * precisamente eso: que nadie vuelva a añadir un campo que no debe
 * existir. Un fallo aquí no es un error de cálculo, es un incumplimiento.
 */
class TarjetaTest {

    /** Nombres que suelen usarse para el código de verificación de la tarjeta. */
    private static final List<String> NOMBRES_PROHIBIDOS =
            Arrays.asList("cvv", "cvc", "cvv2", "cid", "csc", "codigoseguridad", "codigoverificacion");

    @Test
    @DisplayName("La entidad Tarjeta no guarda el codigo de verificacion")
    void noGuardaElCodigoDeVerificacion() {
        for (Field campo : Tarjeta.class.getDeclaredFields()) {
            String nombre = campo.getName().toLowerCase(Locale.ROOT).replace("_", "");

            assertFalse(NOMBRES_PROHIBIDOS.contains(nombre),
                    () -> "Tarjeta no puede tener el campo '" + campo.getName() + "'. "
                        + "PCI DSS prohibe almacenar el codigo de verificacion despues de "
                        + "autorizar el pago, y no admite cifrarlo ni guardar su hash. "
                        + "Si hace falta para cobrar, pasalo por la peticion y usalo en memoria.");
        }
    }

    @Test
    @DisplayName("Sigue guardando lo que si necesita para operar")
    void conservaLosCamposNecesarios() {
        List<String> campos = Arrays.stream(Tarjeta.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        // Si alguno de estos desapareciera, el alta de tarjetas o el cobro
        // dejarian de funcionar.
        assertTrue(campos.contains("numero"), "falta numero");
        assertTrue(campos.contains("titular"), "falta titular");
        assertTrue(campos.contains("fechaVencimiento"), "falta fechaVencimiento");
        assertTrue(campos.contains("saldo"), "falta saldo");
    }
}
