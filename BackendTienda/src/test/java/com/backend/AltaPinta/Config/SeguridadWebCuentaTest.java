package com.backend.AltaPinta.Config;

import com.backend.AltaPinta.controller.CarritoController;
import com.backend.AltaPinta.controller.DireccionController;
import com.backend.AltaPinta.controller.FavoritoController;
import com.backend.AltaPinta.controller.TarjetaController;
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

import static com.backend.AltaPinta.Config.PermisosAssert.denegado;
import static com.backend.AltaPinta.Config.PermisosAssert.permitido;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Autorizacion de la zona privada del cliente: carrito, favoritos,
 * tarjetas y direcciones.
 *
 * Ninguna de estas rutas tiene regla propia en SecurityConfig salvo
 * /favoritos/**; las demas caen en anyRequest().authenticated(). Estas
 * pruebas fijan ese comportamiento para que un cambio futuro en el orden
 * de las reglas no deje datos personales al descubierto sin avisar.
 */
@WebMvcTest(controllers = {
        CarritoController.class,
        FavoritoController.class,
        TarjetaController.class,
        DireccionController.class
})
@Import({SecurityConfig.class, JwtAuthFilter.class})
class SeguridadWebCuentaTest {

    @Autowired private MockMvc mockMvc;

    // Cadena de seguridad
    @MockBean private JwtUtil jwtUtil;
    @MockBean private UserDetailsService userDetailsService;

    // Repositorios que usan los controladores bajo prueba
    @MockBean private CarritoRepository carritoRepo;
    @MockBean private CarritoItemRepository carritoItemRepo;
    @MockBean private ProductoRepository productoRepo;
    @MockBean private ProductoTallaRepository productoTallaRepo;
    @MockBean private TallaRepository tallaRepo;
    @MockBean private ClienteRepository clienteRepo;
    @MockBean private FavoritoRepository favoritoRepo;
    @MockBean private TarjetaRepository tarjetaRepo;
    @MockBean private DireccionRepository direccionRepo;

    private static final String CLIENTE = "cliente@unamba.edu.pe";

    // ============================================================
    @Nested
    @DisplayName("Carrito")
    class Carrito {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede ver el carrito")
        void anonimoNoVeCarrito() throws Exception {
            denegado(mockMvc.perform(get("/carrito")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede agregar al carrito")
        void anonimoNoAgrega() throws Exception {
            denegado(mockMvc.perform(post("/carrito/agregar/1")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede vaciar el carrito de otro")
        void anonimoNoElimina() throws Exception {
            denegado(mockMvc.perform(delete("/carrito/eliminar/1")));
        }

        @Test
        @WithMockUser(username = CLIENTE, authorities = "ROLE_USER")
        @DisplayName("Un cliente autenticado si puede ver su carrito")
        void clienteVeSuCarrito() throws Exception {
            permitido(mockMvc.perform(get("/carrito")));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Favoritos")
    class Favoritos {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede ver favoritos")
        void anonimoNoVeFavoritos() throws Exception {
            denegado(mockMvc.perform(get("/favoritos")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede marcar un favorito")
        void anonimoNoMarca() throws Exception {
            denegado(mockMvc.perform(post("/favoritos/1")));
        }

        @Test
        @WithMockUser(username = CLIENTE, authorities = "ROLE_USER")
        @DisplayName("Un cliente si puede ver sus favoritos")
        void clienteVeSusFavoritos() throws Exception {
            permitido(mockMvc.perform(get("/favoritos")));
        }

        @Test
        @WithMockUser(username = "admin@altapinta.com", authorities = "ROLE_ADMIN")
        @DisplayName("El admin tambien tiene favoritos (regla USER o ADMIN)")
        void adminTambienAccede() throws Exception {
            permitido(mockMvc.perform(get("/favoritos")));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Tarjetas: datos financieros")
    class Tarjetas {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede listar tarjetas")
        void anonimoNoListaTarjetas() throws Exception {
            denegado(mockMvc.perform(get("/tarjetas")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede registrar una tarjeta")
        void anonimoNoRegistraTarjeta() throws Exception {
            denegado(mockMvc.perform(post("/tarjetas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"numero\":\"4111111111111111\",\"titular\":\"X\"}")));
        }

        @Test
        @WithMockUser(username = CLIENTE, authorities = "ROLE_USER")
        @DisplayName("Un cliente si puede listar SUS tarjetas")
        void clienteListaSusTarjetas() throws Exception {
            permitido(mockMvc.perform(get("/tarjetas")));
        }

        @Test
        @WithMockUser(username = CLIENTE, authorities = "ROLE_USER")
        @DisplayName("Un cliente NO puede recargar saldo: eso es de admin")
        void clienteNoRecargaSaldo() throws Exception {
            // Si esto dejara de fallar, cualquier cliente podria regalarse dinero.
            denegado(mockMvc.perform(put("/tarjetas/admin/recargar")
                    .param("numero", "4111111111111111")
                    .param("monto", "9999")));
        }

        @Test
        @WithMockUser(username = "admin@altapinta.com", authorities = "ROLE_ADMIN")
        @DisplayName("Un admin si puede recargar saldo")
        void adminRecargaSaldo() throws Exception {
            permitido(mockMvc.perform(put("/tarjetas/admin/recargar")
                    .param("numero", "4111111111111111")
                    .param("monto", "100")));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Direcciones")
    class Direcciones {

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede ver direcciones")
        void anonimoNoVeDirecciones() throws Exception {
            denegado(mockMvc.perform(get("/direcciones")));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Un anonimo no puede borrar una direccion")
        void anonimoNoBorra() throws Exception {
            denegado(mockMvc.perform(delete("/direcciones/1")));
        }

        @Test
        @WithMockUser(username = CLIENTE, authorities = "ROLE_USER")
        @DisplayName("Un cliente si puede ver sus direcciones")
        void clienteVeSusDirecciones() throws Exception {
            permitido(mockMvc.perform(get("/direcciones")));
        }
    }
}
