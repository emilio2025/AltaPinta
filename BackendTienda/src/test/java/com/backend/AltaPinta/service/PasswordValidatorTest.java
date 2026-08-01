package com.backend.AltaPinta.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RF035: la contrasena debe tener minimo 8 caracteres, una mayuscula,
 * una minuscula y un numero.
 */
class PasswordValidatorTest {

    @ParameterizedTest(name = "\"{0}\" es valida")
    @ValueSource(strings = {
            "Password1",        // caso base
            "Abcdefg1",         // exactamente 8 caracteres, el minimo
            "Contrasena2026",   // mas larga
            "aB3xxxxx",         // una sola mayuscula y un solo numero
            "Passw0rd!",        // los simbolos no estorban
            "  Passw0rd  "      // los espacios cuentan como caracteres validos
    })
    @DisplayName("Acepta contrasenas que cumplen los cuatro criterios")
    void aceptaPasswordsValidas(String password) {
        assertTrue(PasswordValidator.esValida(password),
                "Deberia aceptar: " + password);
    }

    @ParameterizedTest(name = "\"{0}\" es invalida")
    @ValueSource(strings = {
            "Abcdef1",          // 7 caracteres: uno menos del minimo
            "password1",        // sin mayuscula
            "PASSWORD1",        // sin minuscula
            "Passwordd",        // sin numero
            "12345678",         // solo numeros
            "ABCDEFGH",         // solo mayusculas
            "abcdefgh",         // solo minusculas
            ""                  // vacia
    })
    @DisplayName("Rechaza contrasenas que incumplen algun criterio")
    void rechazaPasswordsInvalidas(String password) {
        assertFalse(PasswordValidator.esValida(password),
                "Deberia rechazar: " + password);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Rechaza null sin lanzar NullPointerException")
    void rechazaNull(String password) {
        assertFalse(PasswordValidator.esValida(password));
    }

    @Test
    @DisplayName("Un salto de linea no permite colar una parte invalida")
    void rechazaSaltoDeLinea() {
        // El regex usa "." que por defecto no cruza saltos de linea, pero
        // conviene fijar el comportamiento por si alguien anade el flag DOTALL.
        assertFalse(PasswordValidator.esValida("corta\nPassword1"));
    }
}
