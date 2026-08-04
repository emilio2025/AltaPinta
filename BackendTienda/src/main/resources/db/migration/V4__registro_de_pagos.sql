-- ============================================================
--  V4 - Registro del resultado de cada intento de cobro
--
--  Implementa RF020 ("el sistema debe registrar el resultado del pago,
--  aceptado o rechazado") y RNF018 ("en caso de error en el pago, el
--  proceso debe registrarse como fallido sin afectar el stock").
--
--  Sobre la tabla que se borro en V2: existia una tabla pago que era un
--  cascaron vacio -cero filas, ninguna referencia en el codigo- residuo del
--  endpoint /pago/procesar retirado por un fallo de control de acceso. No
--  implementaba RF020: no la escribia nadie. V2 la elimino por ser codigo
--  muerto y esta V4 crea la implementacion real, con una diferencia de
--  diseno importante:
--
--    pedido_id ADMITE NULOS.
--
--  Esa es la clave de RNF018. Cuando el cobro se rechaza, la transaccion
--  que habria creado el pedido se revierte entera, asi que no hay pedido al
--  que apuntar. Un pago con pedido_id nulo es, precisamente, un intento
--  fallido. La tabla anterior exigia pedido, lo que hacia imposible
--  registrar el caso que el requisito pide registrar.
--
--  El registro del rechazo se escribe en una transaccion propia
--  (PagoService.registrarRechazado, con REQUIRES_NEW) para sobrevivir a esa
--  reversion. Por eso las claves ajenas no llevan ON DELETE CASCADE: el
--  historial de intentos debe permanecer aunque el pedido se cancele.
--
--  No se guarda el numero de tarjeta, solo su identificador: el numero ya
--  vive en la tabla tarjeta y duplicarlo aqui multiplicaria la superficie
--  expuesta sin ninguna ganancia.
-- ============================================================

CREATE TABLE pago (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    pedido_id   BIGINT        NULL,
    cliente_id  BIGINT        NULL,
    tarjeta_id  BIGINT        NULL,
    monto       DECIMAL(12,2) NULL,
    estado      VARCHAR(20)   NULL,
    motivo      VARCHAR(255)  NULL,
    fecha       DATETIME(6)   NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_pago_pedido  FOREIGN KEY (pedido_id)  REFERENCES pedido (id),
    CONSTRAINT fk_pago_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Los reportes consultan por estado y por cliente, ordenando por fecha.
CREATE INDEX idx_pago_estado_fecha  ON pago (estado, fecha);
CREATE INDEX idx_pago_cliente_fecha ON pago (cliente_id, fecha);
