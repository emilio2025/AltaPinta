package com.backend.AltaPinta;

import com.backend.AltaPinta.model.*;
import com.backend.AltaPinta.repository.*;
import com.backend.AltaPinta.service.EmailService;
import com.backend.AltaPinta.service.FacturaPdfService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.backend.AltaPinta.Importes.assertImporte;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba de integracion: la compra de principio a fin.
 *
 * Las otras 154 pruebas miran una capa cada una —el servicio con dobles, la
 * web con MockMvc, el repositorio con H2— y todas pueden pasar mientras las
 * piezas no encajan entre si. Esta recorre la pila entera con la aplicacion
 * de verdad levantada: registro, verificacion, inicio de sesion, catalogo,
 * carrito y pedido, arrastrando el token JWT de un paso al siguiente.
 *
 * Se llama ...Test y no ...IT a proposito: Surefire solo recoge *Test, y una
 * prueba de integracion que no se ejecuta no sirve de nada.
 *
 * Corre sobre H2 en memoria, NO sobre la base de datos de desarrollo, y con
 * el correo y la generacion de PDF sustituidos por dobles: una prueba no
 * puede mandar correos de verdad ni dejar archivos por el disco.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:compra;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        // Las migraciones estan en SQL de MySQL: aqui el esquema lo crea
        // Hibernate a partir de las entidades.
        "spring.flyway.enabled=false",
        // application.properties exige estas sin valor por defecto.
        "JWT_SECRET=clave-solo-para-pruebas-de-64-bytes-o-mas-porque-la-aplicacion-firma-con-hs512-xx",
        "ADMIN_EMAIL=admin@altapinta.test",
        "MAIL_USERNAME=pruebas@altapinta.test",
        "MAIL_PASSWORD=irrelevante"
})
class CompraCompletaTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper json;

    @Autowired private ClienteRepository clienteRepo;
    @Autowired private ProductoRepository productoRepo;
    @Autowired private CategoriaRepository categoriaRepo;
    @Autowired private TipoPrendaRepository tipoRepo;
    @Autowired private DeporteRepository deporteRepo;
    @Autowired private TallaRepository tallaRepo;
    @Autowired private ProductoTallaRepository productoTallaRepo;
    @Autowired private TarjetaRepository tarjetaRepo;
    @Autowired private EnvioRepository envioRepo;
    @Autowired private CuentaTiendaRepository cuentaRepo;
    @Autowired private PedidoRepository pedidoRepo;
    @Autowired private CarritoItemRepository carritoItemRepo;
    @Autowired private CarritoRepository carritoRepo;
    @Autowired private PedidoDetalleRepository detalleRepo;
    @Autowired private FacturaRepository facturaRepo;
    @Autowired private PagoRepository pagoRepo;

    // Sin estos dobles la prueba mandaria correo de verdad y escribiria PDFs.
    @MockBean private EmailService emailService;
    @MockBean private FacturaPdfService facturaPdfService;

    private static final String CORREO = "cliente@altapinta.test";
    private static final String PASSWORD = "Segura123";
    private static final BigDecimal PRECIO = new BigDecimal("89.90");
    private static final BigDecimal SALDO_INICIAL = new BigDecimal("500.00");
    private static final BigDecimal COSTO_ENVIO = new BigDecimal("15.00");
    private static final int STOCK_INICIAL = 10;

    private Producto producto;
    private Talla tallaM;
    private Envio envioLima;

    @BeforeEach
    void prepararCatalogo() {
        // Sin @Transactional en la clase: la prueba del cobro fallido
        // necesita que la transaccion del servicio se revierta sola y luego
        // poder leer como quedo la base. Con una transaccion de prueba
        // envolviendolo todo, el fallo la marcaria entera para revertir y no
        // se podria comprobar nada despues. A cambio, hay que limpiar a mano
        // y en el orden que permiten las claves ajenas.
        pagoRepo.deleteAll();
        carritoItemRepo.deleteAll();
        carritoRepo.deleteAll();
        facturaRepo.deleteAll();
        detalleRepo.deleteAll();
        pedidoRepo.deleteAll();
        tarjetaRepo.deleteAll();
        productoTallaRepo.deleteAll();
        productoRepo.deleteAll();
        clienteRepo.deleteAll();

        // Los datos de referencia se reutilizan en lugar de recrearse:
        // Deporte.nombre es unico y volver a insertarlo reventaba a partir
        // de la segunda prueba.
        Categoria mujer = buscarOCrearCategoria("Mujer");
        TipoPrenda legging = buscarOCrearTipo("Legging");
        Deporte gym = buscarOCrearDeporte("Gym");
        tallaM = buscarOCrearTalla("M");

        producto = new Producto();
        producto.setNombre("Legging de tiro alto");
        producto.setDescripcion("Prueba");
        producto.setPrecio(PRECIO);
        producto.setCategoria(mujer);
        producto.setTipoPrenda(legging);
        producto.setDeporte(gym);
        productoRepo.save(producto);

        ProductoTalla stock = new ProductoTalla();
        stock.setProducto(producto);
        stock.setTalla(tallaM);
        stock.setStock(STOCK_INICIAL);
        productoTallaRepo.save(stock);

        envioLima = envioRepo.findAll().stream().findFirst().orElseGet(() -> {
            Envio e = new Envio();
            e.setLugar("Lima");
            e.setCosto(COSTO_ENVIO);
            return envioRepo.save(e);
        });

        // PedidoService ingresa el cobro en la cuenta con id 1.
        //
        // Hay que ponerla a cero en cada prueba, no solo crearla: sin esto,
        // el ingreso de una prueba se sumaba al de la siguiente y la
        // comprobacion del total fallaba solo al ejecutar la suite entera,
        // no al ejecutar esta clase sola. Los fallos que dependen del orden
        // son los peores de diagnosticar.
        CuentaTienda cuenta = cuentaRepo.findById(1L).orElseGet(CuentaTienda::new);
        cuenta.setSaldo(BigDecimal.ZERO);
        cuentaRepo.save(cuenta);
    }


    private Categoria buscarOCrearCategoria(String nombre) {
        return categoriaRepo.findAll().stream()
                .filter(c -> nombre.equals(c.getNombre())).findFirst()
                .orElseGet(() -> { Categoria c = new Categoria(); c.setNombre(nombre); return categoriaRepo.save(c); });
    }

    private TipoPrenda buscarOCrearTipo(String nombre) {
        return tipoRepo.findAll().stream()
                .filter(t -> nombre.equals(t.getNombre())).findFirst()
                .orElseGet(() -> { TipoPrenda t = new TipoPrenda(); t.setNombre(nombre); return tipoRepo.save(t); });
    }

    private Deporte buscarOCrearDeporte(String nombre) {
        return deporteRepo.findAll().stream()
                .filter(d -> nombre.equals(d.getNombre())).findFirst()
                .orElseGet(() -> { Deporte d = new Deporte(); d.setNombre(nombre); return deporteRepo.save(d); });
    }

    private Talla buscarOCrearTalla(String nombre) {
        return tallaRepo.findAll().stream()
                .filter(t -> nombre.equals(t.getNombre())).findFirst()
                .orElseGet(() -> { Talla t = new Talla(); t.setNombre(nombre); return tallaRepo.save(t); });
    }

    // ------------------------------------------------------------
    //  Pasos reutilizables
    // ------------------------------------------------------------

    /** Registro + verificacion del codigo que quedo en la base. */
    private void registrarYVerificar() throws Exception {
        var registro = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nombre":"Ana","correo":"%s","password":"%s",
                     "direccion":"Av. Siempre Viva 742","dni":"12345678"}
                    """.formatted(CORREO, PASSWORD)))
               .andReturn().getResponse();

        // Se comprueba con el cuerpo delante: un 400 a secas no dice si fallo
        // la contrasena, el DNI o el correo, y aqui se encadenan varios pasos.
        assertThat(registro.getStatus())
                .as("registro rechazado: %s", registro.getContentAsString())
                .isEqualTo(200);

        // El codigo no viaja en la respuesta a proposito: solo llega por
        // correo. Aqui se lee de la base, que es lo unico que puede hacer
        // una prueba sin abrir un buzon.
        String codigo = clienteRepo.findByCorreo(CORREO).orElseThrow().getTokenVerificacion();
        assertThat(codigo).as("el registro debe dejar un codigo de verificacion").isNotBlank();

        mockMvc.perform(post("/api/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\":\"%s\",\"codigo\":\"%s\"}".formatted(CORREO, codigo)))
               .andExpect(status().isOk());
    }

    /** Inicia sesion y devuelve el token JWT. */
    private String iniciarSesion() throws Exception {
        var respuesta = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\":\"%s\",\"password\":\"%s\"}".formatted(CORREO, PASSWORD)))
               .andReturn().getResponse();

        assertThat(respuesta.getStatus())
                .as("login rechazado: %s", respuesta.getContentAsString())
                .isEqualTo(200);

        String cuerpo = respuesta.getContentAsString();

        JsonNode nodo = json.readTree(cuerpo);
        String token = nodo.get("token").asText();
        assertThat(token).isNotBlank();

        // El rol tiene que venir en la respuesta: de el depende que la
        // interfaz muestre o no el panel de administracion. Cuando no se
        // enviaba, el frontend guardaba undefined y decidia quien era
        // administrador comparando el correo con una constante suya.
        assertThat(nodo.hasNonNull("rol"))
                .as("el login debe devolver el rol; si no, la interfaz no sabe que mostrar")
                .isTrue();
        assertThat(nodo.get("rol").asText()).isIn("USER", "ADMIN");

        return token;
    }

    /** Tarjeta del cliente ya registrado, con saldo suficiente. */
    private Tarjeta darTarjetaAlCliente() {
        Cliente cliente = clienteRepo.findByCorreo(CORREO).orElseThrow();
        Tarjeta t = new Tarjeta();
        t.setCliente(cliente);
        t.setNumero("4111111111111111");
        t.setTitular("Ana");
        t.setFechaVencimiento("12/2030");
        t.setSaldo(SALDO_INICIAL);
        t.setActiva(true);
        return tarjetaRepo.save(t);
    }

    private int stockActual() {
        return productoTallaRepo
                .findByProductoIdAndTallaId(producto.getId(), tallaM.getId())
                .orElseThrow().getStock();
    }

    // ============================================================
    @Nested
    @DisplayName("El recorrido completo")
    class Recorrido {

        @Test
        @DisplayName("Registro, sesion, carrito y pedido dejan la base coherente")
        void compraDeExtremoAExtremo() throws Exception {
            registrarYVerificar();
            String token = iniciarSesion();
            Tarjeta tarjeta = darTarjetaAlCliente();

            // El catalogo es publico: se consulta sin token
            mockMvc.perform(get("/productos").param("size", "10"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.totalElements").value(1));

            // Dos unidades de la talla M
            mockMvc.perform(post("/carrito/agregar/" + producto.getId())
                    .header("Authorization", "Bearer " + token)
                    .param("cantidad", "2")
                    .param("tallaId", tallaM.getId().toString()))
                   .andExpect(status().isOk());

            mockMvc.perform(get("/carrito").header("Authorization", "Bearer " + token))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.items.length()").value(1))
                   .andExpect(jsonPath("$.items[0].cantidad").value(2));

            // 89.90 x 2 + 15.00 de envio = 194.80
            mockMvc.perform(post("/pedido/confirmar")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"envioId\":%d,\"tarjetaId\":%d}"
                            .formatted(envioLima.getId(), tarjeta.getId())))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.estado").value("PAGADO"))
                   .andExpect(jsonPath("$.total").value(194.80));

            // --- lo que tiene que haber cambiado en la base ---

            assertThat(stockActual())
                    .as("el stock de la talla comprada baja en 2")
                    .isEqualTo(STOCK_INICIAL - 2);

            // 500.00 - 194.80
            assertImporte("305.20", tarjetaRepo.findById(tarjeta.getId()).orElseThrow().getSaldo(),
                    "el saldo de la tarjeta baja el total del pedido");

            assertImporte("194.80", cuentaRepo.findById(1L).orElseThrow().getSaldo(),
                    "la tienda ingresa el total del pedido");

            Cliente cliente = clienteRepo.findByCorreo(CORREO).orElseThrow();
            assertThat(carritoItemRepo.findByCarritoClienteId(cliente.getId()))
                    .as("el carrito queda vacio tras comprar")
                    .isEmpty();

            assertThat(pedidoRepo.findAll())
                    .as("queda un pedido registrado")
                    .hasSize(1);

            // RF020: el cobro que prospera deja constancia, ligado a su pedido
            var pagos = pagoRepo.findAll();
            assertThat(pagos).as("se registra el intento de cobro").hasSize(1);
            assertThat(pagos.get(0).getEstado()).isEqualTo("APROBADO");
            assertThat(pagos.get(0).getPedido()).as("el pago aprobado apunta a su pedido").isNotNull();
            assertImporte("194.80", pagos.get(0).getMonto(),
                    "el importe registrado es el cobrado");
        }
    }

    // ============================================================
    @Nested
    @DisplayName("Las piezas que solo se ven al juntarlas")
    class Uniones {

        @Test
        @DisplayName("Sin token no se llega al carrito")
        void carritoExigeSesion() throws Exception {
            mockMvc.perform(get("/carrito"))
                   .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Un token inventado no sirve")
        void tokenFalso() throws Exception {
            mockMvc.perform(get("/carrito")
                    .header("Authorization", "Bearer esto.no.es-un-token"))
                   .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("La contraseña debe cumplir las reglas para registrarse")
        void passwordDebil() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"nombre":"Ana","correo":"otra@altapinta.test","password":"1234",
                         "direccion":"x","dni":"87654321"}
                        """))
                   .andExpect(status().isBadRequest());

            assertThat(clienteRepo.findByCorreo("otra@altapinta.test"))
                    .as("no debe quedar el cliente a medio crear")
                    .isEmpty();
        }

        @Test
        @DisplayName("Confirmar dos veces no duplica la compra")
        void segundaConfirmacionFalla() throws Exception {
            registrarYVerificar();
            String token = iniciarSesion();
            Tarjeta tarjeta = darTarjetaAlCliente();

            mockMvc.perform(post("/carrito/agregar/" + producto.getId())
                    .header("Authorization", "Bearer " + token)
                    .param("cantidad", "1")
                    .param("tallaId", tallaM.getId().toString()))
                   .andExpect(status().isOk());

            String cuerpo = "{\"tarjetaId\":%d}".formatted(tarjeta.getId());

            mockMvc.perform(post("/pedido/confirmar")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                   .andExpect(status().isOk());

            // El carrito ya se vacio: el segundo intento no tiene que cobrar
            // otra vez. Esto es lo que protege del doble clic en "pagar".
            mockMvc.perform(post("/pedido/confirmar")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                   .andExpect(status().isBadRequest());

            assertThat(pedidoRepo.findAll()).hasSize(1);
            assertThat(stockActual()).isEqualTo(STOCK_INICIAL - 1);
        }

        @Test
        @DisplayName("Un fallo al cobrar no deja el pedido a medias")
        void saldoInsuficienteNoTocaNada() throws Exception {
            registrarYVerificar();
            String token = iniciarSesion();

            Cliente cliente = clienteRepo.findByCorreo(CORREO).orElseThrow();
            Tarjeta pobre = new Tarjeta();
            pobre.setCliente(cliente);
            pobre.setNumero("4222222222222222");
            pobre.setTitular("Ana");
            pobre.setFechaVencimiento("12/2030");
            pobre.setSaldo(new BigDecimal("1.00"));
            pobre.setActiva(true);
            tarjetaRepo.save(pobre);

            mockMvc.perform(post("/carrito/agregar/" + producto.getId())
                    .header("Authorization", "Bearer " + token)
                    .param("cantidad", "2")
                    .param("tallaId", tallaM.getId().toString()))
                   .andExpect(status().isOk());

            mockMvc.perform(post("/pedido/confirmar")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"tarjetaId\":%d}".formatted(pobre.getId())))
                   .andExpect(status().isBadRequest());

            // La transaccion tiene que revertirse entera: ni stock, ni saldo,
            // ni carrito vaciado. Es lo que ninguna prueba de una sola capa
            // puede comprobar.
            assertThat(stockActual())
                    .as("el stock no se toca si el cobro falla")
                    .isEqualTo(STOCK_INICIAL);

            assertImporte("1.00", tarjetaRepo.findById(pobre.getId()).orElseThrow().getSaldo(),
                    "el saldo se queda como estaba");

            assertThat(pedidoRepo.findAll())
                    .as("no se registra ningun pedido")
                    .isEmpty();

            assertThat(carritoItemRepo.findByCarritoClienteId(cliente.getId()))
                    .as("el carrito sigue con lo que habia")
                    .isNotEmpty();

            // RNF018: el intento fallido SI queda registrado.
            //
            // Es la comprobacion que da sentido a toda la transaccionalidad:
            // el pedido se revirtio entero, pero este registro se escribio en
            // una transaccion propia y sobrevivio al rollback. Si el registro
            // se hiciera dentro de la transaccion del pedido, aqui no habria
            // ninguna fila.
            var pagos = pagoRepo.findAll();
            assertThat(pagos).as("el cobro rechazado deja constancia").hasSize(1);
            assertThat(pagos.get(0).getEstado()).isEqualTo("RECHAZADO");
            assertThat(pagos.get(0).getPedido())
                    .as("un cobro rechazado no tiene pedido: nunca llego a crearse")
                    .isNull();
            assertThat(pagos.get(0).getMotivo())
                    .as("queda el motivo real del rechazo")
                    .contains("Saldo insuficiente");
        }
    }
}
