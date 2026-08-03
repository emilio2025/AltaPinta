# Anexo: defectos detectados y corregidos — AltaPinta

Curso de **Ingeniería de Software II** — UNAMBA
Repositorio: https://github.com/emilio2025/AltaPinta

Este anexo recoge los **17 defectos** encontrados y corregidos durante la fase
de aseguramiento de la calidad. Cada uno indica qué fallaba, qué consecuencia
tenía, cómo se detectó, cómo se corrigió y el commit que lo arregla.

> **Cómo se detectaron.** Doce de los diecisiete aparecieron **al escribir una
> prueba**, no al usar la aplicación. Ese es el dato que mejor resume el valor
> del trabajo: el software funcionaba aparentemente bien y pasaba una revisión
> manual, pero contenía fallos de control de acceso, de precisión monetaria y
> de concurrencia que solo se vieron al forzar sus límites con una prueba
> automática.

---

## Resumen

| # | Defecto | Categoría | Gravedad | Commit |
|---|---|---|---|---|
| 1 | Credenciales en archivos versionados | Seguridad | Alta | `2eae03a` |
| 2 | Cualquier visitante podía escribir en el catálogo | Seguridad | Alta | `e3b2131` |
| 3 | Un cliente podía marcar como rechazado el pedido de otro | Seguridad | Alta | `9fd0735` |
| 4 | El CVV de la tarjeta se almacenaba en claro | Seguridad | Alta | `4171484` |
| 5 | Importes en `double`: totales con arrastre | Corrección | Alta | `506b004` |
| 6 | El bloqueo antidoble-pago no bloqueaba nada | Concurrencia | Alta | `b2a13a2` |
| 7 | Productos sin talla desaparecían del catálogo | Corrección | Media | `65a8964` |
| 8 | Los filtros del catálogo se pisaban entre sí | Corrección | Media | `65a8964` |
| 9 | La API aceptaba prácticamente cualquier JSON | Corrección | Media | `93058a2` |
| 10 | `ventasPorDia` solo funcionaba en MySQL | Portabilidad | Media | `bd086da` |
| 11 | `@PreAuthorize` devolvía 400 en lugar de 403 | Diagnóstico | Baja | `432ca41` |
| 12 | Desplegables vacíos al editar un producto | Uso | Media | `2ed6577` |
| 13 | «Añadir al carrito» quedaba inutilizado tras un fallo de red | Uso | Media | `d6b6998` |
| 14 | El checkout mostraba «[object Object]» al fallar la compra | Uso | Media | `dbdd089` |
| 15 | La portada pública dejaba errores en la consola | Uso | Baja | `42af608` |
| 16 | Direcciones del servidor incrustadas en el código | Despliegue | Alta | `5ec3937` |
| 17 | La compilación de producción no pasaba | Despliegue | Alta | `c6312d0` |

---

## A. Seguridad

### 1. Credenciales en archivos versionados

**Qué fallaba.** La contraseña de MySQL, el secreto de firma de los tokens JWT
y la contraseña de la cuenta de correo estaban escritos en
`application.properties`, un archivo que se sube al repositorio.

**Consecuencia.** Cualquiera con acceso al repositorio obtenía las credenciales
del sistema. Un secreto JWT conocido permite **fabricar tokens válidos** y
suplantar a cualquier usuario, incluido el administrador.

**Corrección.** `application.properties` pasa a usar referencias `${VARIABLE}`
y se sigue versionando: documenta qué configuración necesita el proyecto sin
exponer ningún valor. Los valores reales viven en `secrets.properties`, que
está en `.gitignore`, y en un servidor pueden sobrescribirse con variables de
entorno. `JWT_SECRET` se dejó **sin valor por defecto a propósito**: si falta,
la aplicación no arranca, en lugar de firmar tokens con una clave conocida.

**Commit:** `2eae03a`

---

### 2. Cualquier visitante podía escribir en el catálogo

**Qué fallaba.** `SecurityConfig` declaraba `/categorias/**` y `/tallas/**`
como `permitAll` **sin distinguir el método HTTP**. En `/tipos`, la escritura
caía en `anyRequest().authenticated()`.

**Consecuencia.** Un visitante sin cuenta podía crear categorías y tallas con
un `POST`. Para modificar los tipos de prenda bastaba con registrarse.

**Cómo se detectó.** Al escribir las pruebas de autorización del panel de
administración.

**Corrección.** Las tres rutas separan lectura de escritura: el `GET` sigue
siendo público (la tienda se navega sin cuenta) y el resto de métodos exigen
`ROLE_ADMIN`.

**Verificación.** Se revirtió el arreglo y **5 de las pruebas nuevas fallaron**,
confirmando que cubren el defecto y no lo dan por bueno.

**Commit:** `e3b2131`

---

### 3. Un cliente podía marcar como rechazado el pedido de otro

**Qué fallaba.** `PagoController` buscaba el pedido solo por su identificador:

```java
pedidoRepo.findById(pedidoId)
```

`PagoService` sí comprobaba que la tarjeta perteneciera al cliente, pero
**nadie comprobaba la propiedad del pedido**, y el controlador escribía su
estado con el resultado del cobro:

```java
pedido.setEstado(aprobado ? "PAGADO" : "RECHAZADO");
```

**Consecuencia.** Cualquier usuario con sesión iniciada podía enviar el
identificador del pedido de otra persona y, pagándolo con una tarjeta propia
sin saldo, dejar el pedido de la víctima marcado como `RECHAZADO`. Es un fallo
de control de acceso a nivel de objeto (*IDOR*).

**Corrección.** La búsqueda filtra también por el correo de la sesión mediante
`findByIdAndClienteCorreo`, que ya existía en el repositorio y es el mismo
criterio que usa `PedidoService` para cancelar. El endpoint completo se
eliminó después por ser código muerto (`187e0df`).

**Commit:** `9fd0735`

---

### 4. El CVV de la tarjeta se almacenaba en claro

**Qué fallaba.** La columna `tarjeta.cvv` guardaba el código de verificación de
cada tarjeta, en texto plano.

**Consecuencia.** El estándar **PCI DSS prohíbe almacenar el CVV** una vez
autorizado el pago, y la prohibición es absoluta: no vale cifrarlo ni guardar
su hash. Su única función es viajar hasta la pasarela en el momento del cobro
y desaparecer.

**Corrección.** El arreglo es **quitarlo, no protegerlo**. La entidad `Tarjeta`
pierde el campo y sus accesores, con un comentario que explica por qué no debe
volver. Se eliminaron además la consulta y el DTO que lo transportaban, que ya
eran código muerto: nadie leía el CVV almacenado.

**Commit:** `4171484`

---

## B. Corrección funcional

### 5. Importes en `double`: totales con arrastre

**Qué fallaba.** Los diez campos monetarios del modelo (precio, total, precio
unitario, saldo de tarjeta y de la tienda, coste de envío, importe del pago y
los tres de la factura) usaban `double`.

**Consecuencia.** `double` es binario y no representa exactamente valores como
`0.10` o `89.90`. En una tienda eso produce totales con arrastre
(`59.970000000000006`) que acaban **impresos en la factura y guardados en el
saldo de la tarjeta**.

**Corrección.** Conversión a `BigDecimal` en el modelo, los DTO que los
transportan y las cinco consultas de reportes. La aritmética pasa a
`add`/`subtract`/`multiply`. La comparación de saldo usa `compareTo` y no
`equals`: `equals` distingue `10.0` de `10.00` porque tiene en cuenta la
escala, y aquí interesa el valor. Las entidades declaran
`@Column(precision = 12, scale = 2)`.

**Commit:** `506b004`

---

### 6. El bloqueo antidoble-pago no bloqueaba nada

**Qué fallaba.** Para impedir que un mismo cliente confirmara dos pedidos a la
vez (el doble clic en «pagar»), `PedidoService` usaba un `synchronized` sobre
un mapa en memoria.

**Consecuencia.** Tres problemas, el tercero decisivo:

1. Solo protegía **dentro de una instancia**: con dos servidores, inútil.
2. El mapa **crecía sin límite**, una fuga de memoria lenta.
3. El bloqueo **se tomaba dentro de la transacción**. El segundo hilo abría su
   transacción, esperaba en el cerrojo y, al entrar, **seguía viendo su
   instantánea anterior**. Con MySQL en `REPEATABLE READ`, el carrito le
   aparecía lleno aunque el primero ya lo hubiera vaciado. Es decir: el
   mecanismo no protegía del caso para el que existía.

**Corrección.** Se sustituye por un bloqueo de base de datos:
`findByCorreoBloqueando`, una lectura con `PESSIMISTIC_WRITE`. Al ser una
lectura con bloqueo, InnoDB devuelve la última versión confirmada y no la de
la instantánea: el segundo hilo ve el carrito ya vacío.

**Commit:** `b2a13a2`

---

### 7. Productos sin talla desaparecían del catálogo

**Qué fallaba.** La consulta del catálogo escribía las asociaciones como rutas
(`pt.talla.nombre`, `p.categoria.nombre`), y **HQL las traduce a `INNER JOIN`**,
no a `LEFT JOIN`.

**Consecuencia.** Todo producto sin tallas, sin categoría o sin tipo asignados
quedaba **fuera del catálogo**, invisible para el cliente y sin ningún mensaje
de error.

**Cómo se detectó.** Al escribir las pruebas de la consulta de búsqueda.

**Corrección.** `LEFT JOIN` explícitos, con dos pruebas de regresión.

**Commit:** `65a8964`

---

### 8. Los filtros del catálogo se pisaban entre sí

**Qué fallaba.** Cada filtro trabajaba por su cuenta sobre el catálogo ya
descargado en el navegador.

**Consecuencia.** Elegir un tipo de prenda descartaba la categoría
seleccionada. Además, la búsqueda solo alcanzaba a lo ya descargado: `GET
/productos` devolvía **los 153 productos completos** en cada carga de la tienda.

**Corrección.** Consulta paginada en el backend con filtros opcionales que se
combinan; el frontend manda los cuatro juntos en una única petición, con
retardo de 300 ms en el buscador.

**Commit:** `65a8964`

---

### 9. La API aceptaba prácticamente cualquier JSON

**Qué fallaba.** Solo 2 de los 8 DTO tenían restricciones y solo 3 de 16
controladores usaban `@Valid`.

**Consecuencia.** La mayoría de endpoints guardaba lo que le llegara: una
categoría sin nombre, una tarjeta con **saldo negativo**, un vencimiento con
mes 13 o una **cantidad de cero o negativa** en el carrito. Una cantidad
negativa habría *devuelto* stock al confirmar el pedido en lugar de
descontarlo.

**Corrección.** Restricciones en las entidades que se reciben como cuerpo de
petición y en los DTO que faltaban, con `@Valid` en los siete controladores que
no lo tenían. La cantidad del carrito viaja como parámetro y no la alcanza
`@Valid`, así que necesitó `@Validated` en la clase y un manejador nuevo para
`ConstraintViolationException`, que de otro modo habría salido como error 500.

Un detalle relevante: el patrón del vencimiento admite `MM/AA` **y** `MM/AAAA`
a propósito. En la base de datos había tarjetas antiguas guardadas como
`12/25`, y un patrón estricto las habría roto al confirmar un pedido, porque
ahí se descuenta el saldo y se vuelve a guardar la tarjeta. Se comprobaron los
datos reales **antes** de elegir el patrón.

**Verificación.** Se retiró la validación de tres puntos y fallaron 9 de las 17
pruebas nuevas, exactamente las que cubren esos puntos.

**Commit:** `93058a2`

---

### 10. `ventasPorDia` solo funcionaba en MySQL

**Qué fallaba.** La consulta usaba `AND DATE(p.fecha) = :fecha`. `DATE()` no es
una función estándar de HQL, así que Hibernate la pasaba tal cual al SQL
generado.

**Consecuencia.** El informe de ventas por día quedaba atado a MySQL. `MONTH()`
y `YEAR()`, que sí son estándar, se traducen por dialecto y no daban problema,
lo que hacía el fallo menos evidente.

**Cómo se detectó.** Al escribir las pruebas de reportes sobre H2.

**Corrección.** Se consulta por rango semiabierto `[inicio, fin)`. Además de
ser portable, aplicar una función sobre la columna impedía aprovechar un índice
sobre `fecha`; comparar por rango sí lo permite. La firma pública no cambia.

**Commit:** `bd086da`

---

## C. Experiencia de uso

### 11. `@PreAuthorize` devolvía 400 en lugar de 403

**Qué fallaba.** `AccessDeniedException` hereda de `RuntimeException`, así que
la capturaba el manejador genérico de `GlobalExceptionHandler`.

**Consecuencia.** **No era un agujero de seguridad** —el acceso quedaba
bloqueado igualmente— pero el cliente recibía «datos inválidos» cuando el
problema real era de permisos, lo que dificulta diagnosticar y da información
engañosa a quien consume la API.

**Corrección.** Manejador explícito que devuelve 403.

**Commit:** `432ca41`

---

### 12. Desplegables vacíos al editar un producto

**Qué fallaba.** `[ngValue]` compara por referencia, y los objetos anidados del
producto vienen de una petición distinta que la de los desplegables: mismo
contenido, distinta instancia.

**Consecuencia.** Al editar un producto, los desplegables de categoría y tipo
salían **en blanco aunque el producto tuviera valor**. Guardar sin volver a
elegirlos borraba la clasificación.

**Corrección.** `[compareWith]` comparando por identificador, aplicado a los
tres desplegables.

**Commit:** `2ed6577`

---

### 13. «Añadir al carrito» quedaba inutilizado tras un fallo de red

**Qué fallaba.** En la ficha de producto, el indicador `loadingCarrito` solo se
desactivaba al terminar **bien**. Ese mismo campo deshabilita el botón.

**Consecuencia.** Un fallo de red dejaba el botón **inutilizado hasta recargar
la página**: el cliente no podía reintentar la compra.

**Cómo se detectó.** Al escribir las pruebas de la ficha de producto.

**Corrección.** Se libera también al fallar, y se sale antes si ya hay una
petición en curso, para no añadir la prenda dos veces.

**Commit:** `d6b6998`

---

### 14. El checkout mostraba «[object Object]» al fallar la compra

**Qué fallaba.** El backend responde los errores como `{"message": "..."}`,
pero el componente guardaba el objeto entero en el campo de texto:

```typescript
this.error = err?.error || 'Error procesando pedido, stock insuficiente';
```

**Consecuencia.** La plantilla lo pinta con `{{ error }}`, así que el cliente
veía **`[object Object]` justo en el momento en que le fallaba la compra**, en
lugar de «Stock insuficiente» o «Saldo insuficiente en la tarjeta». Era el
único punto del frontend con ese descuido: los otros once ya leían
`err.error?.message`.

**Cómo se detectó.** Al escribir las pruebas del checkout.

**Corrección.** Se lee el campo `message`, contemplando también la cadena
suelta porque no todos los endpoints pasan por el manejador global. Se
comprobó en el navegador con el cuerpo de error real:

| | |
|---|---|
| Antes | `[object Object]` |
| Después | `Stock insuficiente: Legging (talla M)` |

**Commit:** `dbdd089`

---

### 15. La portada pública dejaba errores en la consola

**Qué fallaba.** `/principal` es pública, pero pedía los favoritos del cliente
al cargar. Sin sesión el backend responde 403, y la llamada no trataba el
error.

**Consecuencia.** Cualquiera que abriera las herramientas del navegador veía
**dos errores rojos en la primera pantalla de la tienda**.

**Corrección.** Los favoritos solo se piden si hay sesión, y además se trata el
error.

**Commit:** `42af608`

---

## D. Portabilidad y despliegue

### 16. Direcciones del servidor incrustadas en el código

**Qué fallaba.** Los servicios del frontend llevaban `http://localhost:8080`
escrito en el código, y las rutas de imagen se guardaban **completas** en la
base de datos.

**Consecuencia.** Ambas cosas atan el sistema a la máquina de desarrollo. Al
desplegar en cualquier otro sitio, **todas las fotos del catálogo apuntarían a
un servidor que no existe** y el frontend hablaría con un backend inalcanzable.

**Corrección.** Configuración por entorno (`environments/`) con reemplazo de
archivo en compilación, y almacenamiento de rutas **relativas** que el frontend
compone con la dirección de su entorno mediante un *pipe*.

**Commit:** `5ec3937`

---

### 17. La compilación de producción no pasaba

**Qué fallaba.** Dos problemas detectados al montar la integración continua:

- El `@import` de la fuente Inter estaba en los 16 CSS de componente, y Angular
  incrusta la hoja completa en cada uno.
- `favoritos.css` conservaba 17 kB de estilos de la versión anterior en
  Bootstrap; ninguno de sus 11 selectores se usaba ya.

**Consecuencia.** La compilación en modo producción **fallaba por exceder los
presupuestos**. En modo desarrollo no se notaba, así que el proyecto parecía
correcto hasta el momento de desplegarlo.

**Corrección.** La fuente se carga una sola vez desde `index.html`, con
`preconnect`. Los estilos muertos se eliminaron tras comprobar uno a uno que
ningún selector se usaba. El paquete baja de **1,38 MB a 1,20 MB** (222 kB
comprimido).

Se detectó además que `mvnw` estaba versionado sin permiso de ejecución (modo
`100644`): en Windows da igual, pero el servidor de integración es Linux y
habría fallado con *permission denied*.

**Commit:** `c6312d0`

---

## Verificación de las correcciones

Ningún arreglo se dio por bueno por el hecho de compilar. El método aplicado
fue **romper a propósito lo que cada prueba cubre y confirmar que falla**:

| Corrección | Comprobación |
|---|---|
| Escritura pública del catálogo | Revertido el arreglo: fallan 5 pruebas |
| Acceso a pedidos ajenos | Revertido el arreglo: falla la prueba de propiedad |
| Validación de entrada | Retirada de 3 puntos: fallan 9 de 17 pruebas |
| Filtros y paginación del catálogo | 2 mutaciones: fallan exactamente 2 pruebas |
| Cálculo del total del carrito | 2 mutaciones: fallan exactamente 2 pruebas |
| Desplegables y paginación del panel | 2 mutaciones: fallan exactamente 3 pruebas |
| Guardas del checkout | 2 mutaciones: fallan exactamente 2 pruebas |
| Vaciado del carrito al comprar | Retirado: la segunda confirmación devuelve 200 en lugar de 400, es decir, **el cliente pagaba dos veces** |

En todos los casos fallaron **las pruebas esperadas y ninguna más**, lo que
confirma que cubren el defecto y que no producen falsos positivos.

---

## Estado final

| | |
|---|---|
| Pruebas de backend | 160 |
| Pruebas de frontend | 125 |
| Integración continua | En verde |
| Compilación de producción | Correcta (1,20 MB) |
| Esquema de base de datos | Versionado con Flyway |
| Secretos en el repositorio | Ninguno (verificado en todo el historial) |

La suite incluye una **prueba de integración** (`CompraCompletaTest`) que
levanta la aplicación completa y recorre registro → verificación → sesión →
catálogo → carrito → pedido, comprobando después el estado de la base de datos.
Es la única capaz de detectar que las piezas no encajan entre sí: comprueba,
entre otras cosas, que un cobro fallido **revierta la transacción entera** sin
dejar stock descontado ni pedidos a medias.
