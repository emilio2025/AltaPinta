package com.backend.AltaPinta.Config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de generacion y validacion de tokens JWT.
 *
 * El secreto se inyecta por reflexion porque en produccion viene de
 * @Value("${jwt.secret}") y aqui no se levanta el contexto de Spring.
 */
class JwtUtilTest {

    // Secreto de prueba: HS512 exige una clave de al menos 64 bytes.
    private static final String SECRETO_TEST =
            "clave-solo-para-pruebas-que-debe-superar-los-64-bytes-exigidos-por-HS512-0123456789";

    private static final long UN_DIA_MS = 86_400_000L;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRETO_TEST);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", UN_DIA_MS);
    }

    @Test
    @DisplayName("El correo que se mete en el token es el que sale")
    void generaYRecuperaElCorreo() {
        String token = jwtUtil.generateToken("cliente@unamba.edu.pe");

        assertEquals("cliente@unamba.edu.pe", jwtUtil.getCorreoFromToken(token));
    }

    @Test
    @DisplayName("Un token recien generado es valido")
    void tokenRecienGeneradoEsValido() {
        String token = jwtUtil.generateToken("cliente@unamba.edu.pe");

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("Un token con la firma alterada se rechaza")
    void rechazaFirmaAlterada() {
        String token = jwtUtil.generateToken("cliente@unamba.edu.pe");
        String[] partes = token.split("\\.");
        String firma = partes[2];

        // Se altera un caracter del MEDIO de la firma, no el ultimo: una firma
        // HS512 son 64 bytes = 512 bits, pero sus 86 caracteres Base64URL
        // codifican 516, asi que varios caracteres finales distintos decodifican
        // a los mismos bytes y cambiar el ultimo no altera la firma real.
        int medio = firma.length() / 2;
        char sustituto = firma.charAt(medio) == 'A' ? 'B' : 'A';
        String firmaAlterada = firma.substring(0, medio) + sustituto + firma.substring(medio + 1);

        assertFalse(jwtUtil.validateToken(partes[0] + "." + partes[1] + "." + firmaAlterada));
    }

    @Test
    @DisplayName("No se puede suplantar a otro usuario cambiando el payload")
    void rechazaSuplantacionDeIdentidad() {
        // Escenario real: un usuario intercepta su propio token y le cambia el
        // "sub" por el del administrador, reutilizando la firma original.
        String token = jwtUtil.generateToken("cliente@unamba.edu.pe");
        String[] partes = token.split("\\.");

        String payloadFalso = Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"admin@altapinta.com\"}".getBytes(StandardCharsets.UTF_8));

        String falsificado = partes[0] + "." + payloadFalso + "." + partes[2];

        assertFalse(jwtUtil.validateToken(falsificado),
                "La firma no cubre el payload manipulado: debe rechazarse");
    }

    @Test
    @DisplayName("Un token firmado con otro secreto se rechaza")
    void rechazaTokenDeOtroEmisor() {
        JwtUtil otroEmisor = new JwtUtil();
        ReflectionTestUtils.setField(otroEmisor, "secret",
                "OTRA-clave-distinta-igual-de-larga-para-HS512-9876543210-abcdefghijklmnop");
        ReflectionTestUtils.setField(otroEmisor, "expirationMs", UN_DIA_MS);

        String tokenAjeno = otroEmisor.generateToken("intruso@ejemplo.com");

        assertFalse(jwtUtil.validateToken(tokenAjeno),
                "Un token firmado con otra clave no debe aceptarse");
    }

    @Test
    @DisplayName("Un token caducado se rechaza")
    void rechazaTokenCaducado() {
        JwtUtil emisorCaducado = new JwtUtil();
        ReflectionTestUtils.setField(emisorCaducado, "secret", SECRETO_TEST);
        // Expiracion negativa: el token nace ya vencido.
        ReflectionTestUtils.setField(emisorCaducado, "expirationMs", -1000L);

        String tokenCaducado = emisorCaducado.generateToken("cliente@unamba.edu.pe");

        assertFalse(jwtUtil.validateToken(tokenCaducado));
    }

    @Test
    @DisplayName("Texto que no es un JWT se rechaza sin lanzar excepcion")
    void rechazaBasura() {
        assertFalse(jwtUtil.validateToken("esto-no-es-un-token"));
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    @DisplayName("Dos usuarios distintos obtienen tokens distintos")
    void tokensDistintosPorUsuario() {
        String tokenA = jwtUtil.generateToken("a@unamba.edu.pe");
        String tokenB = jwtUtil.generateToken("b@unamba.edu.pe");

        assertNotEquals(tokenA, tokenB);
        assertEquals("a@unamba.edu.pe", jwtUtil.getCorreoFromToken(tokenA));
        assertEquals("b@unamba.edu.pe", jwtUtil.getCorreoFromToken(tokenB));
    }
}
