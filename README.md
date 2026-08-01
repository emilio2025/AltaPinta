# AltaPinta — Tienda deportiva

Sistema de comercio electrónico para una tienda deportiva.
Proyecto del curso **Ingeniería de Software II**.

| Módulo | Tecnología | Carpeta |
|---|---|---|
| Backend | Spring Boot 3.3.5 · Java 17 · MySQL | `BackendTienda/` |
| Frontend | Angular 20.3 · Bootstrap 5 | `Practica/` |

Funcionalidades: autenticación con JWT y verificación por correo, catálogo
con tallas y control de stock, carrito, checkout con pago simulado,
generación de facturas en PDF, favoritos, direcciones y auditoría.

---

## Requisitos

- **JDK 17** — el proyecto compila y corre con Java 17.
- **MySQL 8 o superior**, escuchando en el puerto 3306.
- **Node.js 20+** para el frontend.

> **Importante sobre el JDK.** Si en tu máquina el `java` del PATH es un JDK
> más nuevo (24, 25…), no lo uses para este proyecto: Spring Boot 3.3.5 es
> anterior a esos JDK y puede fallar de formas difíciles de diagnosticar.
> El script `BackendTienda/run.ps1` selecciona el JDK 17 automáticamente,
> solo para esa ventana de terminal, sin tocar la configuración del sistema.

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
