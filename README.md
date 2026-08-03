# AltaPinta — Tienda deportiva

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
| `MAIL_USERNAME` | El correo Gmail que envía las notificaciones |
| `MAIL_PASSWORD` | Una *App Password* de 16 caracteres, **no** la contraseña de la cuenta. Se genera en https://myaccount.google.com/apppasswords |
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

Las tablas se crean solas al arrancar (`spring.jpa.hibernate.ddl-auto=update`).

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

### 5. Aplicar las migraciones

Hibernate crea las tablas al arrancar, pero **nunca cambia el tipo de una
columna que ya existe ni elimina ninguna**. Por eso hay cambios que se
aplican a mano, una sola vez, en `BackendTienda/migraciones/`:

| Script | Qué hace |
|---|---|
| `001-importes-a-decimal.sql` | Pasa los importes de `DOUBLE` a `DECIMAL(12,2)`. Sin esto el dinero se guarda en coma flotante binaria y 89,90 no es exactamente 89,90 |
| `002-eliminar-cvv.sql` | Borra la columna `cvv` y su contenido. PCI DSS prohíbe almacenar el código de verificación de la tarjeta |
| `003-catalogo-deportivo.sql` | Nombres, descripciones, deportes y tallas del catálogo |
| `004-imagenes-y-precios.sql` | Ilustración y precio de cada producto |
| `005-urls-de-imagen-relativas.sql` | Convierte las URLs de imagen absolutas en rutas relativas |

Haz una copia de seguridad antes:

```powershell
cd BackendTienda
mysqldump -u root -p alta_pinta > respaldo-alta_pinta.sql
mysql -u root -p alta_pinta < migraciones\001-importes-a-decimal.sql
```

Los scripts 003 y 004 se regeneran con `generar_catalogo.py` y
`generar_imagenes.py`, que llevan semilla fija: producen siempre el mismo
resultado. Edita su vocabulario si quieres otro catálogo.

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
  migraciones/    scripts SQL y generadores del catálogo
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
