-- ============================================================
--  V1 - Esquema inicial
--
--  Volcado del esquema tal y como quedo tras anos de ddl-auto=update
--  mas las correcciones manuales de BackendTienda/migraciones/ (importes
--  a DECIMAL, retirada del CVV, catalogo deportivo, imagenes y rutas
--  relativas). Se genero con mysqldump --no-data sobre la base de datos
--  real, no a mano: asi es identica a la que ya estaba funcionando.
--
--  En una base de datos que YA existe este archivo no se ejecuta. Flyway
--  la marca como "ya esta en la version 1" gracias a
--  spring.flyway.baseline-on-migrate (ver application.properties).
--  Solo corre de verdad al crear una base de datos vacia.
--
--  De aqui en adelante, cualquier cambio de esquema va en un archivo
--  nuevo V2__, V3__... Este no se toca nunca mas: Flyway guarda su suma
--  de comprobacion y se niega a arrancar si cambia.
--
--  Los valores AUTO_INCREMENT del volcado se han quitado para que una
--  base nueva empiece a numerar desde 1.
-- ============================================================


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `accion` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `detalle` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `entidad` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `entidad_id` bigint DEFAULT NULL,
  `fecha` datetime(6) DEFAULT NULL,
  `usuario_correo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrito` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cliente_id` bigint NOT NULL,
  `creado_en` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_carrito_cliente` (`cliente_id`),
  CONSTRAINT `fk_carrito_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrito_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `carrito_id` bigint NOT NULL,
  `producto_id` bigint NOT NULL,
  `cantidad` int NOT NULL,
  `talla_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6nn3q14gqlvemnavid7paljkd` (`carrito_id`,`producto_id`,`talla_id`),
  KEY `fk_item_producto` (`producto_id`),
  KEY `FK2dd71fovmryndf7m33le6c12g` (`talla_id`),
  CONSTRAINT `FK2dd71fovmryndf7m33le6c12g` FOREIGN KEY (`talla_id`) REFERENCES `talla` (`id`),
  CONSTRAINT `fk_item_carrito` FOREIGN KEY (`carrito_id`) REFERENCES `carrito` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_item_producto` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dni` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `verificado` tinyint(1) NOT NULL DEFAULT '0',
  `token_verificacion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `token_expira` datetime DEFAULT NULL,
  `rol` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `token_reset_password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `token_reset_expira` datetime DEFAULT NULL,
  `razon_social` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ruc` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuenta_tienda` (
  `id` bigint NOT NULL,
  `saldo` decimal(12,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deporte` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `icono` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombre` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKp8csicfrqofd738h7ufq9lwxq` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `direccion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `direccion_completa` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `distrito` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `etiqueta` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `referencia` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmyqgr4pql6yur2vlbifggv726` (`cliente_id`),
  CONSTRAINT `FKmyqgr4pql6yur2vlbifggv726` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `envio` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `costo` decimal(12,2) DEFAULT NULL,
  `lugar` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1f0l5tpqdapkvbiw574i2kl2p` (`cliente_id`),
  CONSTRAINT `FK1f0l5tpqdapkvbiw574i2kl2p` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `envio` decimal(12,2) DEFAULT NULL,
  `fecha` datetime(6) DEFAULT NULL,
  `numero` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `subtotal` decimal(12,2) DEFAULT NULL,
  `total` decimal(12,2) DEFAULT NULL,
  `url_pdf` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `pedido_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKngujt6d9ti8crahqfrfb4p9d8` (`pedido_id`),
  CONSTRAINT `FKn6q9mbkc0n4g1uux57clh2bq0` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorito` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `producto_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario_id` (`usuario_id`,`producto_id`),
  UNIQUE KEY `UKhoe6d0jtoeehhd6fmtwrevcj9` (`usuario_id`,`producto_id`),
  KEY `fk_fav_producto` (`producto_id`),
  CONSTRAINT `fk_fav_producto` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`),
  CONSTRAINT `fk_fav_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fecha` datetime NOT NULL,
  `monto` decimal(12,2) DEFAULT NULL,
  `estado` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `tarjeta_numero` varchar(16) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  `pedido_id` bigint DEFAULT NULL,
  `tarjeta_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8fojprqy7kv7k3d192m91e027` (`pedido_id`),
  KEY `FK719gcyy2a08m13apobvuibbab` (`tarjeta_id`),
  CONSTRAINT `FK719gcyy2a08m13apobvuibbab` FOREIGN KEY (`tarjeta_id`) REFERENCES `tarjeta` (`id`),
  CONSTRAINT `FK8fojprqy7kv7k3d192m91e027` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cliente_id` bigint NOT NULL,
  `total` decimal(12,2) DEFAULT NULL,
  `estado` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `costo_envio` double DEFAULT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  `envio_id` bigint DEFAULT NULL,
  `tipo_envio` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pedido_cliente` (`cliente_id`),
  KEY `FK57ghbo31e9gnmpcyg6ne1lkc9` (`envio_id`),
  CONSTRAINT `FK57ghbo31e9gnmpcyg6ne1lkc9` FOREIGN KEY (`envio_id`) REFERENCES `envio` (`id`),
  CONSTRAINT `fk_pedido_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido_detalle` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pedido_id` bigint NOT NULL,
  `producto_id` bigint NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(12,2) DEFAULT NULL,
  `talla_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pedido_id` (`pedido_id`),
  KEY `producto_id` (`producto_id`),
  KEY `FK7ep8hg15v8cpn36vkk1avp074` (`talla_id`),
  CONSTRAINT `FK7ep8hg15v8cpn36vkk1avp074` FOREIGN KEY (`talla_id`) REFERENCES `talla` (`id`),
  CONSTRAINT `pedido_detalle_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`),
  CONSTRAINT `pedido_detalle_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `imagen_url` longtext COLLATE utf8mb4_general_ci,
  `categoria_id` bigint DEFAULT NULL,
  `tipo_prenda_id` bigint DEFAULT NULL,
  `precio` decimal(12,2) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `deporte_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `categoria_id` (`categoria_id`),
  KEY `tipo_prenda_id` (`tipo_prenda_id`),
  KEY `FKpwssm232ib2cjbm3y70vdkqub` (`deporte_id`),
  CONSTRAINT `FKpwssm232ib2cjbm3y70vdkqub` FOREIGN KEY (`deporte_id`) REFERENCES `deporte` (`id`),
  CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categoria` (`id`),
  CONSTRAINT `producto_ibfk_2` FOREIGN KEY (`tipo_prenda_id`) REFERENCES `tipo_prenda` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_imagen` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orden` int DEFAULT NULL,
  `url` longtext COLLATE utf8mb4_general_ci NOT NULL,
  `producto_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi1qiututau8b8vplg9rxog16k` (`producto_id`),
  CONSTRAINT `FKi1qiututau8b8vplg9rxog16k` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_talla` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stock` int NOT NULL,
  `version` bigint DEFAULT NULL,
  `producto_id` bigint DEFAULT NULL,
  `talla_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1yj22vn5nwkodsly7lwboho43` (`producto_id`,`talla_id`),
  KEY `FKkab4hlyg0ngrygpj95fv07ysy` (`talla_id`),
  CONSTRAINT `FKkab4hlyg0ngrygpj95fv07ysy` FOREIGN KEY (`talla_id`) REFERENCES `talla` (`id`),
  CONSTRAINT `FKn1fe5en12c58p72abo9hqktse` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `talla` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarjeta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `numero` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `titular` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_vencimiento` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `saldo` decimal(12,2) DEFAULT NULL,
  `activa` bit(1) DEFAULT NULL,
  `cliente_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero` (`numero`),
  KEY `FKrjij4g6aillsmt5k6c6b3vpmc` (`cliente_id`),
  CONSTRAINT `FKrjij4g6aillsmt5k6c6b3vpmc` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_prenda` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

