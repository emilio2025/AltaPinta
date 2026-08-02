-- ============================================================
--  005 · Guardar las rutas de imagen relativas
-- ============================================================
--
--  POR QUÉ
--
--  Las imágenes se guardaban con la URL completa:
--
--      http://localhost:8080/imagenes/prenda-camiseta-running.svg
--
--  Eso ata los datos a la máquina de desarrollo. El día que la aplicación
--  se despliegue en cualquier otro sitio, las 153 imágenes del catálogo
--  apuntarán a un servidor que no existe y no se verá ninguna.
--
--  A partir de ahora se guarda solo la ruta:
--
--      /imagenes/prenda-camiseta-running.svg
--
--  y el frontend le antepone la dirección de su entorno (ImagenPipe).
--  ImagenService ya devuelve la ruta relativa al subir una foto nueva.
--
--  CÓMO EJECUTARLO
--
--    mysql -u root -p alta_pinta < migraciones/005-urls-de-imagen-relativas.sql
--
--  Es seguro ejecutarlo varias veces: solo toca las filas que todavía
--  tienen el prefijo.
-- ============================================================

START TRANSACTION;

-- Imagen principal del producto
UPDATE producto
SET imagen_url = SUBSTRING(imagen_url, LENGTH('http://localhost:8080') + 1)
WHERE imagen_url LIKE 'http://localhost:8080/%';

-- Galería del producto
UPDATE producto_imagen
SET url = SUBSTRING(url, LENGTH('http://localhost:8080') + 1)
WHERE url LIKE 'http://localhost:8080/%';

COMMIT;

-- ============================================================
--  COMPROBACIÓN
--  Las dos consultas deben devolver 0:
--
--    SELECT COUNT(*) FROM producto        WHERE imagen_url LIKE 'http%';
--    SELECT COUNT(*) FROM producto_imagen WHERE url        LIKE 'http%';
--
--  NOTA: el pipe del frontend sigue aceptando URLs absolutas, así que si
--  queda alguna sin convertir se seguirá viendo. La migración es para que
--  los datos dejen de depender de una máquina concreta.
-- ============================================================
