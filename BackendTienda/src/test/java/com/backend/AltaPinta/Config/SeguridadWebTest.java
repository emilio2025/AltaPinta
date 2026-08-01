package com.backend.AltaPinta.Config;

import com.backend.AltaPinta.controller.CategoriaController;
import com.backend.AltaPinta.controller.PedidoController;
import com.backend.AltaPinta.controller.ProductoController;
import com.backend.AltaPinta.repository.*;
import com.backend.AltaPinta.service.*;

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
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de las reglas de autorizacion: quien puede llamar a que endpoint.
 *
 * Se levanta solo la capa web con la cadena de filtros real (SecurityConfig),
 * y el usuario se simula con @WithMockUser en lugar de un token JWT: lo que se
 * prueba aqui son las REGLAS de acceso, no la generacion del token (eso lo
 * cubre JwtUtilTest).
 *
 * Los codigos exactos de exito varian segun el endpoint (200, 400...), asi que
 * las comprobaciones de "permitido" verifican que NO sea 401 ni 403.
 */
@WebMvcTest(controllers = {
        ProductoController.class,
        PedidoController.class,
        CategoriaController.class
})
@Import({SecurityConfig.class, JwtAuthFilter.class})
class SeguridadWebTest {

    @Autowired private MockMvc mockMvc;

    // Dependencias de la cadena de seguridad
    @MockBean private JwtUtil jwtUtil;
    @MockBean private UserDetailsService userDetailsService;

    // Dependencias de ProductoController
    @MockBean private ProductoRepository productoRepo;
    @MockBean private ProductoTallaRepository productoTallaRepo;
    @MockBean private ProductoImagenRepository productoImagenRepo;
    @MockBean private TallaRepository tallaRepo;
    @MockBean private ImagenService imagenService;
    @MockBean private AuditoriaService auditoriaService;

    // Dependencias de PedidoController
    @MockBean private PedidoService pedidoService;
    @MockBean private PedidoRepository pedidoRepo;
    @MockBean private PedidoDetalleRepository detalleRepo;
    @MockBean private FacturaPdfService facturaPdfService;
    @MockBean private EmailService emailService;

    // Dependencias de CategoriaController
    @MockBean private CategoriaRepository categoriaRepo;

    /** Ni 401 ni 403: la peticion atraveso la capa de seguridad. */
    private void permitido(ResultActions resultado) throws Exception {
        resultado.andExpect(status().is(not401ni403()));
    }

    private static org.hamcrest.Matcher<Integer> not401ni403() {
        return org.hamcrest.Matchers.not(org.hamcrest.Matchers.isOneOf(401, 403));
    }

    /** Rechazado por la capa de seguridad. */
    private void denegado(ResultActions resultado) throws Exception {
        resultado.andExpect(status().is(
                org.hamcrest.Matchers.isOneOf(401, 403)));
    }

    // ============================================================
    @Nested
    @DisplayName("Catalogo publico (sin iniciar sesion)")
    class Publico {

        @Test
        @WithAnonymousUser
        @DisplayName("Cualquiera puede ver el listado de productos")
        void listarProductosEsPublico() throws Exception {
            org.mockito.Mockito.when(productoRepo.findAll()).thenReturn(List.of());

            permitido(mockMvc.perform(get("/productos")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Cualquiera puede ver las categorias")
        void listarCategoriasEsPublico() throws Exception {
            org.mockito.Mockito.when(categoriaRepo.findAll()).thenReturn(List.of());

            permitido(mockMvc.perform(get("/categorias")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Cualquiera puede filtrar productos por categoria")
        void filtrarPorCategoriaEsPublico() throws Exception {
            permitido(mockMvc.perform(get("/productos/categoria/Mujer")));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Gestion de productos: solo administradores")
    class GestionProductos {

        private static final String PRODUCTO_JSON = """
                {"nombre":"Polo deportivo","precio":100.0,"descripcion":"prueba"}
                """;

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede crear productos")
        void anonimoNoCrea() throws Exception {
            denegado(mockMvc.perform(post("/productos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(PRODUCTO_JSON)));
        }

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente normal NO puede crear productos")
        void usuarioNoCrea() throws Exception {
            denegado(mockMvc.perform(post("/productos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(PRODUCTO_JSON)));
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("Un administrador si puede crear productos")
        void adminSiCrea() throws Exception {
            permitido(mockMvc.perform(post("/productos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(PRODUCTO_JSON)));
        }

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente normal NO puede eliminar productos")
        void usuarioNoElimina() throws Exception {
            denegado(mockMvc.perform(delete("/productos/1")));
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("Un administrador si puede eliminar productos")
        void adminSiElimina() throws Exception {
            permitido(mockMvc.perform(delete("/productos/1")));
        }

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente normal NO puede modificar productos")
        void usuarioNoModifica() throws Exception {
            denegado(mockMvc.perform(put("/productos/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(PRODUCTO_JSON)));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Pedidos: hay que haber iniciado sesion")
    class Pedidos {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede ver pedidos ajenos")
        void anonimoNoVePedidos() throws Exception {
            denegado(mockMvc.perform(get("/pedido/mis-pedidos")));
        }

        @Test
        @WithMockUser(username = "cliente@unamba.edu.pe", authorities = "ROLE_USER")
        @DisplayName("Un cliente si puede ver SUS pedidos")
        void clienteVeSusPedidos() throws Exception {
            org.mockito.Mockito.when(pedidoRepo.findByClienteCorreo("cliente@unamba.edu.pe"))
                    .thenReturn(List.of());

            permitido(mockMvc.perform(get("/pedido/mis-pedidos")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede confirmar un pedido")
        void anonimoNoConfirma() throws Exception {
            denegado(mockMvc.perform(post("/pedido/confirmar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"envioId\":1,\"tarjetaId\":1}")));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Seguridad a nivel de metodo (@PreAuthorize)")
    class SeguridadDeMetodo {

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente NO puede listar los pedidos de todos")
        void usuarioNoListaTodosLosPedidos() throws Exception {
            // Este endpoint no esta restringido en SecurityConfig por URL:
            // lo protege @PreAuthorize en el controlador.
            denegado(mockMvc.perform(get("/pedido/todos")));
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("Un administrador si puede listar los pedidos de todos")
        void adminListaTodosLosPedidos() throws Exception {
            org.mockito.Mockito.when(pedidoRepo.findAll()).thenReturn(List.of());

            permitido(mockMvc.perform(get("/pedido/todos")));
        }

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente NO puede cambiar el estado de un pedido")
        void usuarioNoCambiaEstado() throws Exception {
            denegado(mockMvc.perform(put("/pedido/1/estado")
                    .param("nuevoEstado", "ENVIADO")));
        }
    }
}
