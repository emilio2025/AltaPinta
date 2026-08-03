package com.backend.AltaPinta.Config;

import com.backend.AltaPinta.controller.CarritoController;
import com.backend.AltaPinta.controller.CategoriaController;
import com.backend.AltaPinta.controller.ClienteController;
import com.backend.AltaPinta.controller.PedidoController;
import com.backend.AltaPinta.controller.TarjetaController;
import com.backend.AltaPinta.model.Categoria;
import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.model.Tarjeta;
import com.backend.AltaPinta.repository.*;
import com.backend.AltaPinta.service.AuditoriaService;
import com.backend.AltaPinta.service.EmailService;
import com.backend.AltaPinta.service.FacturaPdfService;
import com.backend.AltaPinta.service.PedidoService;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Validacion de la entrada.
 *
 * Antes, la mayoria de endpoints aceptaba el JSON que le llegara: solo 2 de
 * los 8 DTO tenian restricciones y solo 3 de 16 controladores usaban @Valid.
 * Nada impedia crear una categoria sin nombre, guardar una tarjeta con saldo
 * negativo o meter en el carrito una cantidad de cero.
 *
 * Lo que se comprueba aqui no es que la anotacion este escrita —eso lo diria
 * un grep— sino que la peticion se RECHAZA con 400 y con un mensaje que
 * explica cual es el campo malo. Un 400 sin mensaje util no sirve de nada al
 * que consume la API.
 */
@WebMvcTest(controllers = {
        CategoriaController.class,
        TarjetaController.class,
        ClienteController.class,
        PedidoController.class,
        CarritoController.class
})
@Import({SecurityConfig.class, JwtAuthFilter.class, GlobalExceptionHandler.class})
class ValidacionEntradaTest {

    @Autowired private MockMvc mockMvc;

    // Cadena de seguridad
    @MockBean private JwtUtil jwtUtil;
    @MockBean private UserDetailsService userDetailsService;

    // Dependencias de los controladores bajo prueba
    @MockBean private CategoriaRepository categoriaRepo;
    @MockBean private TarjetaRepository tarjetaRepo;
    @MockBean private ClienteRepository clienteRepo;
    @MockBean private PedidoRepository pedidoRepo;
    @MockBean private PedidoDetalleRepository detalleRepo;
    @MockBean private CarritoRepository carritoRepo;
    @MockBean private CarritoItemRepository carritoItemRepo;
    @MockBean private ProductoRepository productoRepo;
    @MockBean private ProductoTallaRepository productoTallaRepo;
    @MockBean private TallaRepository tallaRepo;
    @MockBean private PedidoService pedidoService;
    @MockBean private FacturaPdfService facturaPdfService;
    @MockBean private EmailService emailService;
    @MockBean private AuditoriaService auditoriaService;

    /**
     * Los controladores buscan primero al cliente autenticado y lanzan
     * "Cliente no encontrado" si no esta. Como el manejador global convierte
     * cualquier RuntimeException en 400, sin este doble un caso valido
     * tambien saldria 400 y la prueba no distinguiria un dato malo de un
     * cliente ausente.
     */
    @BeforeEach
    void clienteAutenticadoExiste() {
        Cliente ana = new Cliente();
        ana.setId(1L);
        ana.setCorreo("ana@ejemplo.com");
        ana.setNombre("Ana");

        when(clienteRepo.findByCorreo(anyString())).thenReturn(Optional.of(ana));
        when(clienteRepo.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tarjetaRepo.existsByNumero(anyString())).thenReturn(false);
        when(tarjetaRepo.save(any(Tarjeta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(categoriaRepo.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    /** Rechazada con 400 y con un mensaje que menciona el campo. */
    private void rechazada(ResultActions res, String textoEsperado) throws Exception {
        res.andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString(textoEsperado)));
    }

    /**
     * La validacion la dejo pasar.
     *
     * No se exige 200: con los repositorios simulados el controlador puede
     * devolver otra cosa. Lo unico que se comprueba es que NO sea un 400,
     * que es lo que significaria "este dato no vale".
     */
    private void aceptada(ResultActions res) throws Exception {
        res.andExpect(status().is(not(400)));
    }

    // ============================================================
    @Nested
    @DisplayName("Catalogo")
    class Catalogo {

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("Una categoria sin nombre no se guarda")
        void categoriaSinNombre() throws Exception {
            rechazada(mockMvc.perform(post("/categorias")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}")), "nombre");
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("Una categoria con el nombre en blanco tampoco")
        void categoriaNombreEnBlanco() throws Exception {
            // La cadena vacia pasaba cualquier comprobacion de null
            rechazada(mockMvc.perform(post("/categorias")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"   \"}")), "nombre");
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("Un nombre desmesurado se corta antes de llegar a la base")
        void categoriaNombreLarguisimo() throws Exception {
            String largo = "x".repeat(300);
            rechazada(mockMvc.perform(post("/categorias")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"" + largo + "\"}")), "50");
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("Una categoria correcta sigue guardandose")
        void categoriaValida() throws Exception {
            aceptada(mockMvc.perform(post("/categorias")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"Mujer\"}")));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Tarjetas")
    class Tarjetas {

        private static final String NUMERO = "4111111111111111";

        private String json(String numero, String vence, String saldo) {
            return "{\"numero\":\"" + numero + "\",\"titular\":\"J Paniagua\","
                 + "\"fechaVencimiento\":\"" + vence + "\",\"saldo\":" + saldo + "}";
        }

        @Test
        @WithMockUser
        @DisplayName("Un numero con letras no se acepta")
        void numeroConLetras() throws Exception {
            rechazada(mockMvc.perform(post("/tarjetas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json("4111-ABCD-1111", "12/2030", "500"))), "dígitos");
        }

        @Test
        @WithMockUser
        @DisplayName("Un saldo negativo no se acepta")
        void saldoNegativo() throws Exception {
            // Sin esto se podia registrar una tarjeta en numeros rojos
            rechazada(mockMvc.perform(post("/tarjetas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(NUMERO, "12/2030", "-100"))), "negativo");
        }

        @Test
        @WithMockUser
        @DisplayName("Un vencimiento con mes 13 no se acepta")
        void mesInexistente() throws Exception {
            rechazada(mockMvc.perform(post("/tarjetas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(NUMERO, "13/2030", "500"))), "MM/AA");
        }

        @Test
        @WithMockUser
        @DisplayName("Se admite MM/AAAA, que es lo que pide el formulario")
        void formatoLargo() throws Exception {
            aceptada(mockMvc.perform(post("/tarjetas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(NUMERO, "12/2030", "500"))));
        }

        @Test
        @WithMockUser
        @DisplayName("Se admite tambien MM/AA, que es como estan las tarjetas antiguas")
        void formatoCorto() throws Exception {
            // En la base hay tarjetas guardadas como "12/25". Un patron que
            // solo admitiera cuatro digitos las romperia al confirmar un
            // pedido, porque ahi se descuenta el saldo y se vuelve a guardar
            // la tarjeta: Hibernate valida la entidad antes de escribirla.
            aceptada(mockMvc.perform(post("/tarjetas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(NUMERO, "12/25", "500"))));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Perfil")
    class Perfil {

        @Test
        @WithMockUser(username = "ana@ejemplo.com")
        @DisplayName("Un DNI que no tiene 8 digitos no se acepta")
        void dniMalFormado() throws Exception {
            rechazada(mockMvc.perform(put("/cliente/actualizar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"Ana\",\"dni\":\"123\"}")), "DNI");
        }

        @Test
        @WithMockUser(username = "ana@ejemplo.com")
        @DisplayName("Un perfil sin nombre no se guarda")
        void sinNombre() throws Exception {
            rechazada(mockMvc.perform(put("/cliente/actualizar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"\",\"dni\":\"12345678\"}")), "nombre");
        }

        @Test
        @WithMockUser(username = "ana@ejemplo.com")
        @DisplayName("El RUC y la razon social pueden ir vacios")
        void rucOpcional() throws Exception {
            // Quien no tiene RUC los manda en blanco; exigirlos impediria
            // guardar cualquier otro cambio del perfil.
            aceptada(mockMvc.perform(put("/cliente/actualizar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"Ana\",\"dni\":\"12345678\",\"ruc\":\"\",\"razonSocial\":\"\"}")));
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Pedido")
    class Pedido {

        @Test
        @WithMockUser(username = "ana@ejemplo.com")
        @DisplayName("Confirmar sin tarjeta se rechaza señalando la tarjeta")
        void sinTarjeta() throws Exception {
            // Antes esto llegaba a PedidoService y salia como "La tarjeta no
            // pertenece al cliente", que apunta a un problema de permisos y
            // no a un campo que falta.
            rechazada(mockMvc.perform(post("/pedido/confirmar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}")), "tarjeta");
        }

        @Test
        @WithMockUser(username = "ana@ejemplo.com")
        @DisplayName("Un cuerpo con el JSON roto no revienta el servidor")
        void jsonRoto() throws Exception {
            mockMvc.perform(post("/pedido/confirmar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"tarjetaId\": }"))
                   .andExpect(status().isBadRequest());
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Carrito")
    class Carrito {

        @Test
        @WithMockUser(username = "ana@ejemplo.com")
        @DisplayName("Cantidad cero se rechaza")
        void cantidadCero() throws Exception {
            rechazada(mockMvc.perform(post("/carrito/agregar/1")
                    .param("cantidad", "0")
                    .param("tallaId", "2")), "cantidad");
        }

        @Test
        @WithMockUser(username = "ana@ejemplo.com")
        @DisplayName("Cantidad negativa se rechaza")
        void cantidadNegativa() throws Exception {
            // Una cantidad negativa habria DEVUELTO stock al confirmar el
            // pedido en lugar de descontarlo.
            rechazada(mockMvc.perform(post("/carrito/agregar/1")
                    .param("cantidad", "-3")
                    .param("tallaId", "2")), "cantidad");
        }

        @Test
        @WithMockUser(username = "ana@ejemplo.com")
        @DisplayName("Al actualizar tampoco se admite cero")
        void actualizarACero() throws Exception {
            rechazada(mockMvc.perform(put("/carrito/actualizar/1")
                    .param("cantidad", "0")
                    .param("tallaId", "2")), "cantidad");
        }
    }
}
