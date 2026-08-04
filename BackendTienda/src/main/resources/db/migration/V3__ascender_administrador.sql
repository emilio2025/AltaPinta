-- ============================================================
--  V3 - Ascender la cuenta del responsable del proyecto a ADMIN
--
--  Hasta ahora, la unica forma de tener un administrador era registrarse
--  con el correo configurado en ADMIN_EMAIL: AuthController comprueba esa
--  coincidencia al dar de alta al cliente y le pone ROLE_ADMIN. Eso
--  funciona para el primero, pero no permite ascender una cuenta que ya
--  existe sin tocar la base a mano.
--
--  Se asciende una cuenta existente en lugar de crear otra a proposito:
--  crear una nueva obligaria a inventar y transmitir una contrasena, y ya
--  hay demasiadas cuentas para las personas que realmente usan el sistema.
--  Esta cuenta ya esta verificada y su contrasena solo la conoce su dueno.
--
--  Es una migracion de DATOS, no de esquema. Flyway sirve igual para
--  ambas: lo que aporta es que el cambio quede registrado, con su motivo,
--  y se aplique una sola vez en cada entorno en lugar de ejecutarse a mano
--  y olvidarse de por que se hizo.
--
--  Sobre una base recien creada no hay ninguna fila que coincida y la
--  sentencia no hace nada, que es el comportamiento correcto: ahi el
--  administrador lo crea el registro con ADMIN_EMAIL.
-- ============================================================

UPDATE clientes
   SET rol = 'ADMIN'
 WHERE correo = 'emiliopaniagua549@gmail.com';
