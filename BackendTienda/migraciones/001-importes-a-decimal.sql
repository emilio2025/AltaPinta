-- ============================================================
--  001 · Pasar los importes de DOUBLE a DECIMAL(12,2)
-- ============================================================
--
--  POR QUÉ HACE FALTA EJECUTAR ESTO A MANO
--
--  El código Java ya usa BigDecimal, pero la aplicación arranca con
--  spring.jpa.hibernate.ddl-auto=update, y ese modo solo añade tablas y
--  columnas que falten: nunca cambia el tipo de una columna que ya existe.
--  Sin este script, los importes se siguen guardando en columnas DOUBLE,
--  que son binarias y no representan exactamente valores como 0.10 o 89.90.
--  Es decir, el arreglo estaría a medias: exacto al calcular, inexacto al
--  guardar.
--
--  CÓMO EJECUTARLO
--
--    mysql -u root -p alta_pinta < migraciones/001-importes-a-decimal.sql
--
--  ANTES DE EJECUTARLO: haz una copia de seguridad.
--
--    mysqldump -u root -p alta_pinta > respaldo-alta_pinta.sql
--
--  SOBRE LA PRECISIÓN ELEGIDA
--
--  DECIMAL(12,2) admite hasta 9.999.999.999,99 con dos decimales, de sobra
--  para una tienda. MySQL redondea al convertir desde DOUBLE, así que los
--  importes que ya estuvieran con más de dos decimales por errores de coma
--  flotante quedan saneados a su valor de dos decimales.
-- ============================================================

-- Catálogo
ALTER TABLE producto        MODIFY COLUMN precio          DECIMAL(12,2);

-- Pedidos
ALTER TABLE pedido          MODIFY COLUMN total           DECIMAL(12,2);
ALTER TABLE pedido_detalle  MODIFY COLUMN precio_unitario DECIMAL(12,2);

-- Envíos
ALTER TABLE envio           MODIFY COLUMN costo           DECIMAL(12,2);

-- Dinero
ALTER TABLE tarjeta         MODIFY COLUMN saldo           DECIMAL(12,2);
ALTER TABLE cuenta_tienda   MODIFY COLUMN saldo           DECIMAL(12,2);
ALTER TABLE pago            MODIFY COLUMN monto           DECIMAL(12,2);

-- Facturas
ALTER TABLE factura         MODIFY COLUMN subtotal        DECIMAL(12,2);
ALTER TABLE factura         MODIFY COLUMN envio           DECIMAL(12,2);
ALTER TABLE factura         MODIFY COLUMN total           DECIMAL(12,2);

-- ============================================================
--  COMPROBACIÓN
--  Tras ejecutarlo, todas estas columnas deben aparecer como decimal(12,2):
--
--    SELECT table_name, column_name, column_type
--    FROM information_schema.columns
--    WHERE table_schema = 'alta_pinta'
--      AND column_name IN ('precio','total','precio_unitario','costo',
--                          'saldo','monto','subtotal','envio')
--    ORDER BY table_name, column_name;
-- ============================================================
