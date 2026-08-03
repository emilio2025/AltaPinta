# Historial de commits — AltaPinta

Sistema de comercio electrónico para tienda deportiva.  
Curso de **Ingeniería de Software II** — UNAMBA.

- **Repositorio:** https://github.com/emilio2025/AltaPinta
- **Commits:** 39
- **Periodo:** 2026-07-31 — 2026-08-03
- **Rama:** `main`

Los commits están en orden cronológico, del más antiguo al más reciente.

> **Es una foto, no un documento vivo.** Refleja el historial hasta `42af608`;
> los commits posteriores —incluido el que añade este mismo archivo— no
> aparecen. Para regenerarlo:
>
> ```bash
> git log --reverse --format="%h %ad %s%n%b" --date=short > historial.txt
> ```

---

## Actividad por día

| Fecha | Commits |
|---|---|
| 2026-07-31 | 3 |
| 2026-08-01 | 17 |
| 2026-08-02 | 10 |
| 2026-08-03 | 9 |

---

## Índice

| # | Fecha | Commit | Asunto |
|---|---|---|---|
| 1 | 2026-07-31 | `c2c3368` | Commit inicial: AltaPinta - tienda deportiva |
| 2 | 2026-07-31 | `2eae03a` | Externalizar credenciales a variables de entorno |
| 3 | 2026-07-31 | `f80674d` | Fijar la compilacion y la ejecucion en Java 17 |
| 4 | 2026-08-01 | `80d6685` | Anadir pruebas unitarias del backend (45 casos) |
| 5 | 2026-08-01 | `432ca41` | Anadir pruebas de autorizacion y corregir el 403 de @PreAuthorize |
| 6 | 2026-08-01 | `e3b2131` | Cerrar la escritura publica del catalogo y ampliar las pruebas |
| 7 | 2026-08-01 | `9fd0735` | Impedir que un cliente opere sobre el pedido de otro en /pago/procesar |
| 8 | 2026-08-01 | `187e0df` | Eliminar /pago/procesar y su cadena de codigo muerto |
| 9 | 2026-08-01 | `bd086da` | Probar las consultas de reportes y hacer portable ventasPorDia |
| 10 | 2026-08-01 | `e00c87b` | Anadir pruebas al frontend y eliminar los scaffolds rotos |
| 11 | 2026-08-01 | `65a8964` | Paginar el catalogo y mover busqueda y filtros al backend |
| 12 | 2026-08-01 | `3f90652` | Adaptar la tienda a ropa deportiva: deportes y rediseño de la portada |
| 13 | 2026-08-01 | `211de4f` | Rediseñar el catalogo /menu con la identidad deportiva |
| 14 | 2026-08-01 | `8664d7c` | Rediseñar detalle, carrito y checkout con la identidad deportiva |
| 15 | 2026-08-01 | `2ed6577` | Gestionar deportes desde el panel de administracion |
| 16 | 2026-08-01 | `a29b4f8` | Panel de administracion: deportes, y migrar las pantallas de cuenta |
| 17 | 2026-08-01 | `506b004` | Usar BigDecimal para todos los importes en lugar de double |
| 18 | 2026-08-01 | `4171484` | Dejar de almacenar el CVV de las tarjetas |
| 19 | 2026-08-01 | `2b24f69` | Aplicar las migraciones y no versionar los respaldos |
| 20 | 2026-08-01 | `fc2427e` | Migrar pedidos, alta de tarjeta y las pantallas de acceso |
| 21 | 2026-08-02 | `aba098c` | Unificar las paginas de categoria con el catalogo |
| 22 | 2026-08-02 | `aff5947` | Migrar el panel de administracion al diseño de la tienda |
| 23 | 2026-08-02 | `0a19d9f` | Convertir el catalogo a ropa deportiva y dar surtido de tallas |
| 24 | 2026-08-02 | `3e39b2f` | Ilustrar los productos y escalar los precios por prenda |
| 25 | 2026-08-02 | `a6de19c` | Versionar las ilustraciones de prenda |
| 26 | 2026-08-02 | `285fbd4` | Versionar las ilustraciones de prenda |
| 27 | 2026-08-02 | `3af7060` | Nombres de producto descriptivos, para poder buscar sus fotos |
| 28 | 2026-08-02 | `5ec3937` | Sacar del codigo la direccion del backend y las URLs de imagen |
| 29 | 2026-08-02 | `c6312d0` | Integracion continua, README al dia y arreglos que destapo la CI |
| 30 | 2026-08-02 | `b2a13a2` | Sustituir el bloqueo en memoria por uno de base de datos |
| 31 | 2026-08-03 | `d6b6998` | Pruebas de componente del frontend y aviso de JDK incompatible |
| 32 | 2026-08-03 | `b532725` | Flyway gobierna el esquema; ddl-auto pasa de update a validate |
| 33 | 2026-08-03 | `cd92d0e` | Insignia de CI en el README y nombres de trabajo sin numeros |
| 34 | 2026-08-03 | `dd2fe36` | README: aclarar los requisitos de la App Password de correo |
| 35 | 2026-08-03 | `93058a2` | Validar la entrada de la API en lugar de aceptar cualquier JSON |
| 36 | 2026-08-03 | `5632bf4` | Eliminar la tabla y la entidad Pago (migracion V2) |
| 37 | 2026-08-03 | `dbdd089` | Pruebas del checkout y arreglo del mensaje de error |
| 38 | 2026-08-03 | `80e5568` | Prueba de integracion: la compra de principio a fin |
| 39 | 2026-08-03 | `42af608` | La portada publica ya no pide favoritos sin sesion |

---

## Detalle

### 1. Commit inicial: AltaPinta - tienda deportiva

`c2c3368` · 2026-07-31

Sistema de e-commerce para tienda deportiva, proyecto del curso
Ingenieria de Software II.

- BackendTienda/: API REST con Spring Boot 3.3.5 y Java 17.
  Autenticacion JWT, catalogo de productos con tallas y stock,
  carrito, checkout con pago simulado, generacion de facturas PDF
  (iText), envio de correos, favoritos, direcciones y auditoria.
  Arquitectura en capas: Config / controller / dto / model /
  repository / service.

- Practica/: cliente web en Angular 20.3 con componentes standalone,
  guards de autenticacion y rol, e interceptor JWT.

La configuracion con credenciales (application.properties) queda
fuera del repositorio; se versiona la plantilla
application.properties.example.

---

### 2. Externalizar credenciales a variables de entorno

`2eae03a` · 2026-07-31

Ningun secreto queda en el codigo ni en archivos versionados.

- application.properties ahora usa referencias ${VARIABLE} y vuelve a
  versionarse: documenta que configuracion necesita el proyecto sin
  exponer valores. Los que no son sensibles llevan valor por defecto
  (${DB_HOST:localhost}); JWT_SECRET no lo lleva a proposito, para que
  la aplicacion falle al arrancar antes que firmar tokens con una
  clave conocida.

- Los valores reales se leen de BackendTienda/secrets.properties, que
  esta en .gitignore, mediante spring.config.import con prefijo
  "optional:". En un servidor se pueden sobrescribir con variables de
  entorno, que tienen mayor precedencia que ese archivo.

- Se versiona secrets.properties.example como plantilla y se elimina
  application.properties.example, que queda redundante.

- AuthController: el correo de administrador estaba fijo en el codigo;
  ahora se inyecta con @Value desde app.admin.correo (ADMIN_EMAIL).

Verificado: mvn compile correcto y el arranque resuelve todos los
placeholders (falla despues, al conectar a MySQL, por credenciales de
base de datos ajenas a este cambio).

---

### 3. Fijar la compilacion y la ejecucion en Java 17

`f80674d` · 2026-07-31

En esta maquina el java del PATH era un JDK 25, muy posterior a Spring
Boot 3.3.5. Ademas faltaba la carpeta .mvn, asi que mvnw no funcionaba y
habia que recurrir al Maven del sistema.

- Se regenera el wrapper de Maven (.mvn/wrapper) con la version 3.9.11,
  para que ./mvnw funcione en cualquier maquina sin instalar Maven.

- pom.xml: el plugin de compilacion pasa de <source>/<target> a
  <release>. Los primeros solo fijan la version del lenguaje; con un JDK
  mas nuevo se puede usar sin querer una API que no existe en Java 17,
  que compila bien y falla en tiempo de ejecucion. <release> tambien
  limita las APIs visibles, asi que el error salta al compilar.

- run.ps1: arranca el backend seleccionando el JDK 17 y avisa si falta
  secrets.properties. Fija JAVA_HOME solo en esa sesion de PowerShell,
  para no alterar la configuracion del sistema ni otros proyectos que
  usan un JDK distinto.

- README.md con requisitos, puesta en marcha, el modelo de configuracion
  por capas y los errores mas frecuentes.

Verificado: ./mvnw clean compile correcto sobre JDK 17.0.12, con
bytecode de version 61 (Java 17).

---

### 4. Anadir pruebas unitarias del backend (45 casos)

`80d6685` · 2026-08-01

El proyecto no tenia ninguna prueba. Se cubren primero las piezas con
mas riesgo: el flujo de compra y la autenticacion.

- PedidoServiceTest (21 casos): confirmacion y cancelacion de pedidos
  con dobles de Mockito, sin base de datos. Ademas del camino feliz,
  comprueba que un pedido invalido no deje efectos a medias: con stock
  o saldo insuficiente no se cobra la tarjeta, no se descuenta stock,
  no se abona a la tienda y no se vacia el carrito. Incluye el caso
  RF044 (si falla la factura o el correo, la compra sigue siendo
  valida) y RF043 (no se cancela dos veces ni un pedido ajeno).

- JwtUtilTest (8 casos): ida y vuelta del correo en el token, y
  rechazo de tokens caducados, firmados con otra clave, con la firma
  alterada o con el payload cambiado para suplantar a otro usuario.

- PasswordValidatorTest (16 casos): los cuatro criterios de RF035,
  incluido el limite exacto de 8 caracteres y la entrada nula.

Ejecutar con:  mvnw.cmd test   (requiere JDK 17; ver run.ps1)

---

### 5. Anadir pruebas de autorizacion y corregir el 403 de @PreAuthorize

`432ca41` · 2026-08-01

SeguridadWebTest (15 casos): levanta solo la capa web con la cadena de
filtros real y simula al usuario con @WithMockUser, para comprobar quien
puede llamar a que endpoint.

  - Catalogo publico: productos y categorias se ven sin iniciar sesion.
  - Gestion de productos: crear, modificar y eliminar quedan reservados
    al administrador; un cliente normal y un anonimo son rechazados.
  - Pedidos: /pedido/mis-pedidos exige sesion iniciada.
  - Seguridad de metodo: un cliente no puede listar los pedidos de todos
    ni cambiar el estado de un pedido.

Al escribir esas dos ultimas pruebas se detecto un fallo: los endpoints
protegidos con @PreAuthorize devolvian 400 en lugar de 403.
AccessDeniedException hereda de RuntimeException, asi que la capturaba
el manejador generico de GlobalExceptionHandler. El acceso quedaba
bloqueado igualmente (no era un agujero de seguridad), pero el cliente
recibia "datos invalidos" cuando el problema real era de permisos.

Los endpoints que restringe SecurityConfig por URL no estaban afectados:
se rechazan en la cadena de filtros, antes de llegar al
@RestControllerAdvice, y ya devolvian 403.

El frontend no se ve afectado: su interceptor no consulta el codigo de
estado y todas sus comprobaciones de 403 estan comentadas.

Suite completa: 60 pruebas, todas pasando.

---

### 6. Cerrar la escritura publica del catalogo y ampliar las pruebas

`e3b2131` · 2026-08-01

SecurityConfig declaraba /categorias/** y /tallas/** como permitAll sin
distinguir el metodo HTTP, asi que cualquier visitante sin cuenta podia
crear categorias y tallas con un POST. En /tipos la escritura caia en
anyRequest().authenticated(), de modo que bastaba con registrarse para
modificar el catalogo.

Ahora las tres rutas separan lectura de escritura: el GET sigue siendo
publico (la tienda se navega sin cuenta) y el resto de metodos exigen
ROLE_ADMIN. El frontend no se ve afectado porque solo lee esos
endpoints; ninguna de sus pantallas escribe en ellos.

Verificado revirtiendo el arreglo: sin el, 5 de las pruebas nuevas
fallan.

Pruebas anadidas (31 casos):

- SeguridadWebCuentaTest (16): carrito, favoritos, tarjetas y
  direcciones. Ninguna de esas rutas tiene regla propia salvo
  /favoritos/**; las demas dependen de anyRequest().authenticated(),
  asi que estas pruebas fijan el comportamiento para que un cambio en
  el orden de las reglas no deje datos personales al descubierto.
  Incluye que un cliente no pueda recargar el saldo de su tarjeta.

- SeguridadWebAdminTest (15): auditoria y reportes de ventas
  reservados al administrador, y la separacion lectura/escritura del
  catalogo descrita arriba.

- PermisosAssert: las comprobaciones compartidas por los tres ficheros
  de pruebas de autorizacion.

Suite completa: 91 pruebas, todas pasando.

---

### 7. Impedir que un cliente opere sobre el pedido de otro en /pago/procesar

`9fd0735` · 2026-08-01

PagoController buscaba el pedido solo por id:

    pedidoRepo.findById(pedidoId)

Cualquier usuario con sesion iniciada podia enviar el pedidoId de otra
persona. PagoService si comprobaba que la tarjeta fuera del cliente, pero
nadie comprobaba la propiedad del pedido, y el controlador escribe el
estado del pedido con el resultado del cobro:

    pedido.setEstado(aprobado ? "PAGADO" : "RECHAZADO");

Bastaba entonces con pagar el pedido ajeno usando una tarjeta propia sin
saldo para que el pedido de la victima quedara marcado como RECHAZADO.

La busqueda ahora filtra tambien por el correo de la sesion con
findByIdAndClienteCorreo, que ya existia en el repositorio y es el mismo
criterio que usa PedidoService para cancelar.

Verificado revirtiendo el arreglo: sin el, la prueba
noSePuedePagarElPedidoDeOtro falla.

Pruebas anadidas en SeguridadWebPerfilTest (10 casos):

- Perfil: /cliente/me y /cliente/actualizar exigen sesion, y ambos
  resuelven el cliente desde auth.getName(), no desde un parametro.
- Envios: /envio exige sesion iniciada.
- Pagos: un anonimo no paga; un cliente paga su pedido; y un cliente no
  puede tocar el pedido de otro (no se llama a PagoService, no se guarda
  el pedido y nunca se busca por id sin filtrar por dueño).

Nota: el frontend no usa /pago/procesar. El cobro real ocurre en
/pedido/confirmar, que trabaja sobre el carrito del propio cliente.
Conviene valorar si este endpoint debe seguir existiendo.

Suite completa: 101 pruebas, todas pasando.

---

### 8. Eliminar /pago/procesar y su cadena de codigo muerto

`187e0df` · 2026-08-01

El endpoint no lo usaba nadie: el frontend cobra en /pedido/confirmar,
que trabaja sobre el carrito del propio cliente y descuenta stock.
/pago/procesar duplicaba el cobro sin tocar el stock, y era el endpoint
donde estaba el fallo de acceso a pedidos ajenos corregido en el commit
anterior.

Se eliminan los cuatro archivos que existian solo para el:

  - controller/PagoController.java   unico consumidor de PagoService
  - service/PagoService.java         solo lo usaba ese controlador
  - repository/PagoRepository.java   solo lo usaba ese servicio
  - dto/PagoRequest.java             no lo usaba nadie, ni el controlador,
                                     que recibia los datos por @RequestParam

Se conserva model/Pago.java a proposito: mapea la tabla "pago", que
puede tener registros historicos, y quitarla seria una decision sobre el
modelo de datos, no limpieza de codigo muerto. Con ddl-auto=update
Hibernate no borra tablas, asi que los datos siguen intactos.

SeguridadWebPerfilTest pierde sus 3 casos de pago y queda cubriendo
perfil y envios.

Verificado: no queda ninguna referencia en src/main, la aplicacion
arranca sin errores y en target/classes ya no existen PagoController,
PagoService ni PagoRepository. Suite: 98 pruebas, todas pasando.

---

### 9. Probar las consultas de reportes y hacer portable ventasPorDia

`bd086da` · 2026-08-01

PedidoRepositoryReportesTest (15 casos) cubre las cinco consultas que
alimentan el panel de administracion, sobre una base H2 en memoria que se
crea y se destruye con cada clase, sin tocar la base de desarrollo.

Todas las consultas filtran por estado = 'PAGADO', asi que cada bloque
incluye un pedido CANCELADO para comprobar que no se cuela en las cifras,
y un caso sin datos que verifica que el COALESCE devuelve 0 y no null
(si devolviera null, el panel reventaria).

Al escribir las pruebas se vio que ventasPorDia solo funcionaba en MySQL:

    AND DATE(p.fecha) = :fecha

DATE() no es una funcion estandar de HQL, asi que Hibernate la pasaba tal
cual al SQL generado. MONTH() y YEAR(), que si son estandar, se traducen
por dialecto y no daban problema.

Ahora se consulta por rango semiabierto [inicio, fin) con ventasEntre, y
ventasPorDia queda como metodo default que calcula ese rango. Ademas de
ser portable, aplicar una funcion sobre la columna impedia aprovechar un
indice sobre fecha; comparar por rango si lo permite. La firma publica no
cambia, asi que ReporteController queda igual.

Se anade H2 al pom con scope test: no se empaqueta con la aplicacion, que
sigue usando MySQL.

Verificado: 113 pruebas pasando, y la aplicacion arranca contra MySQL sin
errores, lo que valida la nueva consulta para ese dialecto.

---

### 10. Anadir pruebas al frontend y eliminar los scaffolds rotos

`e00c87b` · 2026-08-01

El proyecto tenia 20 archivos .spec.ts generados por el CLI de Angular que
nunca se tocaron. 17 de ellos ni siquiera compilaban: importaban los
nombres originales de las clases (Admin, Carrito, Login...) y los
componentes se renombraron despues (AdminDashboardComponent,
CarritoComponent, LoginComponent...). Los 3 restantes solo comprobaban
toBeTruthy(), y app.spec.ts esperaba un <h1> con "Hello, desarrollo2" que
ya no existe. En la practica "ng test" nunca habia llegado a ejecutarse.

Se eliminan los 20 y se escriben pruebas de lo que si tiene riesgo,
siguiendo el mismo criterio que en el backend: sesion y control de acceso.

- auth.service.spec.ts (18): guardado y limpieza de la sesion en
  localStorage, isAdmin, y extractErrorMessage, que tiene que entender los
  tres formatos en que responde el backend (objeto, JSON como texto y
  texto plano) porque varios endpoints usan responseType 'text'. Las
  llamadas HTTP se verifican con HttpTestingController.

- guards.spec.ts (7): AuthGuard y AdminGuard. Incluye que sin sesion se
  vaya al login y no al inicio, y que en ese caso ni se pregunte si el
  usuario es administrador.

- auth.interceptor.spec.ts (5): la cabecera Authorization se adjunta solo
  si hay token, el resto de la peticion no se altera, y un token vacio se
  trata como sesion ausente en vez de mandar "Bearer " suelto.

- carrito-state.service.spec.ts (7): el contador compartido de la
  cabecera, incluido que quien se suscribe tarde reciba el valor actual.

Nota sobre el alcance: los guardias solo controlan la navegacion en el
navegador. La seguridad real la impone el backend validando el token en
cada peticion; eso ya lo cubren las pruebas de SeguridadWeb*.

Se anade el script "npm run test:ci", que ejecuta la suite sin modo watch
en Chrome headless.

Resultado: 35 pruebas, todas pasando.

---

### 11. Paginar el catalogo y mover busqueda y filtros al backend

`65a8964` · 2026-08-01

GET /productos devolvia la lista completa: 153 productos con sus tallas,
imagenes, categoria y tipo en cada carga de la tienda. El frontend
descargaba todo eso y filtraba en el navegador, asi que la busqueda solo
alcanzaba a lo ya descargado.

Backend:

- ProductoRepository.buscar(): consulta paginada con cuatro filtros
  opcionales (nombre, categoria, tipo y talla). Cada uno se ignora cuando
  llega null, de modo que la misma consulta sirve para el listado y para
  cualquier combinacion. La consulta de conteo va aparte porque el join
  con las tallas duplica filas y el total de paginas saldria inflado.

- GET /productos acepta page, size y los cuatro filtros, con 12 productos
  por pagina por defecto. Los parametros en blanco se normalizan a null:
  el navegador manda "" al vaciar el buscador.

Frontend:

- ProductoService.buscar() construye la peticion y expone Pagina<T>.
- principal y menu: busqueda con debounce de 300 ms, filtros que ahora se
  combinan entre si (antes cada uno pisaba al anterior) y controles de
  paginacion.
- productos (admin): la tabla ya paginaba, pero en el navegador tras
  descargar el catalogo entero; ahora pide solo la pagina visible.
- admin: el panel pide la primera pagina y muestra el total aparte.

Al escribir las pruebas aparecio un fallo en la consulta: escribir las
asociaciones como rutas (pt.talla.nombre, p.categoria.nombre) hace que
HQL genere INNER JOIN, no LEFT JOIN. Eso dejaba fuera del catalogo a todo
producto sin tallas, sin categoria o sin tipo asignados. Se corrigio con
LEFT JOIN explicitos y quedan dos pruebas de regresion.

ProductoRepositoryBusquedaTest (15 casos) cubre tamaño de pagina, total
de resultados, ultima pagina parcial, paginas sin solapamiento, cada
filtro por separado, su combinacion, y que un producto con varias tallas
no se cuente dos veces.

Verificado en el navegador: escribir "polo" lanza una sola peticion
(?nombre=polo), devuelve 17 resultados en 2 paginas, y el paso a la
pagina 2 trae los 5 restantes.

Suites: 128 backend, 35 frontend.

---

### 12. Adaptar la tienda a ropa deportiva: deportes y rediseño de la portada

`3f90652` · 2026-08-01

Backend — concepto de deporte:

- Entidad Deporte (nombre + icono) y su repositorio. Es una dimension
  distinta de la categoria: la categoria dice para quien es la prenda
  (Mujer, Varon, Niños) y el deporte para que se usa.
- Producto gana una referencia a Deporte, que admite null porque el
  catalogo se cargo antes de que el campo existiera.
- La busqueda paginada acepta un quinto filtro, deporte, con el mismo
  LEFT JOIN explicito que el resto: asi los productos sin deporte
  asignado siguen apareciendo en el catalogo.
- DeporteController con lectura publica y escritura solo para ROLE_ADMIN,
  igual que categorias y tallas.
- DeporteSeeder crea los seis deportes iniciales y asigna uno a los
  productos que no lo tienen, deduciendolo del nombre de la prenda. Solo
  toca productos con deporte nulo, asi que se puede ejecutar varias veces
  y nunca pisa una asignacion hecha a mano desde el panel.

Frontend — PrimeNG, Tailwind y nueva identidad:

- Se instalan PrimeNG 20.4, @primeuix/themes y Tailwind 4 sobre Angular
  20.3.27. Hubo que alinear todos los paquetes de Angular en la misma
  version y regenerar el lockfile: el arbol mezclaba 20.3.6 y 20.3.27 y
  ninguna combinacion resolvia. De paso se eliminan axios y
  react-router-dom, que estaban declarados y no se usaban en ningun sitio.
- Tema propio para PrimeNG en clave deportiva: base monocroma para que
  mande la fotografia del producto, un unico acento lima para llamadas a
  la accion, y componentes sin redondeos.
- styles.css recoge los tokens de marca y las piezas reutilizables
  (tarjeta de producto, cuadricula, botones, insignias).
- Portada rehecha: cabecera fija, hero, deportes y categorias en
  cuadricula, destacados, franja de valores y pie. Al elegir una
  categoria o un deporte, la portada se sustituye por la cuadricula de
  resultados con fichas retirables y contador.

Verificado en el navegador: /deportes responde, la portada monta las seis
tarjetas de deporte y las cinco de categoria, y al pulsar Running la
cuadricula pasa a 4 columnas con sus 12 productos.

Suites: 132 backend, 35 frontend.

---

### 13. Rediseñar el catalogo /menu con la identidad deportiva

`211de4f` · 2026-08-01

El catalogo se queda con el mismo lenguaje visual que la portada:
cabecera fija, tipografia en mayusculas, base monocroma y acento lima.

- Filtros en columna lateral fija, agrupados por deporte, categoria,
  tipo de prenda y talla. En movil la columna se pliega tras un boton.
- Se añade el filtro por deporte, que hasta ahora solo estaba en la
  portada, con el icono de cada uno.
- Los filtros activos se pintan como fichas retirables y el enlace
  activo se resalta tambien en la barra lateral, asi se ve de un vistazo
  que esta aplicado.
- Cuadricula de producto reutilizada de styles.css: imagen 3:4, zoom
  suave al pasar el raton, insignia con el deporte, corazon de favorito
  y accion inferior deslizante.
- El desplegable de tallas pasa a p-select y la paginacion a
  p-paginator, ambos ajustados al tema (sin redondeos).
- La cabecera muestra el contador del carrito en una burbuja.

Verificado en el navegador: los cuatro grupos de filtros se montan, la
cuadricula sale a 4 columnas con 12 de 153 productos y 5 paginas, y al
combinar Running con Mujer la peticion viaja como
?categoria=Mujer&deporte=Running y devuelve 5 productos, con las dos
fichas activas.

Suites: 132 backend, 35 frontend.

---

### 14. Rediseñar detalle, carrito y checkout con la identidad deportiva

`8664d7c` · 2026-08-01

Con estas tres pantallas, todo el recorrido de compra queda con el mismo
lenguaje visual que la portada y el catalogo.

Detalle de producto:
- Galeria con miniaturas en columna y foto principal en 3:4, migas de
  pan y ficha que acompaña al scroll.
- Selector de talla en botones: la talla sin stock sale tachada y
  deshabilitada, y al elegir una se muestran las unidades disponibles.
- El boton principal explica que falta ("Elige una talla") en vez de
  quedarse mudo y deshabilitado.

Carrito:
- Lista con imagen, talla, precio unitario e importe por linea, y
  selector de cantidad con botones en lugar del input numerico suelto.
- Resumen fijo al hacer scroll con entrega, coste y total.
- Estado vacio con salida al catalogo, que antes no existia.

Checkout:
- Columna unica centrada, sin cabecera de navegacion: en esta pantalla
  la unica accion util es terminar la compra.
- El boton de confirmar tambien se deshabilita si no hay tarjeta
  seleccionada, no solo mientras carga.
- Estados de error y de exito con entidad propia.

Ademas, la cabecera comun (logo, navegacion, buscador, iconos, contador
del carrito) pasa a styles.css junto con las fichas, los estados vacios
y las variantes de boton. Estaba duplicada en portada y catalogo y este
cambio la habria triplicado.

Verificado en el navegador: el detalle del producto 1 monta migas,
galeria, insignia de deporte y sus cuatro tallas; al pulsar M queda
activa, informa de 105 disponibles y habilita el boton. El carrito
muestra el estado vacio y el checkout su formulario, su total y el
mensaje de error cuando no puede cargar tarjetas.

Suites: 132 backend, 35 frontend.

---

### 15. Gestionar deportes desde el panel de administracion

`2ed6577` · 2026-08-01

Cierra el circuito abierto al introducir el concepto de deporte: hasta
ahora existia el endpoint pero no habia forma de usarlo desde la
aplicacion, asi que el reparto que dejo el sembrado automatico no se
podia corregir.

- DeporteService para el catalogo de deportes.
- Panel de administracion: alta de deportes con nombre e icono, listado
  en fichas y borrado con confirmacion, avisando de que los productos
  que lo usen quedaran sin deporte asignado (no se borran).
- Pantalla de productos: desplegable de deporte en el formulario, con
  opcion "Sin asignar", y nueva columna en la tabla que distingue los
  productos ya clasificados de los que no.

Ademas se corrige un fallo anterior: al editar un producto, los
desplegables de Categoria y Tipo de prenda salian vacios aunque el
producto tuviera valor. [ngValue] compara por referencia y los objetos
anidados del producto vienen de una peticion distinta que la de los
desplegables: mismo contenido, distinta instancia. Se resuelve con
[compareWith] comparando por id, y se aplica tambien al nuevo
desplegable de deporte.

Verificado en el navegador: el panel lista los seis deportes con sus
iconos, la tabla de productos muestra la columna Deporte, y al editar
"Pantalon cargo" los tres desplegables quedan preseleccionados en
Mujer, Pantalon y Training.

Suites: 132 backend, 35 frontend.

---

### 16. Panel de administracion: deportes, y migrar las pantallas de cuenta

`a29b4f8` · 2026-08-01

Administracion:
- El formulario de producto gana el campo Deporte y la tabla su columna,
  con "Sin asignar" cuando el producto no lo tiene. Esto es lo que
  permite corregir el reparto que dejo el sembrado automatico.
- El dashboard gana una seccion de deportes: alta con nombre e icono,
  listado en fichas y borrado. Al eliminar un deporte los productos que
  lo usaban quedan sin asignar, no se borran; el confirm lo advierte.
- Nuevo DeporteService en el frontend.

Al añadir el campo apareció un fallo que ya existía: los desplegables de
Categoria y Tipo salian vacios al editar un producto. [ngValue] compara
por referencia y los objetos anidados del producto vienen de una
peticion distinta que la de los desplegables: mismo contenido, distinta
instancia. Se resuelve con [compareWith] comparando por id, y se aplica
a los tres desplegables.

Pantallas de cuenta (favoritos, perfil, direcciones, tarjetas):
- Diseño comun de zona de cuenta en styles.css: navegacion lateral fija
  que marca la pantalla activa, panel de contenido, campos de formulario
  y estado vacio. La cabecera compartida tambien vive ya alli.
- Favoritos reutiliza la cuadricula de producto de la tienda.
- Perfil separa el correo (no editable, identifica la cuenta) del resto,
  y solo habilita los campos en modo edicion.
- Direcciones: formulario plegable y listado en fichas con etiqueta.
- Tarjetas: cada una se pinta como una tarjeta fisica, mostrando solo
  los cuatro ultimos digitos, que es lo unico que envia el backend.
- favoritos y tarjetas no tenian los metodos de navegacion de cuenta que
  necesita la barra lateral; se añaden.

Ademas, mi-perfil no tenia rama de error al cargar: si la peticion
fallaba la pantalla se quedaba en "Cargando..." indefinidamente. Ahora
muestra un aviso con salida al login.

Verificado en el navegador: las cuatro pantallas montan su navegacion
lateral con la entrada activa correcta y su estado vacio, y el
formulario de direcciones se abre con sus cuatro campos.

Suites: 132 backend, 35 frontend.

---

### 17. Usar BigDecimal para todos los importes en lugar de double

`506b004` · 2026-08-01

double es binario y no representa exactamente valores como 0.10 o 89.90.
En una tienda eso se traduce en totales con arrastre (59.970000000000006)
que acaban impresos en la factura y guardados en el saldo de la tarjeta.

Se convierten a BigDecimal los diez campos monetarios del modelo
(precio, total, precio unitario, saldo de tarjeta y de la tienda, costo
de envio, monto del pago y los tres importes de la factura), los DTOs
que los transportan y las cinco consultas de reportes.

La aritmetica de PedidoService pasa a add/subtract/multiply. La
comparacion de saldo usa compareTo y no equals: equals distingue 10.0 de
10.00 porque tiene en cuenta la escala, y aqui interesa el valor.

Las entidades declaran @Column(precision = 12, scale = 2), que es lo que
usara Hibernate al crear la tabla en una base nueva.

FALTA UN PASO MANUAL EN LA BASE DE DATOS EXISTENTE

La aplicacion arranca con ddl-auto=update, y ese modo solo añade tablas
y columnas que falten: nunca cambia el tipo de una columna que ya existe.
Sin ejecutar migraciones/001-importes-a-decimal.sql los importes se
siguen guardando en columnas DOUBLE y el arreglo queda a medias: exacto
al calcular, inexacto al guardar. Se nota ya en la API, que devuelve
99.9 en vez de 99.90 porque el valor viene de una columna DOUBLE.

Pruebas: se añade Importes con los helpers imp() y assertImporte(), que
compara por valor y no por escala, y dos casos nuevos que fijan la
precision: 19.99 x 3 mas 10.01 de envio da exactamente 69.98, y 0.10
diez veces da exactamente 1.00. Ambos fallaban con double.

Verificado: 134 pruebas y la aplicacion arranca contra MySQL sin errores.
El JSON no cambia de forma, asi que el frontend no se ve afectado.

---

### 18. Dejar de almacenar el CVV de las tarjetas

`4171484` · 2026-08-01

PCI DSS prohibe guardar el codigo de verificacion de la tarjeta una vez
autorizado el pago, y la prohibicion es absoluta: no vale cifrarlo ni
guardar su hash. Su unica funcion es viajar hasta la pasarela en el
momento del cobro y desaparecer. Aqui se guardaba en claro, en la
columna tarjeta.cvv.

El arreglo es quitarlo, no protegerlo:

- La entidad Tarjeta pierde el campo y sus accesores, con un comentario
  que explica por que no debe volver.
- Se elimina findByNumeroAndCvvAndFechaVencimiento del repositorio y el
  DTO PedidoConfirmarRequest. Ambos eran codigo muerto que quedo del
  flujo de /pago/procesar, retirado en un commit anterior; nadie leia el
  CVV almacenado.
- El formulario de alta sigue pidiendolo y validando su formato, porque
  es lo que el usuario espera, pero ya no lo envia: el backend no lo
  guarda y mandarlo solo lo expondria en la red y en los registros.

TarjetaTest fija la regla mirando la estructura de la entidad, que es
inusual, pero lo que hay que impedir es exactamente que alguien vuelva a
declarar el campo. Cubre tambien las variantes de nombre habituales
(cvc, csc, cid...) y comprueba que siguen estando los campos que si se
necesitan para operar. Verificado reintroduciendo el campo: la prueba
falla y explica el motivo.

FALTA UN PASO MANUAL: ddl-auto=update nunca elimina columnas, asi que la
columna cvv sigue en la tabla con los codigos ya guardados. Se retira con
migraciones/002-eliminar-cvv.sql.

Ese script deja anotado un punto que queda abierto: la columna 'numero'
guarda el numero completo en claro. PCI DSS si permite almacenarlo, pero
solo cifrado o truncado. Con tarjetas simuladas no es urgente; con
tarjetas reales habria que cifrarlo.

Suites: 136 backend, 35 frontend.

---

### 19. Aplicar las migraciones y no versionar los respaldos

`2b24f69` · 2026-08-01

Ejecutadas las dos migraciones sobre la base de datos alta_pinta, con
respaldo previo (mysqldump de las 20 tablas, verificado antes de tocar
nada).

- 002 elimino la columna tarjeta.cvv, que guardaba el codigo de
  verificacion de las 5 tarjetas registradas. Comprobado despues: no
  queda ninguna columna cvv, cvc, csc ni cid en toda la base, y los
  datos siguen intactos (153 productos, 5 tarjetas, 15 pedidos).

- 001 resulto ser una operacion sin efecto: las diez columnas de importe
  YA eran decimal(12,2). Las convirtio Hibernate al arrancar la
  aplicacion despues del cambio a BigDecimal. Se ejecuto igualmente
  porque es idempotente y deja el estado explicito.

CORRECCION A LO QUE DIJE EN EL COMMIT ANTERIOR

Afirme que sin ejecutar 001 los importes se seguirian guardando en
columnas DOUBLE, y que la API devolvia 99.9 en vez de 99.90 como prueba
de ello. Las dos cosas eran falsas:

- Hibernate si altero el tipo de esas columnas en este caso.
- El 99.9 era un artefacto de mi comprobacion, no de la API: parseaba la
  respuesta con Python y json.load convierte los numeros a float,
  perdiendo el cero final. La API envia "precio":99.90, verificado ahora
  leyendo el texto crudo de la respuesta.

Los respaldos pasan a .gitignore: llevan datos de clientes y pedidos.

Verificado: la aplicacion arranca contra el esquema migrado sin errores
y la tienda responde.

---

### 20. Migrar pedidos, alta de tarjeta y las pantallas de acceso

`fc2427e` · 2026-08-01

Al migrar la zona de cuenta deje enlaces a pantallas que seguian con el
diseño viejo. La barra lateral que construi apuntaba a "Mis pedidos", que
todavia era Bootstrap: una incoherencia que introduje yo.

- pedido: listado en fichas con estado, total y acciones. Las etiquetas
  de estado pasan de badge de Bootstrap a la paleta de la tienda, con un
  color por estado. Se añade irDirecciones(), que faltaba para completar
  la navegacion lateral.
- agregar-tarjeta: columna unica centrada, igual que el checkout. Incluye
  un aviso explicito de que el CVV se valida pero ni se envia ni se
  guarda, para que no parezca un olvido.
- login, registro, olvido y cambio de contraseña: diseño comun de acceso
  en styles.css, con panel de marca a la izquierda y formulario a la
  derecha. En movil el panel se oculta. El registro mantiene sus dos
  pasos (alta y verificacion por codigo) indicando en cual estas.

Los textos de ayuda de contraseña repiten el criterio que valida
PasswordValidator en el backend, para que el usuario no descubra el
requisito al recibir el error.

Verificado en el navegador las seis pantallas: montan su estructura, sus
campos y sus botones, y en ninguna queda una sola clase de Bootstrap.

Suites: 136 backend, 35 frontend.

---

### 21. Unificar las paginas de categoria con el catalogo

`aba098c` · 2026-08-02

Varon, Mujer, Ninos y Bebes eran cuatro componentes que repetian entero
el catalogo: 2.365 lineas entre los doce archivos, y solo se
diferenciaban en el titulo y en la imagen del banner. Es exactamente lo
que /menu ya hace con el filtro de categoria puesto.

Ahora las cuatro rutas apuntan a MenuComponent, que lee la categoria de
los datos de la ruta y la preselecciona. Las URL no cambian, asi que los
enlaces que ya existian (irCategoria en cada cabecera) siguen
funcionando.

Lo unico que esas pantallas tenian y /menu no era la ordenacion por
precio, asi que se ha llevado al catalogo, y ademas mejor: antes
ordenaba en el navegador, con lo que solo reordenaba los productos ya
descargados. Ahora se lo pide al backend via Pageable, que ordena el
catalogo entero. Se añaden tambien "Nombre: A-Z" y la opcion de volver
al orden natural.

Verificado en el navegador: /mujer abre el catalogo con la ficha Mujer
puesta y sus 33 productos, /bebe con los 9 suyos, y al elegir "Precio de
menor a mayor" la peticion sale como
?page=0&size=12&categoria=Mujer&sort=precio,asc y los precios llegan
ordenados de verdad.

Nota aparte: al ejecutar las suites aparecieron 22 archivos que ya se
habian borrado en commits anteriores (PagoService y su cadena,
PedidoConfirmarRequest y los 20 scaffolds de prueba), reaparecidos en
disco como archivos sin seguimiento. El proyecto vive dentro de una
carpeta de OneDrive y la sincronizacion los restauro. Rompian las dos
compilaciones. Se han vuelto a eliminar, comprobando antes con git que
cada uno correspondia a un borrado previo.

Suites: 136 backend, 35 frontend.

---

### 22. Migrar el panel de administracion al diseño de la tienda

`aff5947` · 2026-08-02

Ultimas dos pantallas que quedaban en Bootstrap. Comparten el lenguaje
visual de la tienda pero con cabecera oscura y una insignia, para que se
note de un vistazo que no estas en la parte publica.

Resumen:
- Tres indicadores arriba: total vendido, pedidos pagados y tamaño del
  catalogo.
- Las consultas de ventas por dia, mes y año quedan agrupadas, con el
  resultado destacado en una franja.
- Deportes y recarga de tarjeta pasan a bloques con el mismo patron.
- Pedidos y auditoria en tablas con scroll horizontal propio, para que no
  desborden en pantallas estrechas, y con fila de "sin datos" cuando
  estan vacias.

Productos:
- El formulario se organiza en rejilla, con las tallas como celdas que se
  resaltan al marcarlas y las fotos en huecos con vista previa y boton de
  quitar superpuesto.
- La tabla gana miniatura del producto y muestra el precio con dos
  decimales.

Las cabeceras y tablas estan duplicadas entre admin.css y productos.css a
proposito: el CSS de componente esta encapsulado, asi que uno no alcanza
al otro. Llevarlas a styles.css las haria globales para toda la tienda,
que no las necesita.

Verificado en el navegador. En productos, ademas, se comprueba el arreglo
de los desplegables: al pulsar editar en un producto, Categoria, Tipo y
Deporte llegan preseleccionados (Mujer, Pantalon, Training) y sus 4
tallas marcadas. En ninguna de las dos pantallas queda una sola clase de
Bootstrap.

Con esto, las 21 pantallas del frontend estan migradas.

Suites: 136 backend, 35 frontend.

---

### 23. Convertir el catalogo a ropa deportiva y dar surtido de tallas

`0a19d9f` · 2026-08-02

Los 153 productos tenian nombres de ropa generica ("Polo Niña Karla
Coco") que no pegaban con una tienda deportiva, y 149 de ellos tenian una
sola talla: el cliente no podia elegir.

Se reescriben nombre y descripcion de los 153 con vocabulario propio de
ropa deportiva (tejido, corte, uso), se les asigna un deporte coherente
con la prenda, y cada uno pasa a tener entre 4 y 6 tallas con stock.

NO se copia el catalogo de ninguna marca. El encargo era usar los
nombres y descripciones de Gymshark, pero eso significaria reproducir su
texto comercial y sus marcas de producto en una entrega academica. Se ha
escrito un catalogo original de AltaPinta en ese mismo registro, que
cumple el objetivo sin apropiarse de material ajeno.

migraciones/generar_catalogo.py construye el SQL y queda versionado, con
semilla fija para que sea reproducible. Al escribirlo aparecieron tres
errores de redaccion que se corrigieron antes de aplicar nada: se
describian "hombros libres" en leggings, la concordancia de genero
fallaba ("Pantalon ... Confeccionada") y el publico quedaba mal colocado
("para el gimnasio de mujer"). Ahora cada prenda declara su genero
gramatical y la zona del cuerpo que viste, y de ahi salen los cortes y
remates que tienen sentido.

Efectos en la base de datos, con respaldo previo:
  - producto: 153 nombres y descripciones nuevos, todos con deporte
  - producto_talla: se rehace entera, de 157 filas a 816
  - el reparto de deportes deja de estar sesgado por el valor por defecto
    (Training baja de 103 a 64, Basquet sube de 0 a 5)

Los pedidos y sus lineas no se tocan: 15 pedidos siguen intactos.

Verificado en la API y en el navegador: la portada muestra los nombres
nuevos, y el detalle del producto 1 ofrece sus seis tallas seleccionables.

---

### 24. Ilustrar los productos y escalar los precios por prenda

`3e39b2f` · 2026-08-02

Cada producto mostraba una foto de ropa generica que no tenia relacion
con su nombre: un "Legging Aero" salia con la foto de un vestido. Y los
precios venian del catalogo antiguo, sin relacion con la prenda.

IMAGENES

No se descargan fotos de internet. Las de una tienda real tienen
derechos, y traerlas de un banco de imagenes significaria depender de una
descarga externa. En su lugar se dibujan siluetas propias en SVG, en la
estetica monocroma de la tienda: camiseta, sudadera, chaqueta, media
cremallera, short, pantalon, vestido, falda y conjunto de bebe.

Se genera un archivo por combinacion de prenda y deporte (54 en total, a
530 bytes de media) en vez de uno por producto: la cuadricula gana
variedad de fondo sin llenar el disco de 153 imagenes casi iguales. El
fondo y la franja superior cambian con el deporte.

PRECIOS

Pasan a escalar por tipo de prenda, que es lo que uno espera de una
tienda: camiseta 49.90-89.90, short 59.90-99.90, pantalon 99.90-159.90,
sudadera 129.90-189.90, chaqueta 179.90-259.90. El escalon depende del id
del producto, asi que el resultado es reproducible.

La primera version generaba precios como 61.32 y 214.19. Ninguna tienda
los pone asi, de modo que ahora se redondea a decena y se resta un
centimo: todos acaban en .90, comprobado en base de datos.

De paso, las tarjetas de producto de portada, catalogo y favoritos
mostraban "S/ 179.9" porque les faltaba el pipe de decimales. Corregido.

Verificado en el navegador: ninguna imagen rota, cada una corresponde a
la prenda de su nombre, y los precios se ven con sus dos decimales.

Suites: 136 backend, 35 frontend.

---

### 25. Versionar las ilustraciones de prenda

`a6de19c` · 2026-08-02

Las 54 siluetas SVG quedaban fuera del repositorio: productos-imagenes/
esta en .gitignore porque ahi van las fotos que se suben desde el panel,
que son datos y no codigo.

Estas son otra cosa. Las genera un script del propio repositorio, son
deterministas y forman parte del aspecto de la tienda, como cualquier
icono. Sin ellas, quien clone el proyecto ve el catalogo entero con la
imagen de repuesto.

Se añade una excepcion para prenda-*.svg. Las fotos subidas siguen
ignoradas, comprobado.

---

### 26. Versionar las ilustraciones de prenda

`285fbd4` · 2026-08-02

Las 54 siluetas SVG quedaban fuera del repositorio, y sin ellas quien
clone el proyecto ve todo el catalogo con la imagen de repuesto.

productos-imagenes/ esta ignorada porque ahi van las fotos que se suben
desde el panel, que son datos y no codigo. Las siluetas son otra cosa:
las genera un script del propio repositorio, son deterministas y forman
parte del aspecto de la tienda, como cualquier icono.

El primer intento de excepcion no funciono: git no puede volver a incluir
un archivo si su directorio esta excluido. Se ignora el contenido de la
carpeta en vez de la carpeta, y entonces la excepcion si aplica.

Comprobado: las 54 siluetas entran, y las fotos subidas y las facturas
PDF siguen fuera.

---

### 27. Nombres de producto descriptivos, para poder buscar sus fotos

`3af7060` · 2026-08-02

Los nombres eran de fantasia: "Camiseta Aero", "Legging Vertex". Suenan a
marca, pero pegados en un buscador de imagenes no devuelven nada, y hacen
falta fotos reales para el catalogo.

Ahora cada producto se llama por lo que es: prenda, rasgo, deporte y
color. "Camiseta de manga corta fútbol azul marino", "Legging de tiro
alto training gris". El propio nombre sirve de termino de busqueda.

Los rasgos pasan a declararse prenda por prenda en vez de por zona del
cuerpo. Aplicarlos en bloque producia disparates que se vieron al revisar
la primera tanda:

  - "Casaca deportiva sin mangas"
  - "Cortavientos de cuello redondo"
  - "Camiseta de manga corta de manga larga"
  - "Falda deportiva ajustado", sin concordancia

Cada prenda lleva ahora solo los rasgos que le caben, ya concordados: un
cortavientos puede ser impermeable o con capucha, una falda puede ser
plisada o con short interior.

Se añade migraciones/busqueda-imagenes.tsv, que empareja cada producto
con el termino que conviene buscar, ordenado por termino para que los
productos que comparten busqueda queden juntos y una misma foto sirva
para varios. Son 139 terminos para 153 productos.

Los 153 nombres siguen siendo unicos. Verificado en el navegador: cada
tarjeta muestra su ilustracion correspondiente y ninguna imagen rota.

---

### 28. Sacar del codigo la direccion del backend y las URLs de imagen

`5ec3937` · 2026-08-02

Dos cosas impedian desplegar el proyecto fuera de esta maquina.

1. LA DIRECCION DEL BACKEND ESTABA ESCRITA A MANO

Habia 14 apariciones de "http://localhost:8080" repartidas por 13
archivos del frontend, y no existia configuracion de entornos. Desplegar
obligaba a editar los 13 a mano.

Se añaden src/environments/environment.ts (produccion) y
environment.development.ts, con la sustitucion declarada en angular.json.
En produccion apiUrl queda vacio, que significa "el mismo origen que
sirve la aplicacion", lo habitual detras de un proxy inverso; si el
backend vive en otro dominio, se pone ahi y no se toca nada mas.

2. LAS URLS DE IMAGEN SE GUARDABAN COMPLETAS EN LA BASE DE DATOS

    http://localhost:8080/imagenes/prenda-camiseta-running.svg

Al desplegar, las 153 imagenes apuntarian a un servidor inexistente, y
las fotos nuevas se seguirian guardando igual. Ahora:

  - ImagenService devuelve la ruta relativa al subir una foto.
  - migraciones/005 convierte las que ya estaban guardadas. Aplicada:
    0 absolutas, 153 relativas.
  - ImagenPipe compone la URL con la direccion del entorno. Sigue
    aceptando absolutas por si queda alguna antigua, y devuelve la imagen
    de repuesto cuando el producto no tiene ninguna.

Al verificarlo, las peticiones salian a localhost:4200 en vez de 8080:
ng serve lee angular.json al arrancar y no lo relee, asi que seguia con
la configuracion anterior. Reiniciado el servidor, correcto.

La prueba de AuthService tenia tambien la URL fija; ahora la lee del
entorno, de modo que no se rompe si cambia.

Verificado en el navegador: la base de datos devuelve
"/imagenes/prenda-pantalon-training.svg", el pipe lo resuelve a
localhost:8080 y no hay ninguna imagen rota ni en portada ni en el
catalogo.

Suites: 136 backend, 35 frontend.

---

### 29. Integracion continua, README al dia y arreglos que destapo la CI

`c6312d0` · 2026-08-02

INTEGRACION CONTINUA

.github/workflows/pruebas.yml ejecuta las dos suites en cada push y pull
request, en trabajos paralelos para ver los dos resultados a la vez en
lugar de arreglar, empujar y descubrir el siguiente fallo. El frontend
compila ademas en modo produccion, que detecta lo que el modo desarrollo
deja pasar. Si el backend falla, se publican los informes de surefire.

Antes de darla por buena aparecieron dos cosas que la habrian roto:

  - mvnw estaba versionado sin permiso de ejecucion (modo 100644). En
    Windows da igual, pero el runner es Linux y habria fallado con
    "permission denied". Corregido a 100755.

  - La compilacion de produccion NO PASABA, y era culpa mia: puse el
    @import de la fuente Inter en los 16 CSS de componente, y Angular
    incrusta la hoja completa en cada uno. Ahora la fuente se carga una
    sola vez desde index.html, con preconnect.

  - favoritos.css conservaba 17 kB de estilos de la version en Bootstrap.
    Ninguno de sus 11 selectores se usaba ya en la plantilla: se
    comprobo uno a uno antes de borrarlos. El archivo queda en 426 bytes.

Con eso el bundle baja de 1.38 MB a 1.20 MB (222 kB comprimido). Los
presupuestos de angular.json eran los de un proyecto recien generado, sin
libreria de componentes; se suben a 1.3/1.6 MB, que sigue avisando si algo
se dispara.

README

Decia "Angular 20.3 · Bootstrap 5", que dejo de ser cierto hace veinte
commits. Ahora refleja PrimeNG y Tailwind, y se añaden el estado de las
pruebas y como ejecutarlas, la tabla de migraciones con que hace cada
una, la estructura del proyecto y donde vive la direccion del backend,
con el aviso de reiniciar npm start al tocar angular.json.

Suites: 136 backend, 35 frontend. Compilacion de produccion correcta.

---

### 30. Sustituir el bloqueo en memoria por uno de base de datos

`b2a13a2` · 2026-08-02

Al confirmar un pedido, RF045 impide que un mismo cliente pague dos veces
por un doble clic. Estaba resuelto con un synchronized sobre un
ConcurrentHashMap de bloqueos por cliente, y tenia tres problemas:

  - Solo protegia dentro de una instancia de la JVM. Con el backend
    detras de un balanceador, dos peticiones que caigan en instancias
    distintas no se ven entre si.

  - El mapa crecia sin limite: una entrada por cada cliente que hubiera
    comprado alguna vez, sin eliminar ninguna.

  - Y el mas serio: el synchronized se tomaba DENTRO de la transaccion.
    El segundo hilo abria su transaccion, esperaba en el cerrojo y, al
    entrar, seguia viendo su instantanea anterior. Con MySQL en
    REPEATABLE READ el carrito le aparecia lleno aunque el primero ya lo
    hubiera vaciado, asi que la proteccion no llegaba a funcionar.

Ahora se bloquea la fila del cliente en la base de datos con
findByCorreoBloqueando, que es un SELECT ... FOR UPDATE. Sirve entre
instancias, no acumula estado en memoria, y al ser una lectura con
bloqueo InnoDB devuelve la ultima version confirmada: el segundo hilo ve
el carrito ya vacio.

Se añade una prueba que verifica que se usa el buscador con bloqueo y no
el normal. Es una comprobacion de implementacion, poco habitual, pero
aqui cambiar un metodo por el otro desactiva la proteccion sin que falle
ninguna otra prueba.

Verificado: 137 pruebas y la aplicacion arranca contra MySQL sin errores,
lo que valida la consulta con bloqueo para ese dialecto.

---

### 31. Pruebas de componente del frontend y aviso de JDK incompatible

`d6b6998` · 2026-08-03

Las 35 pruebas que habia cubrian servicios, guardias e interceptor: ningun
componente. Se anaden 72 casos sobre las cuatro pantallas que tienen logica
propia, y en el camino aparecio un fallo real.

Catalogo (menu): que los cuatro filtros viajen juntos en una sola consulta
—se pisaban entre si antes—, que volver a pulsar un filtro lo desactive, que
cambiar de filtro devuelva a la primera pagina, el retardo del buscador y la
traduccion entre el indice de PrimeNG y el numero de pagina.

Carrito: el calculo del total con y sin envio, y que al volver a "recojo en
tienda" se borre el envio guardado. Si quedara puesto, el cliente pagaria un
envio que no pidio.

Ficha de producto: que no deje elegir una talla agotada ni una sin stock
declarado, y que al carrito se mande el id de la talla y no el de la fila de
stock.

Panel de productos: el comparador por id de los desplegables, la traduccion
de pagina (la tabla numera desde 1 y el backend desde 0) y la cadena de
guardado, que encadena cuatro peticiones dependientes.

Fallo encontrado y corregido: en la ficha de producto, "anadir al carrito"
solo bajaba loadingCarrito al terminar bien. Como ese mismo campo deshabilita
el boton, un fallo de red lo dejaba inutilizado hasta recargar la pagina.
Ahora tambien se libera al fallar, y se sale antes si ya hay una peticion en
curso para no anadir la prenda dos veces.

Cada prueba se comprobo rompiendo a proposito lo que cubre y confirmando que
falla: seis mutaciones, seis fallos en las pruebas esperadas y en ninguna mas.

Aparte, el pom ahora rechaza JDK 24 y 25. Con esos, Byte Buddy no sabe leer
las clases que generan y Mockito devuelve 77 errores de "Failed to load
ApplicationContext" que parecen un fallo del codigo. El enforcer corta antes
con un mensaje que dice como arreglarlo.

Backend 137 / Frontend 107 / Build de produccion 1.20 MB.

---

### 32. Flyway gobierna el esquema; ddl-auto pasa de update a validate

`b532725` · 2026-08-03

Hasta ahora el esquema lo creaba Hibernate con ddl-auto=update y lo que ese
modo no sabe hacer -cambiar el tipo de una columna, borrar otra- se aplicaba
a mano con los .sql de migraciones/. Eso dejaba la unica copia fiel del
esquema dentro de la base de datos de una maquina concreta: nadie mas podia
reconstruirla, y los desajustes tardaban meses en aparecer. Los importes
siguieron siendo DOUBLE mucho despues de pasar las entidades a BigDecimal
justamente por eso.

V1__esquema_inicial.sql es el volcado del esquema real ya corregido. Se
genero con mysqldump --no-data sobre la base que estaba funcionando, no a
mano, para que no hubiera diferencias; se le quitaron los AUTO_INCREMENT=N
para que una base nueva numere desde 1.

En una base que ya existe V1 no se ejecuta: baseline-on-migrate la marca como
"ya esta en la version 1" y sigue desde V2. Solo corre al crear una vacia.

ddl-auto pasa a validate. Ahora un desajuste entre entidades y esquema se ve
al arrancar en lugar de convertirse en un error raro mas adelante.

Los dos tests de repositorio corren sobre H2 y las migraciones estan escritas
en SQL de MySQL, asi que llevan spring.flyway.enabled=false: su esquema lo
sigue creando Hibernate desde las entidades.

Comprobado, en este orden:

  - Sobre una base de datos vacia aparte (no la de desarrollo): Flyway aplico
    V1 y la aplicacion arranco con validate. Que validate pase sobre un
    esquema construido solo por V1 prueba las dos cosas a la vez, que V1 es
    completo y que coincide con las entidades.
  - Volcados de las dos bases comparados: los 20 CREATE TABLE son identicos.
    La unica diferencia era de escritura, mysqldump omite CHARACTER SET
    utf8mb4 cuando coincide con el de la tabla; las collations coinciden.
  - Sobre la base de desarrollo: baseline correcto, arranque con validate sin
    errores, catalogo respondiendo y datos intactos (153 productos, 6
    clientes, 15 pedidos). Lo unico que cambia es la tabla nueva
    flyway_schema_history, con una sola fila BASELINE.

Los cinco .sql de migraciones/ ya no se ejecutan; su contenido esta dentro de
V1. Se conservan como registro, con migraciones/LEEME.md explicando que hizo
cada uno y como se hace un cambio de esquema a partir de ahora.

Backend 137 / Frontend 107.

---

### 33. Insignia de CI en el README y nombres de trabajo sin numeros

`cd92d0e` · 2026-08-03

Los nombres de los trabajos decian "136 pruebas" y "35 pruebas" cuando ya
son 137 y 107. Un numero ahi se queda obsoleto en cuanto se anade una prueba,
asi que se quita en lugar de corregirlo: quedan "Backend" y "Frontend".

La insignia si refleja el estado real de la ultima ejecucion, sin mantenerla.

---

### 34. README: aclarar los requisitos de la App Password de correo

`dd2fe36` · 2026-08-03

Decia "el correo Gmail", pero tambien funciona una cuenta institucional de
Google Workspace: comprobado autenticando contra el SMTP.

Se anaden las dos trampas que cuestan tiempo: la pagina de App Passwords solo
existe si la cuenta tiene verificacion en dos pasos, y con varias sesiones de
Google abiertas es facil generarla en una cuenta distinta de la de
MAIL_USERNAME, lo que falla con un error de autenticacion poco claro.

---

### 35. Validar la entrada de la API en lugar de aceptar cualquier JSON

`93058a2` · 2026-08-03

Solo 2 de los 8 DTO tenian restricciones y solo 3 de 16 controladores
usaban @Valid. En la practica, la mayoria de endpoints guardaba lo que le
llegara: una categoria sin nombre, una tarjeta con saldo negativo o una
cantidad de cero en el carrito entraban sin que nada se quejara. La logica
de negocio frenaba algunas cosas, pero por casualidad y no por diseno.

Restricciones anadidas a las entidades que se reciben como cuerpo de
peticion (Categoria, Talla, TipoPrenda, Deporte, Tarjeta) y a los DTO que
faltaban (ActualizarPerfilRequest, ConfirmarPedidoDTO), con @Valid en los
siete controladores que no lo tenian.

Dos casos merecen explicacion:

La cantidad del carrito no viaja en el cuerpo sino como parametro, asi que
@Valid no la alcanza: hace falta @Validated en la clase. Eso lanza
ConstraintViolationException, que el manejador global no cubria y habria
salido como 500. Ahora hay un manejador para ella y otro para el cuerpo
ilegible, que antes devolvia la pagina de error de Spring con la traza.

El vencimiento de la tarjeta admite MM/AA y MM/AAAA a proposito. El
formulario pide cuatro digitos, pero en la base hay tarjetas guardadas como
"12/25", y un patron estricto las habria roto al confirmar un pedido: ahi se
descuenta el saldo y se vuelve a guardar la tarjeta, y Hibernate valida la
entidad antes de escribirla. El cliente no habria podido comprar y el error
no habria senalado a su tarjeta. Se vio mirando los datos reales antes de
elegir el patron, no despues.

Por lo mismo, el DNI y el RUC del perfil aceptan la cadena vacia: la
pantalla manda el cliente entero y quien no tiene RUC lo envia en blanco.
Exigirlo impediria guardar cualquier otro cambio del perfil.

17 pruebas nuevas comprueban que la peticion se RECHAZA con 400 y con un
mensaje que nombra el campo, no que la anotacion este escrita. Se verificaron
quitando @Valid de Categoria y Tarjeta y el @Min del carrito: fallaron 9 de
las 17, exactamente las que cubren eso, y ninguna de las de casos validos.

Comprobado ademas que la aplicacion sigue arrancando con ddl-auto=validate
—@Size en una entidad podria haber hecho que Hibernate exigiera varchar(50)
y bloqueara el arranque— y que el catalogo sigue sirviendo los 153 productos.

Backend 154 pruebas.

---

### 36. Eliminar la tabla y la entidad Pago (migracion V2)

`5632bf4` · 2026-08-03

Sobraban desde que se retiro el endpoint /pago/procesar, que buscaba el
pedido solo por su id sin comprobar de quien era: cualquier cliente
autenticado podia marcar como RECHAZADO el pedido de otro. Se quito el
endpoint, pero la entidad y su tabla se quedaron, y Hibernate seguia
creandola con ddl-auto=update sin que nadie leyera ni escribiera en ella.

Comprobado antes de borrar: ninguna referencia a Pago en el backend ni en
el frontend, 0 filas en la tabla, y ninguna otra tabla apuntando a ella
(solo pago apuntaba a pedido y a tarjeta, y esas claves ajenas se van con
ella).

Es el primer cambio de esquema que pasa por Flyway de principio a fin, no
solo la linea base. Verificado sobre la base de desarrollo: el historial
tiene ahora la fila V2, la tabla ya no existe, la aplicacion arranca con
ddl-auto=validate y los datos siguen enteros (153 productos, 6 clientes,
15 pedidos, 5 tarjetas).

El pago real vive dentro de PedidoService, que valida la tarjeta, descuenta
el saldo y registra el ingreso en la misma transaccion que crea el pedido.

Backend 154 pruebas, con clean para que no quedara la clase antigua en el
classpath.

---

### 37. Pruebas del checkout y arreglo del mensaje de error

`dbdd089` · 2026-08-03

El checkout no tenia ninguna prueba, siendo la pantalla donde se cobra. Se
anaden 17 casos: el calculo del total, la carga de tarjetas, el envio
heredado del carrito, las dos guardas que impiden pagar, el envio de la
peticion y el camino de fallo.

Escribiendolas aparecio un fallo real. El backend responde los errores como
{"message": "..."}, pero el componente guardaba err.error entero en el
campo de texto:

    this.error = err?.error || 'Error procesando pedido, stock insuficiente';

La plantilla lo pinta con {{ error }}, asi que el cliente veia
"[object Object]" justo cuando le fallaba la compra, en lugar de "Stock
insuficiente" o "Saldo insuficiente en la tarjeta". Comprobado en el
navegador con el cuerpo de error real.

Era el unico sitio del frontend con ese descuido: los demas ya usaban
err.error?.message. Ahora tambien contempla la cadena suelta, porque no
todos los endpoints pasan por el manejador global.

De paso, el mensaje generico ya no afirma "stock insuficiente", que era
una causa inventada cuando el fallo podia ser de red.

Las pruebas se verificaron rompiendo lo que cubren: quitando la guarda de
saldo y fijando el envio a null fallaron las dos que tocaba y ninguna mas.

Frontend 125 pruebas. Build de produccion 1.20 MB.

---

### 38. Prueba de integracion: la compra de principio a fin

`80e5568` · 2026-08-03

Las otras 154 miran una capa cada una -el servicio con dobles, la web con
MockMvc, el repositorio con H2- y todas pueden pasar mientras las piezas no
encajan entre si. Esta levanta la aplicacion entera y recorre registro,
verificacion, inicio de sesion, catalogo, carrito y pedido arrastrando el
token JWT de un paso al siguiente, y luego comprueba como quedo la base:
stock descontado, saldo de la tarjeta, ingreso en la cuenta de la tienda,
carrito vacio y pedido registrado.

Cubre ademas lo que ninguna prueba de una sola capa puede ver:

  - Que un cobro fallido revierta la transaccion ENTERA. Con saldo
    insuficiente no debe quedar ni stock descontado, ni saldo tocado, ni
    pedido a medias, y el carrito debe seguir con lo que habia.
  - Que confirmar dos veces no duplique la compra: el carrito ya vaciado
    hace fallar el segundo intento.
  - Que sin token, o con uno inventado, no se llegue al carrito.

Corre sobre H2 en memoria, nunca sobre la base de desarrollo, y con
EmailService y FacturaPdfService sustituidos por dobles: una prueba no manda
correos de verdad ni deja archivos por el disco.

Se llama ...Test y no ...IT porque Surefire solo recoge *Test, y una prueba
de integracion que no se ejecuta no sirve de nada.

Tres cosas que costo dar con ellas y quedan documentadas en el codigo:

  - La aplicacion firma con HS512, que exige una clave de 64 bytes; con 63
    el login devuelve 400 y el mensaje no llega al cliente. Por eso las
    aserciones de registro y login muestran el cuerpo de la respuesta.
  - Los datos de referencia se reutilizan en lugar de recrearse:
    Deporte.nombre es unico y reinsertarlo reventaba desde la segunda
    prueba.
  - cuenta_tienda se pone a cero en cada prueba. Sin eso el ingreso de una
    se sumaba al de la siguiente y el fallo solo aparecia al correr la suite
    completa, no la clase sola.

La clase no lleva @Transactional a proposito: la prueba del cobro fallido
necesita que la transaccion del servicio se revierta sola y poder leer
despues como quedo la base. A cambio, la limpieza es a mano y en el orden
que permiten las claves ajenas.

Verificada rompiendo lo que cubre: sin el vaciado del carrito, la segunda
confirmacion devolvio 200 en lugar de 400 -es decir, el cliente pagaba dos
veces-, y sin el descuento de stock fallo la comprobacion del inventario.

Backend 160 pruebas.

---

### 39. La portada publica ya no pide favoritos sin sesion

`42af608` · 2026-08-03

/principal es publica, pero pedia los favoritos del cliente al cargar. Sin
token el backend responde 403 y la llamada no trataba el error, asi que
Angular lo dejaba en la consola: cualquiera que abriera las herramientas del
navegador veia dos errores rojos en la primera pantalla de la tienda.

Ahora solo se piden si hay sesion, y ademas se trata el error. Comprobado en
el navegador: la portada carga sin la peticion a /favoritos y todas las
demas responden 200.

De paso se quita un bloque comentado con dos console.log que ya no se
ejecutaba, y el metodo queda con una linea que dice lo que hace.

Frontend 125 pruebas.

---
