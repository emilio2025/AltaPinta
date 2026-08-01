package com.backend.AltaPinta.Config;

import com.backend.AltaPinta.controller.ClienteController;
import com.backend.AltaPinta.controller.EnvioController;
import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.repository.ClienteRepository;
import com.backend.AltaPinta.repository.EnvioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static com.backend.AltaPinta.Config.PermisosAssert.denegado;
import static com.backend.AltaPinta.Config.PermisosAssert.permitido;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Autorizacion de perfil y envios.
 *
 * Ademas de comprobar quien puede entrar, aqui se verifica algo que la
 * cadena de filtros no cubre: que un usuario con sesion iniciada no pueda
 * leer ni modificar el perfil de OTRO pasando su correo por parametro.
 */
@WebMvcTest(controllers = {
        ClienteController.class,
        EnvioController.class
})
@Import({SecurityConfig.class, JwtAuthFilter.class})
class SeguridadWebPerfilTest {

    @Autowired private MockMvc mockMvc;

    // Cadena de seguridad
    @MockBean private JwtUtil jwtUtil;
    @MockBean private UserDetailsService userDetailsService;

    // Dependencias de los controladores bajo prueba
    @MockBean private ClienteRepository clienteRepo;
    @MockBean private EnvioRepository envioRepo;

    private static final String OTRO_CLIENTE = "victima@unamba.edu.pe";
    private static final String CLIENTE = "cliente@unamba.edu.pe";

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(2L);
        cliente.setCorreo(CLIENTE);
    }

    // ============================================================
    @Nested
    @DisplayName("Perfil del cliente")
    class Perfil {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede leer un perfil")
        void anonimoNoLeePerfil() throws Exception {
            denegado(mockMvc.perform(get("/cliente/me")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede modificar un perfil")
        void anonimoNoModificaPerfil() throws Exception {
            denegado(mockMvc.perform(put("/cliente/actualizar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"Intruso\"}")));
        }

        @Test
        @WithMockUser(username = CLIENTE, authorities = "ROLE_USER")
        @DisplayName("Un cliente si puede leer su propio perfil")
        void clienteLeeSuPerfil() throws Exception {
            when(clienteRepo.findByCorreo(CLIENTE)).thenReturn(Optional.of(cliente));

            permitido(mockMvc.perform(get("/cliente/me")));
        }

        @Test
        @WithMockUser(username = CLIENTE, authorities = "ROLE_USER")
        @DisplayName("El perfil devuelto es el del usuario de la sesion, no uno pedido por parametro")
        void elPerfilSaleDeLaSesion() throws Exception {
            when(clienteRepo.findByCorreo(CLIENTE)).thenReturn(Optional.of(cliente));

            // Aunque se intente colar el correo de otro por parametro, el
            // controlador solo mira auth.getName().
            mockMvc.perform(get("/cliente/me").param("correo", OTRO_CLIENTE));

            verify(clienteRepo).findByCorreo(CLIENTE);
            verify(clienteRepo, never()).findByCorreo(OTRO_CLIENTE);
        }

        @Test
        @WithMockUser(username = CLIENTE, authorities = "ROLE_USER")
        @DisplayName("Al actualizar, solo se toca el perfil de la sesion")
        void actualizarSoloAfectaAlPropio() throws Exception {
            when(clienteRepo.findByCorreo(CLIENTE)).thenReturn(Optional.of(cliente));

            mockMvc.perform(put("/cliente/actualizar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"Nuevo nombre\",\"dni\":\"12345678\"}"));

            verify(clienteRepo).findByCorreo(CLIENTE);
            verify(clienteRepo, never()).findByCorreo(OTRO_CLIENTE);
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Envios")
    class Envios {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede consultar las opciones de envio")
        void anonimoNoConsultaEnvios() throws Exception {
            denegado(mockMvc.perform(get("/envio")));
        }

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente con sesion si puede consultarlas")
        void clienteConsultaEnvios() throws Exception {
            when(envioRepo.findAll()).thenReturn(List.of());

            permitido(mockMvc.perform(get("/envio")));
        }
    }
}
