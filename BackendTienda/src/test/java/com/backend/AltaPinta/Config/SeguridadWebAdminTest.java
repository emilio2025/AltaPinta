package com.backend.AltaPinta.Config;

import com.backend.AltaPinta.controller.AuditoriaController;
import com.backend.AltaPinta.controller.CategoriaController;
import com.backend.AltaPinta.controller.ReporteController;
import com.backend.AltaPinta.controller.TallaController;
import com.backend.AltaPinta.controller.TipoPrendaController;
import com.backend.AltaPinta.repository.*;

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

import java.math.BigDecimal;
import java.util.List;

import static com.backend.AltaPinta.Config.PermisosAssert.denegado;
import static com.backend.AltaPinta.Config.PermisosAssert.permitido;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Autorizacion de la zona de administracion: auditoria, reportes y
 * escritura del catalogo (categorias, tallas y tipos de prenda).
 *
 * La escritura del catalogo se probaba mal antes: SecurityConfig declaraba
 * /categorias/** y /tallas/** como permitAll para TODOS los metodos, asi que
 * un anonimo podia crear categorias y tallas. Ahora publico es solo el GET.
 */
@WebMvcTest(controllers = {
        AuditoriaController.class,
        ReporteController.class,
        CategoriaController.class,
        TallaController.class,
        TipoPrendaController.class
})
@Import({SecurityConfig.class, JwtAuthFilter.class})
class SeguridadWebAdminTest {

    @Autowired private MockMvc mockMvc;

    // Cadena de seguridad
    @MockBean private JwtUtil jwtUtil;
    @MockBean private UserDetailsService userDetailsService;

    // Repositorios que usan los controladores bajo prueba
    @MockBean private AuditoriaRepository auditoriaRepo;
    @MockBean private PedidoRepository pedidoRepo;
    @MockBean private CategoriaRepository categoriaRepo;
    @MockBean private TallaRepository tallaRepo;
    @MockBean private ProductoRepository productoRepo;
    @MockBean private TipoPrendaRepository tipoPrendaRepo;

    private static final String CATEGORIA_JSON = "{\"nombre\":\"Intrusa\"}";
    private static final String TALLA_JSON = "{\"nombre\":\"XXL\"}";

    // ============================================================
    @Nested
    @DisplayName("Auditoria: solo administradores")
    class Auditoria {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede leer el registro de auditoria")
        void anonimoNoLee() throws Exception {
            denegado(mockMvc.perform(get("/auditoria")));
        }

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente normal no puede leer el registro de auditoria")
        void clienteNoLee() throws Exception {
            denegado(mockMvc.perform(get("/auditoria")));
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("Un administrador si puede leerlo")
        void adminLee() throws Exception {
            when(auditoriaRepo.findAll()).thenReturn(List.of());

            permitido(mockMvc.perform(get("/auditoria")));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Reportes de ventas: solo administradores")
    class Reportes {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede ver el total vendido")
        void anonimoNoVeVentas() throws Exception {
            denegado(mockMvc.perform(get("/reportes/total")));
        }

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente normal no puede ver las ventas de la tienda")
        void clienteNoVeVentas() throws Exception {
            denegado(mockMvc.perform(get("/reportes/total")));
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("Un administrador si puede ver el total vendido")
        void adminVeVentas() throws Exception {
            when(pedidoRepo.totalVendido()).thenReturn(BigDecimal.ZERO);

            permitido(mockMvc.perform(get("/reportes/total")));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Catalogo: lectura publica, escritura solo admin")
    class EscrituraCatalogo {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo SI puede leer las categorias")
        void anonimoLeeCategorias() throws Exception {
            when(categoriaRepo.findAll()).thenReturn(List.of());

            permitido(mockMvc.perform(get("/categorias")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo NO puede crear categorias")
        void anonimoNoCreaCategorias() throws Exception {
            denegado(mockMvc.perform(post("/categorias")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(CATEGORIA_JSON)));
        }

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente normal NO puede crear categorias")
        void clienteNoCreaCategorias() throws Exception {
            denegado(mockMvc.perform(post("/categorias")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(CATEGORIA_JSON)));
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("Un administrador si puede crear categorias")
        void adminCreaCategorias() throws Exception {
            permitido(mockMvc.perform(post("/categorias")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(CATEGORIA_JSON)));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo SI puede leer las tallas")
        void anonimoLeeTallas() throws Exception {
            when(tallaRepo.findAll()).thenReturn(List.of());

            permitido(mockMvc.perform(get("/tallas")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo NO puede crear tallas")
        void anonimoNoCreaTallas() throws Exception {
            denegado(mockMvc.perform(post("/tallas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TALLA_JSON)));
        }

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente normal NO puede crear tallas")
        void clienteNoCreaTallas() throws Exception {
            denegado(mockMvc.perform(post("/tallas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TALLA_JSON)));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo SI puede leer los tipos de prenda")
        void anonimoLeeTipos() throws Exception {
            when(tipoPrendaRepo.findAll()).thenReturn(List.of());

            permitido(mockMvc.perform(get("/tipos")));
        }

        @Test
        @WithMockUser(authorities = "ROLE_USER")
        @DisplayName("Un cliente normal NO puede crear tipos de prenda")
        void clienteNoCreaTipos() throws Exception {
            denegado(mockMvc.perform(post("/tipos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"Casaca\"}")));
        }
    }
}
