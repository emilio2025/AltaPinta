package com.backend.AltaPinta;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ayudas para trabajar con importes en las pruebas.
 *
 * Los importes son BigDecimal, y ahí equals() no sirve para compararlos:
 * distingue 10.0 de 10.00 porque tiene en cuenta la escala. Lo que interesa
 * es el valor, y eso lo dice compareTo().
 */
public final class Importes {

    private Importes() {}

    /** Crea un importe a partir de su representación decimal. */
    public static BigDecimal imp(String valor) {
        return new BigDecimal(valor);
    }

    /** Comprueba que el importe vale lo esperado, sin importar la escala. */
    public static void assertImporte(String esperado, BigDecimal real) {
        assertImporte(esperado, real, null);
    }

    public static void assertImporte(String esperado, BigDecimal real, String mensaje) {
        BigDecimal valorEsperado = new BigDecimal(esperado);
        assertEquals(0, valorEsperado.compareTo(real),
                () -> (mensaje == null ? "" : mensaje + ": ")
                        + "se esperaba " + valorEsperado.toPlainString()
                        + " pero fue " + (real == null ? "null" : real.toPlainString()));
    }
}
