# AltaPinta — Tienda deportiva

[![Pruebas](https://github.com/emilio2025/AltaPinta/actions/workflows/pruebas.yml/badge.svg)](https://github.com/emilio2025/AltaPinta/actions/workflows/pruebas.yml)

Sistema de comercio electrónico para una tienda deportiva.
Proyecto del curso **Ingeniería de Software II**.

| Módulo | Tecnología | Carpeta |
|---|---|---|
| Backend | Spring Boot 3.3.5 · Java 17 · MySQL | `BackendTienda/` |
| Frontend | Angular 20.3 · PrimeNG 20 · Tailwind 4 | `Practica/` |

Funcionalidades: autenticación con JWT y verificación por correo, catálogo
paginado con búsqueda y filtros por categoría, deporte, tipo de prenda y
talla, control de stock por talla, carrito, checkout con pago simulado,
generación de facturas en PDF, favoritos, direcciones, panel de
administración con reportes de ventas y registro de auditoría.

### Estado de las pruebas

| Suite | Casos | Cómo se ejecuta |
|---|---|---|
| Backend | 137 | `cd BackendTienda && .\mvnw.cmd test` |
| Frontend | 107 | `cd Practica && npm run test:ci` |

En el backend cubren el flujo de compra, la autenticación, las reglas de
autorización de todos los controladores y las consultas de reportes. Las de
repositorio corren sobre H2 en memoria, así que no tocan tu base de datos.

En el frontend cubren los servicios, las guardias, el interceptor y las cuatro
pantallas con lógica propia: el catálogo (filtros combinados, búsqueda con
retardo, orden y paginación), el carrito (cálculo del total y del envío), la
ficha de producto (elección de talla y stock) y el mantenimiento de productos
del panel de administración.

---

## Requisitos

- **JDK 17** — el proyecto compila y corre con Java 17.
- **MySQL 8 o superior**, escuchando en el puerto 3306.
- **Node.js 20+** para el frontend.

> **Importante sobre el JDK.** Si en tu máquina el `java` del PATH es un JDK
> más nuevo (24, 25…), no lo uses para este proyecto: Spring Boot 3.3.5 es
> anterior a esos JDK y puede fallar de formas difíciles de diagnosticar.
> Las pruebas son las primeras en caer, porque Mockito no sabe leer las clases
> que generan esos JDK y devuelve decenas de `Failed to load ApplicationContext`
> que parecen un fallo del código sin serlo.
>
> Para que eso no se confunda con un error real, el `pom.xml` comprueba la
> versión antes de compilar y corta con un mensaje explicándolo. Si te sale,
> apunta `JAVA_HOME` al JDK 17 solo en esa terminal:
>
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
> ```
>
> El script `BackendTienda/run.ps1` ya lo hace automáticamente al arrancar,
> sin tocar la configuración del sistema.

---

## Puesta en marcha

### 1. Configurar los secretos

Las credenciales **no están en el repositorio**. Se leen de
`BackendTienda/secrets.properties`, que está en `.gitignore`.

```powershell
cd BackendTienda
Copy-Item secrets.properties.example secrets.properties
```

Abre el archivo y rellena:

| Clave | Qué poner |
|---|---|
| `DB_PASSWORD` | La contraseña de tu usuario de MySQL |
| `MAIL_USERNAME` | La cuenta de Google que envía las notificaciones. Sirve tanto una de Gmail como una institucional de Workspace; también se usa como remitente |
| `MAIL_PASSWORD` | Una *App Password* de 16 caracteres, **no** la contraseña de la cuenta. Se genera en https://myaccount.google.com/apppasswords |

> La página de *App Passwords* solo aparece si la cuenta tiene activada la
> verificación en dos pasos. Si dice que la opción no está disponible, es eso.
> Ojo también con la cuenta: si tienes varias sesiones de Google abiertas, esa
> página muestra la del selector de arriba a la derecha, y la contraseña que
> generes solo vale para **esa** cuenta. Tiene que ser la misma de
> `MAIL_USERNAME`, o el envío falla con un error de autenticación poco claro.
| `JWT_SECRET` | Una cadena larga y aleatoria (mínimo 64 caracteres) |
| `ADMIN_EMAIL` | El correo que recibirá el rol de administrador al registrarse |

Para generar un `JWT_SECRET`:

```powershell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Maximum 256 }))
```

### 2. Crear la base de datos

```sql
CREATE DATABASE alta_pinta CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Crea la base vacía y nada más: las tablas las crea Flyway al arrancar el
backend, a partir de los scripts versionados del repositorio (ver el paso 5).

### 3. Arrancar el backend

```powershell
cd BackendTienda
.\run.ps1
```

Queda en http://localhost:8080 · Documentación de la API (Swagger) en
http://localhost:8080/swagger-ui.html

### 4. Arrancar el frontend

```powershell
cd Practica
npm install
npm start
```

Queda en http://localhost:4200

### 5. Las migraciones se aplican solas

No hay que hacer nada: **Flyway las aplica al arrancar el backend**. Los
scripts viven en el repositorio, en `BackendTienda/src/main/resources/db/migration/`,
y se ejecutan en orden y una sola vez. Flyway lleva la cuenta en la tabla
`flyway_schema_history` de la propia base de datos.

Hibernate ya no toca el esquema: `ddl-auto` está en `validate`, así que solo
comprueba al arrancar que las tablas coinciden con las entidades y falla si no.
Antes estaba en `update`, que iba modificando la base por su cuenta pero nunca
cambiaba el tipo de una columna ni borraba ninguna; por eso los importes se
quedaron en `DOUBLE` meses después de pasarlos a `BigDecimal`.

| Script | Qué hace |
|---|---|
| `V1__esquema_inicial.sql` | Las 20 tablas. Es el volcado del esquema real ya corregido, generado con `mysqldump --no-data` |
| `V2__eliminar_tabla_pago.sql` | Borra la tabla `pago`, que sobraba desde que se retiró el endpoint `/pago/procesar` |

**En una base de datos que ya existe, V1 no se ejecuta.** Flyway la marca como
"ya está en la versión 1" (`spring.flyway.baseline-on-migrate`) y sigue desde
V2. Solo corre de verdad al crear una base vacía.

Para cambiar el esquema, crea un archivo nuevo numerado a continuación
(`V2__lo_que_sea.sql`) y arranca. Nunca edites uno ya aplicado: Flyway guarda
su suma de comprobación y se niega a arrancar si cambia.

> Los cinco `.sql` sueltos de `BackendTienda/migraciones/` son de antes de
> Flyway y **ya no se ejecutan**: su contenido está dentro de V1. Se conservan
> como registro; hay detalle de cada uno en `migraciones/LEEME.md`.

---

## Estructura del proyecto

```
BackendTienda/
  src/main/java/com/backend/AltaPinta/
    Config/       seguridad, JWT, manejo de errores
    controller/   endpoints REST
    model/        entidades JPA
    repository/   acceso a datos
    service/      lógica de negocio
  src/main/resources/db/migration/   migraciones de Flyway (V1, V2...)
  migraciones/    scripts SQL de antes de Flyway (histórico)
  productos-imagenes/  ilustraciones e imágenes subidas
  run.ps1         arranque con el JDK correcto

Practica/src/
  app/            componentes (una carpeta por pantalla)
    guards/       control de acceso por ruta
    interceptors/ inserción del token JWT
    pipes/        composición de URLs de imagen
    theme/        tema de PrimeNG
  services/       clientes HTTP del backend
  environments/   dirección del backend por entorno
  styles.css      tokens de marca y piezas compartidas
```

---

## Dónde vive la dirección del backend

En `Practica/src/environments/`. No está escrita en ningún servicio:

| Archivo | Cuándo se usa | `apiUrl` |
|---|---|---|
| `environment.development.ts` | `npm start` | `http://localhost:8080` |
| `environment.ts` | `npm run build` | vacío, es decir el mismo origen |

Vacío en producción significa "el mismo servidor que sirve la aplicación",
que es lo habitual detrás de un proxy inverso. Si tu backend queda en otro
dominio, ponlo ahí y no toques nada más.

> Si cambias `angular.json`, **reinicia `npm start`**: el servidor de
> desarrollo lee ese archivo al arrancar y no vuelve a leerlo.

Las imágenes se guardan en la base de datos como ruta relativa
(`/imagenes/x.svg`) y el pipe `imagen` les antepone la dirección del
entorno. Guardar la URL completa ataba los datos a una máquina concreta.

---

## Cómo se maneja la configuración

`application.properties` **sí** se versiona: solo contiene referencias
`${VARIABLE}`, ningún valor sensible. Los valores reales se resuelven en
este orden, y gana el último:

```
application.properties   →  valores por defecto, sin secretos
      ↓
secrets.properties       →  tu máquina (ignorado por git)
      ↓
variables de entorno     →  servidor de producción
```

Por eso en un despliegue real basta con definir `JWT_SECRET`, `DB_PASSWORD`,
etc. como variables de entorno: sobrescriben el archivo sin tocar el código.

`JWT_SECRET` no tiene valor por defecto a propósito. Si falta, la aplicación
no arranca, en lugar de firmar tokens con una clave conocida.

---

## Problemas frecuentes

**`Access denied for user 'root'@'localhost' (using password: NO)`**
Falta `DB_PASSWORD` en `secrets.properties`, o el valor no es correcto.

**`Could not resolve placeholder 'JWT_SECRET'`**
No existe `secrets.properties`, o le falta esa clave. Cópialo desde
`secrets.properties.example`.

**Hay varios servidores de base de datos instalados**
Comprueba cuál ocupa el 3306 antes de dar por hecho que es el que crees:

```powershell
Get-NetTCPConnection -State Listen -LocalPort 3306 | Select-Object OwningProcess
```

**La sesión se cierra sola tras cambiar `JWT_SECRET`**
Es lo esperado: al cambiar la clave de firma, los tokens anteriores dejan de
ser válidos. Vuelve a iniciar sesión.

**`Schema-validation: missing table` o `wrong column type`**
El esquema de la base no coincide con las entidades. Con `ddl-auto=validate`
esto se ve al arrancar en lugar de dar errores raros meses después. La causa
casi siempre es un cambio en una entidad sin su migración: crea el `V2__…sql`
que haga el cambio en la base.

**`Validate failed: Migration checksum mismatch for version 1`**
Alguien editó un script que Flyway ya había aplicado. No se corrigen editando:
devuelve el archivo a como estaba y haz el cambio en un `V2__` nuevo.

**`Found non-empty schema without schema history table`**
Solo puede salir si se desactiva `spring.flyway.baseline-on-migrate`. Esa
opción existe precisamente porque la base de datos ya tenía las 20 tablas
cuando se adoptó Flyway.
