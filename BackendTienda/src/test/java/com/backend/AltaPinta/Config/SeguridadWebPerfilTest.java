package com.backend.AltaPinta.Config;

import com.backend.AltaPinta.controller.ClienteController;
import com.backend.AltaPinta.controller.EnvioController;
import com.backend.AltaPinta.controller.PagoController;
import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.model.Pedido;
import com.backend.AltaPinta.repository.ClienteRepository;
import com.backend.AltaPinta.repository.EnvioRepository;
import com.backend.AltaPinta.repository.PedidoRepository;
import com.backend.AltaPinta.service.PagoService;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Autorizacion de perfil, envios y pagos.
 *
 * Ademas de comprobar quien puede entrar, aqui se verifica algo que la
 * cadena de filtros no cubre: que un usuario con sesion iniciada no pueda
 * operar sobre datos de OTRO usuario pasando su identificador.
 */
@WebMvcTest(controllers = {
        ClienteController.class,
        EnvioController.class,
        PagoController.class
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
    @MockBean private PedidoRepository pedidoRepo;
    @MockBean private PagoService pagoService;

    private static final String VICTIMA = "victima@unamba.edu.pe";
    private static final String ATACANTE = "atacante@unamba.edu.pe";

    private Cliente atacante;

    @BeforeEach
    void setUp() {
        atacante = new Cliente();
        atacante.setId(2L);
        atacante.setCorreo(ATACANTE);
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
        @WithMockUser(username = ATACANTE, authorities = "ROLE_USER")
        @DisplayName("Un cliente si puede leer su propio perfil")
        void clienteLeeSuPerfil() throws Exception {
            when(clienteRepo.findByCorreo(ATACANTE)).thenReturn(Optional.of(atacante));

            permitido(mockMvc.perform(get("/cliente/me")));
        }

        @Test
        @WithMockUser(username = ATACANTE, authorities = "ROLE_USER")
        @DisplayName("El perfil devuelto es el del usuario de la sesion, no uno pedido por parametro")
        void elPerfilSaleDeLaSesion() throws Exception {
            when(clienteRepo.findByCorreo(ATACANTE)).thenReturn(Optional.of(atacante));

            // Aunque se intente colar el correo de otro por parametro, el
            // controlador solo mira auth.getName().
            mockMvc.perform(get("/cliente/me").param("correo", VICTIMA));

            verify(clienteRepo).findByCorreo(ATACANTE);
            verify(clienteRepo, never()).findByCorreo(VICTIMA);
        }

        @Test
        @WithMockUser(username = ATACANTE, authorities = "ROLE_USER")
        @DisplayName("Al actualizar, solo se toca el perfil de la sesion")
        void actualizarSoloAfectaAlPropio() throws Exception {
            when(clienteRepo.findByCorreo(ATACANTE)).thenReturn(Optional.of(atacante));

            mockMvc.perform(put("/cliente/actualizar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"Nuevo nombre\",\"dni\":\"12345678\"}"));

            verify(clienteRepo).findByCorreo(ATACANTE);
            verify(clienteRepo, never()).findByCorreo(VICTIMA);
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

    // ============================================================
    @Nested
    @DisplayName("Pagos")
    class Pagos {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede procesar un pago")
        void anonimoNoPaga() throws Exception {
            denegado(mockMvc.perform(post("/pago/procesar")
                    .param("pedidoId", "1")
                    .param("numeroTarjeta", "4111111111111111")
                    .param("cvv", "123")
                    .param("fechaVencimiento", "12/30")));
        }

        @Test
        @WithMockUser(username = ATACANTE, authorities = "ROLE_USER")
        @DisplayName("Un cliente si puede pagar SU pedido")
        void clientePagaSuPedido() throws Exception {
            Pedido propio = new Pedido();
            propio.setId(10L);
            propio.setTotal(215.0);

            when(pedidoRepo.findByIdAndClienteCorreo(10L, ATACANTE)).thenReturn(Optional.of(propio));
            when(clienteRepo.findByCorreo(ATACANTE)).thenReturn(Optional.of(atacante));
            when(pagoService.procesarPago(any(), any(), anyString(), anyString(), anyString(), anyDouble()))
                    .thenReturn(true);

            permitido(mockMvc.perform(post("/pago/procesar")
                    .param("pedidoId", "10")
                    .param("numeroTarjeta", "4111111111111111")
                    .param("cvv", "123")
                    .param("fechaVencimiento", "12/30")));

            verify(pagoService).procesarPago(any(), any(), anyString(), anyString(), anyString(), anyDouble());
        }

        @Test
        @WithMockUser(username = ATACANTE, authorities = "ROLE_USER")
        @DisplayName("Un cliente NO puede operar sobre el pedido de otro")
        void noSePuedePagarElPedidoDeOtro() throws Exception {
            // Escenario del fallo corregido: el atacante envia el pedidoId de la
            // victima con su propia tarjeta. Si la tarjeta no tuviera saldo, el
            // pedido ajeno acababa marcado como RECHAZADO.
            // La busqueda ahora filtra tambien por el correo de la sesion, asi
            // que el pedido de la victima sencillamente no aparece.
            when(pedidoRepo.findByIdAndClienteCorreo(99L, ATACANTE)).thenReturn(Optional.empty());

            mockMvc.perform(post("/pago/procesar")
                    .param("pedidoId", "99")
                    .param("numeroTarjeta", "4111111111111111")
                    .param("cvv", "123")
                    .param("fechaVencimiento", "12/30"));

            // Lo importante no es el codigo devuelto, sino que no pasa nada:
            verify(pagoService, never())
                    .procesarPago(any(), any(), anyString(), anyString(), anyString(), anyDouble());
            verify(pedidoRepo, never()).save(any(Pedido.class));
            // Y jamas se busca el pedido solo por id, sin filtrar por dueño.
            verify(pedidoRepo, never()).findById(any());
        }
    }
}
