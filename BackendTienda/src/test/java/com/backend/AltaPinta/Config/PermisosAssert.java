package com.backend.AltaPinta.Config;

import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Comprobaciones compartidas por las pruebas de autorizacion.
 *
 * Se mira solo si la peticion atraveso o no la capa de seguridad, no el
 * codigo de exito concreto: segun el endpoint puede ser 200, 400 (falta el
 * cuerpo) o 500 (el repositorio es un doble), y eso es irrelevante aqui.
 */
final class PermisosAssert {

    private PermisosAssert() {}

    /** La peticion paso la capa de seguridad: ni 401 ni 403. */
    static void permitido(ResultActions resultado) throws Exception {
        resultado.andExpect(status().is(not(isOneOf(401, 403))));
    }

    /** La capa de seguridad rechazo la peticion. */
    static void denegado(ResultActions resultado) throws Exception {
        resultado.andExpect(status().is(isOneOf(401, 403)));
    }
}
