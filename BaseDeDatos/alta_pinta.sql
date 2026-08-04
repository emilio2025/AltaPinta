-- ============================================================
--  AltaPinta - Script de base de datos
--
--  Contiene el ESQUEMA COMPLETO (20 tablas) y los DATOS DEL
--  CATALOGO: productos, categorias, tipos de prenda, deportes,
--  tallas, stock por talla, imagenes y destinos de envio.
--
--  NO contiene datos personales. Las tablas de clientes,
--  direcciones, tarjetas, pedidos, facturas, favoritos, carritos,
--  pagos y auditoria se crean vacias a proposito: sus filas son
--  datos reales de personas y no deben publicarse.
--
--  Uso:  mysql -u root -p < alta_pinta.sql
--
--  Alternativa recomendada: crear la base vacia y arrancar el
--  backend. Flyway aplica las migraciones V1..V5 por su cuenta
--  y deja el esquema identico. Este script existe para poder
--  inspeccionar la estructura sin levantar la aplicacion.
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

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `alta_pinta` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `alta_pinta`;
DROP TABLE IF EXISTS `auditoria`;
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
) ENGINE=InnoDB AUTO_INCREMENT=216 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `carrito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrito` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cliente_id` bigint NOT NULL,
  `creado_en` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_carrito_cliente` (`cliente_id`),
  CONSTRAINT `fk_carrito_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `carrito_item`;
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
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `clientes`;
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
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cuenta_tienda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuenta_tienda` (
  `id` bigint NOT NULL,
  `saldo` decimal(12,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `deporte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deporte` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `icono` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombre` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKp8csicfrqofd738h7ufq9lwxq` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `direccion`;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `envio`;
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `factura`;
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `favorito`;
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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pedido_id` bigint DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  `tarjeta_id` bigint DEFAULT NULL,
  `monto` decimal(12,2) DEFAULT NULL,
  `estado` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `motivo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pago_pedido` (`pedido_id`),
  KEY `idx_pago_estado_fecha` (`estado`,`fecha`),
  KEY `idx_pago_cliente_fecha` (`cliente_id`,`fecha`),
  CONSTRAINT `fk_pago_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`),
  CONSTRAINT `fk_pago_pedido` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `pedido`;
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `pedido_detalle`;
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
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `producto`;
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
) ENGINE=InnoDB AUTO_INCREMENT=166 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `producto_imagen`;
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `producto_talla`;
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
) ENGINE=InnoDB AUTO_INCREMENT=1945 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `talla`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `talla` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tarjeta`;
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipo_prenda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_prenda` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


-- ---------- Datos del catalogo ----------

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

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (3,'Bebé'),(1,'Mujer'),(4,'Niños'),(2,'Varon');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `cuenta_tienda` WRITE;
/*!40000 ALTER TABLE `cuenta_tienda` DISABLE KEYS */;
INSERT INTO `cuenta_tienda` VALUES (1,1693.00);
/*!40000 ALTER TABLE `cuenta_tienda` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `deporte` WRITE;
/*!40000 ALTER TABLE `deporte` DISABLE KEYS */;
INSERT INTO `deporte` VALUES (1,'pi-forward','Running'),(2,'pi-bolt','Training'),(3,'pi-circle','Fútbol'),(4,'pi-star','Básquet'),(5,'pi-heart','Yoga'),(6,'pi-compass','Outdoor');
/*!40000 ALTER TABLE `deporte` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `envio` WRITE;
/*!40000 ALTER TABLE `envio` DISABLE KEYS */;
INSERT INTO `envio` VALUES (1,17.50,'Amazonas',NULL),(2,17.50,'Áncash',NULL),(3,24.00,'Arequipa',NULL),(4,17.50,'Ayacucho',NULL),(5,24.00,'Cajamarca',NULL),(6,12.00,'Callao',NULL),(7,21.00,'Cusco',NULL),(8,24.00,'Huancavelica',NULL),(9,17.50,'Huánuco',NULL),(10,17.50,'Ica',NULL),(11,17.50,'Junín',NULL),(12,12.00,'La Libertad',NULL),(13,17.50,'Lambayeque',NULL),(14,12.00,'Lima',NULL),(15,30.00,'Loreto',NULL),(16,21.00,'Madre de Dios',NULL),(17,30.00,'Moquegua',NULL),(18,17.50,'Pasco',NULL),(19,24.00,'Piura',NULL),(20,24.00,'Puno',NULL),(21,17.50,'San Martín',NULL),(22,21.00,'Tacna',NULL),(23,24.00,'Tumbes',NULL),(24,17.50,'Ucayali',NULL);
/*!40000 ALTER TABLE `envio` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'Legging de tiro alto training gris','Legging de mujer para el gimnasio. Confeccionado en malla transpirable en zonas de calor, con largo por encima de la rodilla. Bolsillo trasero para el móvil.','/imagenes/foto-pantalon-01.jpg',1,4,109.90,4,2),(5,'Pantalón jogger de tiro alto training verde militar','Pantalón jogger de niño para el gimnasio. Confeccionado en punto elástico de compresión ligera, con cintura elástica que no aprieta. Bolsillo trasero para el móvil.','/imagenes/prenda-pantalon-training.svg',4,4,139.90,3,2),(6,'Legging largo training azul marino','Legging de niño para el gimnasio. Confeccionado en poliéster ligero con tratamiento antiolor, con corte ajustado que no estorba en la zancada. Bolsillos laterales con cierre.','/imagenes/prenda-pantalon-training.svg',4,4,149.90,3,2),(7,'Cortavientos con capucha running blanco','Cortavientos de mujer para salir a correr. Confeccionado en poliéster ligero con tratamiento antiolor, con corte holgado para entrenar sin agobios. Costuras planas para evitar rozaduras.','/imagenes/foto-casaca-01.jpg',1,6,259.90,2,1),(8,'Casaca deportiva con cremallera outdoor blanca','Casaca deportiva de mujer para la montaña y el día a día. Confeccionada en algodón peinado con toque seco, con hombros libres para no limitar el gesto. Bajo reforzado que aguanta lavados.','/imagenes/foto-casaca-02.jpg',1,6,179.90,2,6),(9,'Casaca deportiva running granate','Casaca deportiva de mujer para salir a correr. Confeccionada en tejido técnico de secado rápido, con sisas amplias que dejan respirar. Etiqueta impresa, sin costura en el cuello.','/imagenes/foto-casaca-03.jpg',1,6,189.90,2,1),(10,'Sudadera térmica de cuello alto training gris','Sudadera térmica de mujer para entrenar a diario. Confeccionada en tejido técnico de secado rápido, con hombros libres para no limitar el gesto. Bajo reforzado que aguanta lavados.','/imagenes/foto-chompa-01.jpg',1,7,169.90,2,2),(11,'Chaqueta de media cremallera térmica outdoor gris','Chaqueta de media cremallera de mujer para salir al aire libre. Confeccionada en algodón peinado con toque seco, con sisas amplias que dejan respirar. Etiqueta impresa, sin costura en el cuello.','/imagenes/foto-chompa-02.jpg',1,7,179.90,2,6),(12,'Sudadera térmica de cuello alto training granate','Sudadera térmica de mujer para entrenar a diario. Confeccionada en tejido técnico de secado rápido, con sisas amplias que dejan respirar. Costuras planas para evitar rozaduras.','/imagenes/foto-chompa-03.jpg',1,7,179.90,2,2),(13,'Falda deportiva con short interior training negra','Falda deportiva de mujer para entrenar a diario. Confeccionada en poliéster ligero con tratamiento antiolor, con cintura elástica que no aprieta. Cintura elástica con cordón interior.','/imagenes/foto-falda-01.jpg',1,8,99.90,2,2),(14,'Falda deportiva plisada training gris','Falda deportiva de mujer para tus sesiones de fuerza. Confeccionada en punto elástico de compresión ligera, con corte ajustado que no estorba en la zancada. Bolsillos laterales con cierre.','/imagenes/foto-falda-02.jpg',1,8,99.90,2,2),(15,'Falda-short con malla interior training blanca','Falda-short de mujer para entrenar a diario. Confeccionada en punto elástico de compresión ligera, con corte ajustado que no estorba en la zancada. Costuras planas para evitar rozaduras.','/imagenes/foto-falda-03.jpg',1,8,109.90,2,2),(17,'Polerón técnico con capucha running gris','Polerón técnico de mujer para salir a correr. Confeccionado en tejido reciclado con acabado suave, con hombros libres para no limitar el gesto. Etiqueta impresa, sin costura en el cuello.','/imagenes/foto-polera-01.jpg',1,1,139.90,2,1),(18,'Sudadera con capucha de algodón training granate','Sudadera con capucha de mujer para el gimnasio. Confeccionada en poliéster ligero con tratamiento antiolor, con corte ajustado que sigue el movimiento. Detalle reflectante para sesiones de tarde.','/imagenes/foto-polera-02.jpg',1,1,149.90,2,2),(19,'Sudadera training granate','Sudadera de mujer para el gimnasio. Confeccionada en tejido reciclado con acabado suave, con sisas amplias que dejan respirar. Detalle reflectante para sesiones de tarde.','/imagenes/foto-polera-03.jpg',1,1,159.90,2,2),(20,'Camiseta de manga larga training verde militar','Camiseta de mujer para el gimnasio. Confeccionada en malla transpirable en zonas de calor, con corte holgado para entrenar sin agobios. Bajo reforzado que aguanta lavados.','/imagenes/foto-polo-01.jpg',1,2,69.90,2,2),(21,'Camiseta de manga corta de cuello redondo training gris','Camiseta de manga corta de mujer para tus sesiones de fuerza. Confeccionada en punto elástico de compresión ligera, con corte regular, ni pegado ni suelto. Costuras planas para evitar rozaduras.','/imagenes/foto-polo-02.jpg',1,2,79.90,2,2),(22,'Camiseta de manga corta training negra','Camiseta de manga corta de mujer para entrenar a diario. Confeccionada en punto elástico de compresión ligera, con hombros libres para no limitar el gesto. Detalle reflectante para sesiones de tarde.','/imagenes/foto-polo-03.jpg',1,2,79.90,2,2),(23,'Camiseta de manga corta de cuello redondo outdoor negra','Camiseta de manga corta de mujer para salir al aire libre. Confeccionada en poliéster ligero con tratamiento antiolor, con corte ajustado que sigue el movimiento. Etiqueta impresa, sin costura en el cuello.','/imagenes/foto-polo-04.jpg',1,2,89.90,2,6),(24,'Short de tiro medio running azul marino','Short de mujer para salir a correr. Confeccionado en malla transpirable en zonas de calor, con corte holgado con caída natural. Cintura elástica con cordón interior.','/imagenes/foto-short-01.jpg',1,3,59.90,2,1),(25,'Short deportivo training azul marino','Short deportivo de mujer para tus sesiones de fuerza. Confeccionado en poliéster ligero con tratamiento antiolor, con corte ajustado que no estorba en la zancada. Costuras planas para evitar rozaduras.','/imagenes/foto-short-02.jpg',1,3,69.90,2,2),(26,'Short de entrenamiento running azul marino','Short de entrenamiento de mujer para tus kilómetros diarios. Confeccionado en malla transpirable en zonas de calor, con corte ajustado que no estorba en la zancada. Cintura elástica con cordón interior.','/imagenes/foto-short-03.jpg',1,3,69.90,2,1),(27,'Short de entrenamiento de secado rápido básquet azul marino','Short de entrenamiento de mujer para la pista. Confeccionado en punto elástico de compresión ligera, con cintura alta que se mantiene en su sitio. Costuras planas para evitar rozaduras.','/imagenes/foto-short-04.webp',1,3,79.90,2,4),(28,'Vestido de entrenamiento sin mangas training gris','Vestido de entrenamiento de mujer para entrenar a diario. Confeccionado en tejido reciclado con acabado suave, con sisas amplias que dejan respirar. Costuras planas para evitar rozaduras.','/imagenes/foto-vestido-01.jpg',1,5,119.90,2,2),(29,'Vestido de entrenamiento yoga verde militar','Vestido de entrenamiento de mujer para estirar y respirar. Confeccionado en punto elástico de compresión ligera, con sisas amplias que dejan respirar. Costuras planas para evitar rozaduras.','/imagenes/foto-vestido-02.jpg',1,5,129.90,2,5),(30,'Vestido deportivo con short interior yoga verde militar','Vestido deportivo de mujer para yoga y movilidad. Confeccionado en tejido reciclado con acabado suave, con sisas amplias que dejan respirar. Etiqueta impresa, sin costura en el cuello.','/imagenes/foto-vestido-03.jpg',1,5,129.90,2,5),(31,'Vestido deportivo con short interior yoga negro','Vestido deportivo de mujer para estirar y respirar. Confeccionado en malla transpirable en zonas de calor, con hombros libres para no limitar el gesto. Detalle reflectante para sesiones de tarde.','/imagenes/foto-vestido-04.jpg',1,5,139.90,2,5),(32,'Vestido deportivo yoga azul marino','Vestido deportivo de mujer para estirar y respirar. Confeccionado en punto elástico de compresión ligera, con sisas amplias que dejan respirar. Bajo reforzado que aguanta lavados.','/imagenes/foto-vestido-05.jpg',1,5,89.90,2,5),(33,'Vestido de entrenamiento sin mangas yoga negro','Vestido de entrenamiento de mujer para yoga y movilidad. Confeccionado en algodón peinado con toque seco, con corte ajustado que sigue el movimiento. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-vestido-yoga.svg',1,5,99.90,2,5),(34,'Pantalón jogger outdoor gris','Pantalón jogger de hombre para salir al aire libre. Confeccionado en punto elástico de compresión ligera, con corte ajustado que no estorba en la zancada. Bolsillos laterales con cierre.','/imagenes/prenda-pantalon-outdoor.svg',2,4,119.90,3,6),(38,'Camiseta técnica de cuello redondo training gris','Camiseta técnica de hombre para tus sesiones de fuerza. Confeccionada en tejido técnico de secado rápido, con hombros libres para no limitar el gesto. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-camiseta-training.svg',2,2,79.90,2,2),(39,'Camiseta de manga corta training gris','Camiseta de hombre para entrenar a diario. Confeccionada en algodón peinado con toque seco, con corte regular, ni pegado ni suelto. Bajo reforzado que aguanta lavados.','/imagenes/prenda-camiseta-training.svg',2,2,89.90,2,2),(41,'Camiseta técnica training blanca','Camiseta técnica de hombre para entrenar a diario. Confeccionada en punto elástico de compresión ligera, con corte regular, ni pegado ni suelto. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-camiseta-training.svg',2,2,59.90,2,2),(42,'Camiseta técnica fútbol blanca','Camiseta técnica de hombre para el partido y el entrenamiento. Confeccionada en malla transpirable en zonas de calor, con sisas amplias que dejan respirar. Costuras planas para evitar rozaduras.','/imagenes/prenda-camiseta-futbol.svg',2,2,59.90,2,3),(44,'Camiseta de manga corta training verde militar','Camiseta de manga corta de hombre para el gimnasio. Confeccionada en punto elástico de compresión ligera, con corte regular, ni pegado ni suelto. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-camiseta-training.svg',2,2,69.90,2,2),(45,'Casaca deportiva outdoor gris','Casaca deportiva de hombre para salir al aire libre. Confeccionada en algodón peinado con toque seco, con corte regular, ni pegado ni suelto. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-chaqueta-outdoor.svg',2,6,239.90,2,6),(46,'Casaca deportiva con cremallera outdoor verde militar','Casaca deportiva de hombre para salir al aire libre. Confeccionada en algodón peinado con toque seco, con corte ajustado que sigue el movimiento. Bajo reforzado que aguanta lavados.','/imagenes/prenda-chaqueta-outdoor.svg',2,6,249.90,2,6),(47,'Cortavientos con capucha outdoor gris','Cortavientos de hombre para la montaña y el día a día. Confeccionado en tejido técnico de secado rápido, con corte holgado para entrenar sin agobios. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-chaqueta-outdoor.svg',2,6,259.90,2,6),(48,'Casaca deportiva running verde militar','Casaca deportiva de hombre para tus kilómetros diarios. Confeccionada en tejido técnico de secado rápido, con sisas amplias que dejan respirar. Costuras planas para evitar rozaduras.','/imagenes/prenda-chaqueta-running.svg',2,6,179.90,2,1),(49,'Chaqueta técnica con capucha outdoor blanca','Chaqueta técnica de hombre para la montaña y el día a día. Confeccionada en tejido reciclado con acabado suave, con hombros libres para no limitar el gesto. Bajo reforzado que aguanta lavados.','/imagenes/prenda-chaqueta-outdoor.svg',2,6,189.90,2,6),(50,'Sudadera térmica outdoor blanca','Sudadera térmica de hombre para la montaña y el día a día. Confeccionada en punto elástico de compresión ligera, con corte regular, ni pegado ni suelto. Costuras planas para evitar rozaduras.','/imagenes/prenda-media-outdoor.svg',2,7,169.90,2,6),(51,'Chaqueta de media cremallera outdoor gris','Chaqueta de media cremallera de hombre para salir al aire libre. Confeccionada en malla transpirable en zonas de calor, con sisas amplias que dejan respirar. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-media-outdoor.svg',2,7,179.90,2,6),(52,'Chaqueta de media cremallera training granate','Chaqueta de media cremallera de hombre para entrenar a diario. Confeccionada en tejido reciclado con acabado suave, con hombros libres para no limitar el gesto. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-media-training.svg',2,7,179.90,2,2),(53,'Sudadera térmica outdoor granate','Sudadera térmica de hombre para salir al aire libre. Confeccionada en tejido reciclado con acabado suave, con corte holgado para entrenar sin agobios. Bajo reforzado que aguanta lavados.','/imagenes/prenda-media-outdoor.svg',2,7,189.90,2,6),(54,'Chaqueta de media cremallera térmica outdoor azul marino','Chaqueta de media cremallera de hombre para la montaña y el día a día. Confeccionada en punto elástico de compresión ligera, con corte ajustado que sigue el movimiento. Costuras planas para evitar rozaduras.','/imagenes/prenda-media-outdoor.svg',2,7,199.90,2,6),(55,'Chaqueta de media cremallera térmica training verde militar','Chaqueta de media cremallera de hombre para entrenar a diario. Confeccionada en poliéster ligero con tratamiento antiolor, con hombros libres para no limitar el gesto. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-media-training.svg',2,7,209.90,2,2),(56,'Polerón técnico running gris','Polerón técnico de hombre para tus kilómetros diarios. Confeccionado en tejido reciclado con acabado suave, con corte holgado para entrenar sin agobios. Costuras planas para evitar rozaduras.','/imagenes/prenda-sudadera-running.svg',2,1,129.90,2,1),(57,'Sudadera con cremallera outdoor gris','Sudadera de hombre para salir al aire libre. Confeccionada en malla transpirable en zonas de calor, con corte regular, ni pegado ni suelto. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-sudadera-outdoor.svg',2,1,139.90,2,6),(58,'Sudadera con capucha outdoor blanca','Sudadera de hombre para salir al aire libre. Confeccionada en malla transpirable en zonas de calor, con corte holgado para entrenar sin agobios. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-sudadera-outdoor.svg',2,1,149.90,2,6),(59,'Sudadera con capucha de algodón outdoor granate','Sudadera con capucha de hombre para salir al aire libre. Confeccionada en algodón peinado con toque seco, con sisas amplias que dejan respirar. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-sudadera-outdoor.svg',2,1,159.90,2,6),(60,'Polerón técnico running negro','Polerón técnico de hombre para rodajes largos. Confeccionado en algodón peinado con toque seco, con corte holgado para entrenar sin agobios. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-sudadera-running.svg',2,1,159.90,2,1),(61,'Short de entrenamiento de secado rápido running azul marino','Short de entrenamiento de hombre para salir a correr. Confeccionado en punto elástico de compresión ligera, con corte ajustado que no estorba en la zancada. Bolsillo trasero para el móvil.','/imagenes/prenda-short-running.svg',2,3,89.90,2,1),(62,'Short de entrenamiento de secado rápido fútbol blanco','Short de entrenamiento de hombre para pisar el campo. Confeccionado en punto elástico de compresión ligera, con corte holgado con caída natural. Cintura elástica con cordón interior.','/imagenes/prenda-short-futbol.svg',2,3,89.90,2,3),(63,'Short de entrenamiento de secado rápido básquet gris','Short de entrenamiento de hombre para la pista. Confeccionado en poliéster ligero con tratamiento antiolor, con corte holgado con caída natural. Cintura elástica con cordón interior.','/imagenes/prenda-short-basquet.svg',2,3,99.90,2,4),(64,'Short deportivo con bolsillos running verde militar','Short deportivo de hombre para tus kilómetros diarios. Confeccionado en algodón peinado con toque seco, con corte ajustado que no estorba en la zancada. Costuras planas para evitar rozaduras.','/imagenes/prenda-short-running.svg',2,3,59.90,2,1),(65,'Short deportivo con bolsillos running gris','Short deportivo de hombre para salir a correr. Confeccionado en punto elástico de compresión ligera, con cintura alta que se mantiene en su sitio. Bolsillos laterales con cierre.','/imagenes/prenda-short-running.svg',2,3,69.90,2,1),(66,'Chaqueta técnica outdoor verde militar','Chaqueta técnica de niño para la montaña y el día a día. Confeccionada en algodón peinado con toque seco, con sisas amplias que dejan respirar. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-chaqueta-outdoor.svg',4,6,199.90,2,6),(67,'Cortavientos con capucha outdoor azul marino','Cortavientos de niño para salir al aire libre. Confeccionado en poliéster ligero con tratamiento antiolor, con sisas amplias que dejan respirar. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-chaqueta-outdoor.svg',4,6,209.90,2,6),(68,'Casaca deportiva con cremallera running blanca','Casaca deportiva de niño para salir a correr. Confeccionada en tejido reciclado con acabado suave, con sisas amplias que dejan respirar. Bajo reforzado que aguanta lavados.','/imagenes/prenda-chaqueta-running.svg',4,6,229.90,2,1),(69,'Cortavientos impermeable running negro','Cortavientos de niño para rodajes largos. Confeccionado en algodón peinado con toque seco, con sisas amplias que dejan respirar. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-chaqueta-running.svg',4,6,239.90,2,1),(70,'Chaqueta técnica impermeable running verde militar','Chaqueta técnica de niño para rodajes largos. Confeccionada en tejido reciclado con acabado suave, con corte ajustado que sigue el movimiento. Costuras planas para evitar rozaduras.','/imagenes/prenda-chaqueta-running.svg',4,6,249.90,2,1),(71,'Chaqueta técnica con capucha running blanca','Chaqueta técnica de niño para rodajes largos. Confeccionada en tejido técnico de secado rápido, con corte ajustado que sigue el movimiento. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-chaqueta-running.svg',4,6,259.90,2,1),(72,'Casaca deportiva running azul marino','Casaca deportiva de niño para rodajes largos. Confeccionada en tejido técnico de secado rápido, con sisas amplias que dejan respirar. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-chaqueta-running.svg',4,6,179.90,2,1),(73,'Cortavientos impermeable outdoor gris','Cortavientos de niño para salir al aire libre. Confeccionado en algodón peinado con toque seco, con corte regular, ni pegado ni suelto. Bajo reforzado que aguanta lavados.','/imagenes/prenda-chaqueta-outdoor.svg',4,6,189.90,2,6),(74,'Chaqueta técnica con capucha running verde militar','Chaqueta técnica de niño para salir a correr. Confeccionada en poliéster ligero con tratamiento antiolor, con corte regular, ni pegado ni suelto. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-chaqueta-running.svg',4,6,199.90,2,1),(75,'Chaqueta de media cremallera training gris','Chaqueta de media cremallera de niño para entrenar a diario. Confeccionada en algodón peinado con toque seco, con hombros libres para no limitar el gesto. Bajo reforzado que aguanta lavados.','/imagenes/prenda-media-training.svg',4,7,179.90,2,2),(76,'Chaqueta de media cremallera training negra','Chaqueta de media cremallera de niño para entrenar a diario. Confeccionada en tejido técnico de secado rápido, con hombros libres para no limitar el gesto. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-media-training.svg',4,7,179.90,2,2),(77,'Sudadera térmica training gris','Sudadera térmica de niño para tus sesiones de fuerza. Confeccionada en algodón peinado con toque seco, con corte regular, ni pegado ni suelto. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-media-training.svg',4,7,189.90,2,2),(78,'Chaqueta de media cremallera térmica training azul marino','Chaqueta de media cremallera de niño para entrenar a diario. Confeccionada en poliéster ligero con tratamiento antiolor, con corte regular, ni pegado ni suelto. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-media-training.svg',4,7,199.90,2,2),(79,'Sudadera térmica outdoor azul marino','Sudadera térmica de niño para la montaña y el día a día. Confeccionada en algodón peinado con toque seco, con corte holgado para entrenar sin agobios. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-media-outdoor.svg',4,7,209.90,2,6),(80,'Chaqueta de media cremallera térmica outdoor blanca','Chaqueta de media cremallera de niño para salir al aire libre. Confeccionada en punto elástico de compresión ligera, con corte ajustado que sigue el movimiento. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-media-outdoor.svg',4,7,149.90,2,6),(81,'Chaqueta de media cremallera térmica training granate','Chaqueta de media cremallera de niño para el gimnasio. Confeccionada en punto elástico de compresión ligera, con corte regular, ni pegado ni suelto. Costuras planas para evitar rozaduras.','/imagenes/prenda-media-training.svg',4,7,159.90,2,2),(82,'Chaqueta de media cremallera térmica training negra','Chaqueta de media cremallera de niño para el gimnasio. Confeccionada en malla transpirable en zonas de calor, con corte holgado para entrenar sin agobios. Costuras planas para evitar rozaduras.','/imagenes/prenda-media-training.svg',4,7,169.90,2,2),(83,'Falda deportiva training granate','Falda deportiva de niño para entrenar a diario. Confeccionada en tejido técnico de secado rápido, con corte ajustado que no estorba en la zancada. Costuras planas para evitar rozaduras.','/imagenes/prenda-falda-training.svg',4,8,89.90,2,2),(84,'Falda deportiva yoga gris','Falda deportiva de niño para estirar y respirar. Confeccionada en tejido reciclado con acabado suave, con corte holgado con caída natural. Bolsillo trasero para el móvil.','/imagenes/prenda-falda-yoga.svg',4,8,89.90,2,5),(85,'Falda-short con malla interior yoga blanca','Falda-short de niño para pilates y yoga. Confeccionada en tejido reciclado con acabado suave, con cintura elástica que no aprieta. Costuras planas para evitar rozaduras.','/imagenes/prenda-falda-yoga.svg',4,8,99.90,2,5),(86,'Falda-short yoga verde militar','Falda-short de niño para yoga y movilidad. Confeccionada en algodón peinado con toque seco, con largo por encima de la rodilla. Bolsillos laterales con cierre.','/imagenes/prenda-falda-yoga.svg',4,8,99.90,2,5),(87,'Falda-short yoga blanca','Falda-short de niño para pilates y yoga. Confeccionada en tejido técnico de secado rápido, con cintura alta que se mantiene en su sitio. Cintura elástica con cordón interior.','/imagenes/prenda-falda-yoga.svg',4,8,109.90,2,5),(88,'Falda-short con malla interior training granate','Falda-short de niño para entrenar a diario. Confeccionada en punto elástico de compresión ligera, con corte ajustado que no estorba en la zancada. Bolsillo trasero para el móvil.','/imagenes/prenda-falda-training.svg',4,8,69.90,2,2),(89,'Falda-short con malla interior training verde militar','Falda-short de niño para el gimnasio. Confeccionada en punto elástico de compresión ligera, con corte holgado con caída natural. Cintura elástica con cordón interior.','/imagenes/prenda-falda-training.svg',4,8,79.90,2,2),(90,'Falda-short training verde militar','Falda-short de niño para tus sesiones de fuerza. Confeccionada en poliéster ligero con tratamiento antiolor, con corte holgado con caída natural. Bolsillo trasero para el móvil.','/imagenes/prenda-falda-training.svg',4,8,79.90,2,2),(91,'Falda deportiva con short interior training blanca','Falda deportiva de niño para entrenar a diario. Confeccionada en algodón peinado con toque seco, con cintura alta que se mantiene en su sitio. Bolsillo trasero para el móvil.','/imagenes/prenda-falda-training.svg',4,8,89.90,2,2),(92,'Pantalón de entrenamiento holgado outdoor blanco','Pantalón de entrenamiento de niño para salir al aire libre. Confeccionado en poliéster ligero con tratamiento antiolor, con corte ajustado que no estorba en la zancada. Costuras planas para evitar rozaduras.','/imagenes/prenda-pantalon-outdoor.svg',4,4,129.90,2,6),(93,'Pantalón de entrenamiento holgado training gris','Pantalón de entrenamiento de niño para entrenar a diario. Confeccionado en tejido técnico de secado rápido, con cintura alta que se mantiene en su sitio. Bolsillos laterales con cierre.','/imagenes/prenda-pantalon-training.svg',4,4,139.90,2,2),(94,'Legging largo training gris','Legging de niño para tus sesiones de fuerza. Confeccionado en tejido reciclado con acabado suave, con cintura alta que se mantiene en su sitio. Bolsillos laterales con cierre.','/imagenes/prenda-pantalon-training.svg',4,4,149.90,2,2),(95,'Legging de tiro alto outdoor blanco','Legging de niño para salir al aire libre. Confeccionado en malla transpirable en zonas de calor, con cintura alta que se mantiene en su sitio. Costuras planas para evitar rozaduras.','/imagenes/prenda-pantalon-outdoor.svg',4,4,159.90,2,6),(96,'Pantalón jogger con puños training negro','Pantalón jogger de niño para el gimnasio. Confeccionado en punto elástico de compresión ligera, con cintura alta que se mantiene en su sitio. Costuras planas para evitar rozaduras.','/imagenes/prenda-pantalon-training.svg',4,4,99.90,2,2),(97,'Legging de compresión training granate','Legging de niño para entrenar a diario. Confeccionado en malla transpirable en zonas de calor, con largo por encima de la rodilla. Costuras planas para evitar rozaduras.','/imagenes/prenda-pantalon-training.svg',4,4,109.90,2,2),(98,'Pantalón jogger con puños outdoor blanco','Pantalón jogger de niño para salir al aire libre. Confeccionado en punto elástico de compresión ligera, con cintura alta que se mantiene en su sitio. Cintura elástica con cordón interior.','/imagenes/prenda-pantalon-outdoor.svg',4,4,119.90,2,6),(99,'Legging de tiro alto training granate','Legging de niño para entrenar a diario. Confeccionado en tejido reciclado con acabado suave, con cintura alta que se mantiene en su sitio. Costuras planas para evitar rozaduras.','/imagenes/prenda-pantalon-training.svg',4,4,129.90,2,2),(100,'Legging de tiro alto yoga azul marino','Legging de niño para estirar y respirar. Confeccionado en tejido técnico de secado rápido, con corte holgado con caída natural. Bolsillo trasero para el móvil.','/imagenes/prenda-pantalon-yoga.svg',4,4,129.90,2,5),(101,'Legging de compresión yoga verde militar','Legging de niño para estirar y respirar. Confeccionado en punto elástico de compresión ligera, con cintura elástica que no aprieta. Cintura elástica con cordón interior.','/imagenes/prenda-pantalon-yoga.svg',4,4,139.90,2,5),(102,'Pantalón de entrenamiento holgado yoga gris','Pantalón de entrenamiento de niño para pilates y yoga. Confeccionado en algodón peinado con toque seco, con cintura elástica que no aprieta. Costuras planas para evitar rozaduras.','/imagenes/prenda-pantalon-yoga.svg',4,4,149.90,2,5),(103,'Pantalón de entrenamiento yoga blanco','Pantalón de entrenamiento de niño para pilates y yoga. Confeccionado en tejido técnico de secado rápido, con corte holgado con caída natural. Bolsillo trasero para el móvil.','/imagenes/prenda-pantalon-yoga.svg',4,4,159.90,2,5),(104,'Polerón técnico running verde militar','Polerón técnico de niño para tus kilómetros diarios. Confeccionado en malla transpirable en zonas de calor, con sisas amplias que dejan respirar. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-sudadera-running.svg',4,1,129.90,2,1),(105,'Polerón técnico training negro','Polerón técnico de niño para entrenar a diario. Confeccionado en tejido técnico de secado rápido, con corte regular, ni pegado ni suelto. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-sudadera-training.svg',4,1,139.90,2,2),(106,'Sudadera con capucha running verde militar','Sudadera con capucha de niño para rodajes largos. Confeccionada en poliéster ligero con tratamiento antiolor, con hombros libres para no limitar el gesto. Bajo reforzado que aguanta lavados.','/imagenes/prenda-sudadera-running.svg',4,1,149.90,2,1),(107,'Polerón técnico con capucha outdoor gris','Polerón técnico de niño para salir al aire libre. Confeccionado en algodón peinado con toque seco, con corte holgado para entrenar sin agobios. Costuras planas para evitar rozaduras.','/imagenes/prenda-sudadera-outdoor.svg',4,1,159.90,2,6),(108,'Sudadera con capucha training blanca','Sudadera con capucha de niño para el gimnasio. Confeccionada en poliéster ligero con tratamiento antiolor, con hombros libres para no limitar el gesto. Bajo reforzado que aguanta lavados.','/imagenes/prenda-sudadera-training.svg',4,1,159.90,2,2),(109,'Polerón técnico outdoor granate','Polerón técnico de niño para la montaña y el día a día. Confeccionado en tejido reciclado con acabado suave, con hombros libres para no limitar el gesto. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-sudadera-outdoor.svg',4,1,169.90,2,6),(110,'Polerón técnico running azul marino','Polerón técnico de niño para salir a correr. Confeccionado en malla transpirable en zonas de calor, con corte regular, ni pegado ni suelto. Bajo reforzado que aguanta lavados.','/imagenes/prenda-sudadera-running.svg',4,1,179.90,2,1),(111,'Sudadera con cremallera outdoor verde militar','Sudadera de niño para la montaña y el día a día. Confeccionada en punto elástico de compresión ligera, con corte ajustado que sigue el movimiento. Costuras planas para evitar rozaduras.','/imagenes/prenda-sudadera-outdoor.svg',4,1,189.90,2,6),(112,'Sudadera con capucha de algodón training gris','Sudadera con capucha de niño para tus sesiones de fuerza. Confeccionada en punto elástico de compresión ligera, con corte regular, ni pegado ni suelto. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-sudadera-training.svg',4,1,129.90,2,2),(113,'Polerón técnico training azul marino','Polerón técnico de niño para entrenar a diario. Confeccionado en punto elástico de compresión ligera, con sisas amplias que dejan respirar. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-sudadera-training.svg',4,1,139.90,2,2),(114,'Camiseta técnica de cuello redondo training verde militar','Camiseta técnica de niño para entrenar a diario. Confeccionada en tejido técnico de secado rápido, con corte holgado para entrenar sin agobios. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-camiseta-training.svg',4,2,59.90,2,2),(115,'Camiseta técnica sin mangas outdoor granate','Camiseta técnica de niño para salir al aire libre. Confeccionada en tejido técnico de secado rápido, con sisas amplias que dejan respirar. Costuras planas para evitar rozaduras.','/imagenes/prenda-camiseta-outdoor.svg',4,2,69.90,2,6),(116,'Camiseta de manga corta outdoor gris','Camiseta de manga corta de niño para salir al aire libre. Confeccionada en algodón peinado con toque seco, con corte holgado para entrenar sin agobios. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-camiseta-outdoor.svg',4,2,69.90,2,6),(117,'Camiseta sin mangas outdoor granate','Camiseta de niño para salir al aire libre. Confeccionada en tejido técnico de secado rápido, con corte holgado para entrenar sin agobios. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-camiseta-outdoor.svg',4,2,79.90,2,6),(118,'Camiseta de manga corta de cuello redondo outdoor blanca','Camiseta de manga corta de niño para salir al aire libre. Confeccionada en punto elástico de compresión ligera, con corte regular, ni pegado ni suelto. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-camiseta-outdoor.svg',4,2,79.90,2,6),(119,'Camiseta de manga corta outdoor granate','Camiseta de manga corta de niño para la montaña y el día a día. Confeccionada en poliéster ligero con tratamiento antiolor, con sisas amplias que dejan respirar. Bajo reforzado que aguanta lavados.','/imagenes/prenda-camiseta-outdoor.svg',4,2,89.90,2,6),(120,'Camiseta de manga corta fútbol blanca','Camiseta de niño para pisar el campo. Confeccionada en algodón peinado con toque seco, con corte regular, ni pegado ni suelto. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-camiseta-futbol.svg',4,2,49.90,2,3),(121,'Camiseta de manga corta de cuello redondo fútbol verde militar','Camiseta de manga corta de niño para pisar el campo. Confeccionada en punto elástico de compresión ligera, con hombros libres para no limitar el gesto. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-camiseta-futbol.svg',4,2,59.90,2,3),(122,'Camiseta de manga corta training blanca','Camiseta de manga corta de niño para entrenar a diario. Confeccionada en punto elástico de compresión ligera, con corte holgado para entrenar sin agobios. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-camiseta-training.svg',4,2,59.90,2,2),(123,'Camiseta técnica de cuello redondo fútbol azul marino','Camiseta técnica de niño para pisar el campo. Confeccionada en tejido reciclado con acabado suave, con hombros libres para no limitar el gesto. Bajo reforzado que aguanta lavados.','/imagenes/prenda-camiseta-futbol.svg',4,2,69.90,2,3),(124,'Camiseta sin mangas outdoor gris','Camiseta de niño para salir al aire libre. Confeccionada en punto elástico de compresión ligera, con hombros libres para no limitar el gesto. Costuras planas para evitar rozaduras.','/imagenes/prenda-camiseta-outdoor.svg',4,2,69.90,2,6),(125,'Short con malla interior training blanco','Short de niño para tus sesiones de fuerza. Confeccionado en punto elástico de compresión ligera, con corte holgado con caída natural. Cintura elástica con cordón interior.','/imagenes/prenda-short-training.svg',4,3,89.90,2,2),(126,'Short deportivo training negro','Short deportivo de niño para tus sesiones de fuerza. Confeccionado en poliéster ligero con tratamiento antiolor, con largo por encima de la rodilla. Costuras planas para evitar rozaduras.','/imagenes/prenda-short-training.svg',4,3,89.90,2,2),(127,'Short de entrenamiento fútbol granate','Short de entrenamiento de niño para pisar el campo. Confeccionado en tejido reciclado con acabado suave, con corte ajustado que no estorba en la zancada. Bolsillo trasero para el móvil.','/imagenes/prenda-short-futbol.svg',4,3,99.90,2,3),(128,'Short deportivo básquet verde militar','Short deportivo de niño para jugar sin límites. Confeccionado en tejido técnico de secado rápido, con corte holgado con caída natural. Cintura elástica con cordón interior.','/imagenes/prenda-short-basquet.svg',4,3,59.90,2,4),(129,'Short deportivo fútbol granate','Short deportivo de niño para pisar el campo. Confeccionado en tejido técnico de secado rápido, con cintura elástica que no aprieta. Cintura elástica con cordón interior.','/imagenes/prenda-short-futbol.svg',4,3,69.90,2,3),(130,'Short running granate','Short de niño para rodajes largos. Confeccionado en tejido reciclado con acabado suave, con corte holgado con caída natural. Bolsillo trasero para el móvil.','/imagenes/prenda-short-running.svg',4,3,69.90,2,1),(131,'Short de tiro medio training azul marino','Short de niño para tus sesiones de fuerza. Confeccionado en algodón peinado con toque seco, con corte ajustado que no estorba en la zancada. Cintura elástica con cordón interior.','/imagenes/prenda-short-training.svg',4,3,79.90,2,2),(132,'Short de entrenamiento de secado rápido training granate','Short de entrenamiento de niño para tus sesiones de fuerza. Confeccionado en poliéster ligero con tratamiento antiolor, con corte holgado con caída natural. Bolsillos laterales con cierre.','/imagenes/prenda-short-training.svg',4,3,79.90,2,2),(133,'Short con malla interior running azul marino','Short de niño para rodajes largos. Confeccionado en algodón peinado con toque seco, con cintura elástica que no aprieta. Bolsillo trasero para el móvil.','/imagenes/prenda-short-running.svg',4,3,89.90,2,1),(134,'Short deportivo con bolsillos training gris','Short deportivo de niño para entrenar a diario. Confeccionado en tejido técnico de secado rápido, con corte holgado con caída natural. Bolsillos laterales con cierre.','/imagenes/prenda-short-training.svg',4,3,89.90,2,2),(135,'Short deportivo con bolsillos básquet negro','Short deportivo de niño para la pista. Confeccionado en tejido técnico de secado rápido, con corte holgado con caída natural. Bolsillos laterales con cierre.','/imagenes/prenda-short-basquet.svg',4,3,99.90,2,4),(136,'Short de tiro medio training blanco','Short de niño para entrenar a diario. Confeccionado en algodón peinado con toque seco, con corte ajustado que no estorba en la zancada. Cintura elástica con cordón interior.','/imagenes/prenda-short-training.svg',4,3,59.90,2,2),(137,'Vestido deportivo con short interior yoga azul marino','Vestido deportivo de niño para estirar y respirar. Confeccionado en algodón peinado con toque seco, con corte holgado para entrenar sin agobios. Costuras planas para evitar rozaduras.','/imagenes/prenda-vestido-yoga.svg',4,5,99.90,2,5),(138,'Vestido deportivo yoga granate','Vestido deportivo de niño para pilates y yoga. Confeccionado en algodón peinado con toque seco, con hombros libres para no limitar el gesto. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-vestido-yoga.svg',4,5,99.90,2,5),(139,'Vestido deportivo yoga gris','Vestido deportivo de niño para pilates y yoga. Confeccionado en punto elástico de compresión ligera, con corte ajustado que sigue el movimiento. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-vestido-yoga.svg',4,5,109.90,2,5),(140,'Vestido de entrenamiento sin mangas training azul marino','Vestido de entrenamiento de niño para tus sesiones de fuerza. Confeccionado en poliéster ligero con tratamiento antiolor, con corte holgado para entrenar sin agobios. Costuras planas para evitar rozaduras.','/imagenes/prenda-vestido-training.svg',4,5,119.90,2,2),(141,'Vestido de entrenamiento sin mangas yoga blanco','Vestido de entrenamiento de niño para pilates y yoga. Confeccionado en tejido reciclado con acabado suave, con corte ajustado que sigue el movimiento. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-vestido-yoga.svg',4,5,129.90,2,5),(142,'Vestido de entrenamiento yoga blanco','Vestido de entrenamiento de niño para pilates y yoga. Confeccionado en algodón peinado con toque seco, con sisas amplias que dejan respirar. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-vestido-yoga.svg',4,5,129.90,2,5),(143,'Vestido deportivo con short interior yoga blanco','Vestido deportivo de niño para pilates y yoga. Confeccionado en algodón peinado con toque seco, con corte regular, ni pegado ni suelto. Detalle reflectante para sesiones de tarde.','/imagenes/prenda-vestido-yoga.svg',4,5,139.90,2,5),(144,'Vestido deportivo yoga verde militar','Vestido deportivo de niño para yoga y movilidad. Confeccionado en malla transpirable en zonas de calor, con sisas amplias que dejan respirar. Costuras planas para evitar rozaduras.','/imagenes/prenda-vestido-yoga.svg',4,5,89.90,2,5),(145,'Vestido de entrenamiento training azul marino','Vestido de entrenamiento de niño para entrenar a diario. Confeccionado en poliéster ligero con tratamiento antiolor, con sisas amplias que dejan respirar. Bajo reforzado que aguanta lavados.','/imagenes/prenda-vestido-training.svg',4,5,99.90,2,2),(146,'Vestido de entrenamiento yoga azul marino','Vestido de entrenamiento de niño para pilates y yoga. Confeccionado en punto elástico de compresión ligera, con corte ajustado que sigue el movimiento. Etiqueta impresa, sin costura en el cuello.','/imagenes/prenda-vestido-yoga.svg',4,5,99.90,2,5),(147,'Pantalón jogger outdoor verde militar','Pantalón jogger de mujer para la montaña y el día a día. Confeccionado en punto elástico de compresión ligera, con cintura alta que se mantiene en su sitio. Bolsillo trasero para el móvil.','/imagenes/foto-pantalon-02.jpg',1,4,129.90,2,6),(148,'Pantalón de entrenamiento holgado training azul marino','Pantalón de entrenamiento de mujer para el gimnasio. Confeccionado en malla transpirable en zonas de calor, con corte ajustado que no estorba en la zancada. Costuras planas para evitar rozaduras.','/imagenes/foto-pantalon-03.jpg',1,4,129.90,2,2),(149,'Falda-short con malla interior training gris','Falda-short de mujer para el gimnasio. Confeccionada en algodón peinado con toque seco, con corte ajustado que no estorba en la zancada. Cintura elástica con cordón interior.','/imagenes/prenda-falda-training.svg',1,8,99.90,2,2),(150,'Falda-short con malla interior training negra','Falda-short de mujer para el gimnasio. Confeccionada en tejido reciclado con acabado suave, con cintura elástica que no aprieta. Bolsillos laterales con cierre.','/imagenes/prenda-falda-training.svg',1,8,99.90,2,2),(151,'Falda deportiva yoga blanca','Falda deportiva de mujer para pilates y yoga. Confeccionada en poliéster ligero con tratamiento antiolor, con corte holgado con caída natural. Bolsillos laterales con cierre.','/imagenes/prenda-falda-yoga.svg',1,8,109.90,2,5),(152,'Short de entrenamiento básquet gris','Short de entrenamiento de mujer para jugar sin límites. Confeccionado en poliéster ligero con tratamiento antiolor, con cintura alta que se mantiene en su sitio. Costuras planas para evitar rozaduras.','/imagenes/prenda-short-basquet.svg',1,3,59.90,2,4),(153,'Conjunto deportivo training azul marino','Conjunto deportivo de bebé para tus sesiones de fuerza. Confeccionado en tejido técnico de secado rápido, con sisas amplias que dejan respirar. Costuras planas para evitar rozaduras.','/imagenes/8f473c4a-46a9-4cd0-b1ff-192eab171692.jpg',3,NULL,39.90,4,2),(154,'Conjunto deportivo de dos piezas training granate','Conjunto deportivo de bebé para tus sesiones de fuerza. Confeccionado en tejido reciclado con acabado suave, con hombros libres para no limitar el gesto. Bajo reforzado que aguanta lavados.','/imagenes/51bde60f-0555-46a6-924d-9b5bd7b6f1f6.webp',4,NULL,49.90,4,2),(155,'Body deportivo de manga corta training negro','Body deportivo de bebé para el gimnasio. Confeccionado en tejido técnico de secado rápido, con hombros libres para no limitar el gesto. Bajo reforzado que aguanta lavados.','/imagenes/0215ec01-c050-4f82-9527-58e6ecfb29f3.jpg',3,NULL,49.90,3,2),(156,'Conjunto deportivo training granate','Conjunto deportivo de bebé para entrenar a diario. Confeccionado en punto elástico de compresión ligera, con corte holgado para entrenar sin agobios. Etiqueta impresa, sin costura en el cuello.','/imagenes/a7efb362-93ed-4883-83b2-58669019c802.jpg',3,NULL,59.90,3,2),(157,'Conjunto deportivo de dos piezas training azul marino','Conjunto deportivo de bebé para el gimnasio. Confeccionado en tejido reciclado con acabado suave, con corte ajustado que sigue el movimiento. Etiqueta impresa, sin costura en el cuello.','/imagenes/3ab39b34-9ac8-401c-98a2-628ed6f045a2.jpg',3,NULL,59.90,3,2),(158,'Body deportivo training gris','Body deportivo de bebé para el gimnasio. Confeccionado en poliéster ligero con tratamiento antiolor, con hombros libres para no limitar el gesto. Costuras planas para evitar rozaduras.','/imagenes/eba89bbd-5e99-4db7-9615-717f0b16087c.jpg',3,NULL,69.90,3,2),(159,'Body deportivo training granate','Body deportivo de bebé para el gimnasio. Confeccionado en poliéster ligero con tratamiento antiolor, con corte holgado para entrenar sin agobios. Etiqueta impresa, sin costura en el cuello.','/imagenes/05788ee9-9003-4388-a47e-9f4d2fc9c790.webp',3,NULL,69.90,3,2),(160,'Conjunto deportivo de dos piezas training blanco','Conjunto deportivo de bebé para el gimnasio. Confeccionado en tejido técnico de secado rápido, con corte holgado para entrenar sin agobios. Bajo reforzado que aguanta lavados.','/imagenes/af36f2f4-ebac-4076-ae16-d93d42eecb36.webp',3,NULL,39.90,3,2),(162,'Conjunto deportivo training verde militar','Conjunto deportivo de bebé para el gimnasio. Confeccionado en tejido técnico de secado rápido, con corte holgado para entrenar sin agobios. Etiqueta impresa, sin costura en el cuello.','/imagenes/d1386426-94ab-46d3-8ece-2ad349b560ab.webp',3,NULL,49.90,3,2),(163,'Camiseta sin mangas training negra','Camiseta de niño para tus sesiones de fuerza. Confeccionada en tejido técnico de secado rápido, con corte ajustado que sigue el movimiento. Costuras planas para evitar rozaduras.','/imagenes/311c70b5-3777-4735-80b8-0aada80176f2.jpg',4,2,69.90,3,2);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `producto_imagen` WRITE;
/*!40000 ALTER TABLE `producto_imagen` DISABLE KEYS */;
INSERT INTO `producto_imagen` VALUES (1,0,'/imagenes/8f473c4a-46a9-4cd0-b1ff-192eab171692.jpg',153),(2,1,'/imagenes/693d0c45-763f-419f-893c-a16cbfccd2bf.jpg',153),(3,0,'/imagenes/51bde60f-0555-46a6-924d-9b5bd7b6f1f6.webp',154),(4,1,'/imagenes/c2efb0b2-2220-4600-9973-378f693c0879.webp',154),(5,2,'/imagenes/813d3b17-d4f5-4a64-89ad-f678466b3dde.webp',154),(6,0,'/imagenes/0215ec01-c050-4f82-9527-58e6ecfb29f3.jpg',155),(7,1,'/imagenes/867cc00c-af23-4f06-a7cc-ebb51af37cbd.webp',155),(8,0,'/imagenes/a7efb362-93ed-4883-83b2-58669019c802.jpg',156),(9,0,'/imagenes/3ab39b34-9ac8-401c-98a2-628ed6f045a2.jpg',157),(10,0,'/imagenes/eba89bbd-5e99-4db7-9615-717f0b16087c.jpg',158),(12,0,'/imagenes/af36f2f4-ebac-4076-ae16-d93d42eecb36.webp',160),(13,0,'/imagenes/d1386426-94ab-46d3-8ece-2ad349b560ab.webp',162),(14,0,'/imagenes/311c70b5-3777-4735-80b8-0aada80176f2.jpg',163),(15,0,'/imagenes/05788ee9-9003-4388-a47e-9f4d2fc9c790.webp',159),(16,1,'/imagenes/45ead979-7e24-4dee-9a8e-f0a72cc36b07.jpg',159);
/*!40000 ALTER TABLE `producto_imagen` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `producto_talla` WRITE;
/*!40000 ALTER TABLE `producto_talla` DISABLE KEYS */;
INSERT INTO `producto_talla` VALUES (1084,10,0,5,1),(1085,32,0,5,2),(1086,19,0,5,3),(1087,13,0,5,4),(1088,20,0,5,5),(1089,39,0,6,1),(1090,33,0,6,2),(1091,6,0,6,3),(1092,31,0,6,4),(1093,22,0,6,5),(1094,26,0,7,1),(1095,13,0,7,2),(1096,11,0,7,3),(1097,25,0,7,4),(1098,13,0,7,5),(1099,11,0,7,6),(1100,17,0,8,1),(1101,34,0,8,2),(1102,23,0,8,3),(1103,30,0,8,4),(1104,7,0,8,5),(1105,25,0,8,6),(1106,26,0,9,1),(1107,14,0,9,2),(1108,36,0,9,3),(1109,36,0,9,4),(1110,28,0,9,5),(1111,21,0,9,6),(1112,5,0,10,1),(1113,8,0,10,2),(1114,16,0,10,3),(1115,6,0,10,4),(1116,13,0,10,5),(1117,31,0,10,6),(1118,35,0,11,1),(1119,35,0,11,2),(1120,28,0,11,3),(1121,4,0,11,4),(1122,34,0,11,5),(1123,34,0,11,6),(1124,14,0,12,1),(1125,22,0,12,2),(1126,38,0,12,3),(1127,26,0,12,4),(1128,17,0,12,5),(1129,25,0,12,6),(1130,13,0,13,1),(1131,11,0,13,2),(1132,24,0,13,3),(1133,19,0,13,4),(1134,16,0,13,5),(1135,35,0,13,6),(1136,17,0,14,1),(1137,30,0,14,2),(1138,17,0,14,3),(1139,25,0,14,4),(1140,36,0,14,5),(1141,8,0,14,6),(1142,20,0,15,1),(1143,26,0,15,2),(1144,5,0,15,3),(1145,36,0,15,4),(1146,24,0,15,5),(1147,36,0,15,6),(1148,26,0,17,1),(1149,15,0,17,2),(1150,38,0,17,3),(1151,19,0,17,4),(1152,29,0,17,5),(1153,13,0,17,6),(1154,14,0,18,1),(1155,21,0,18,2),(1156,35,0,18,3),(1157,6,0,18,4),(1158,10,0,18,5),(1159,37,0,18,6),(1160,27,0,19,1),(1161,34,0,19,2),(1162,18,0,19,3),(1163,38,0,19,4),(1164,34,0,19,5),(1165,27,0,19,6),(1166,9,0,20,1),(1167,12,0,20,2),(1168,9,0,20,3),(1169,19,0,20,4),(1170,39,0,20,5),(1171,24,0,20,6),(1172,36,0,21,1),(1173,22,0,21,2),(1174,19,0,21,3),(1175,29,0,21,4),(1176,27,0,21,5),(1177,20,0,21,6),(1178,20,0,22,1),(1179,8,0,22,2),(1180,40,0,22,3),(1181,18,0,22,4),(1182,15,0,22,5),(1183,29,0,22,6),(1184,23,0,23,1),(1185,11,0,23,2),(1186,30,0,23,3),(1187,36,0,23,4),(1188,19,0,23,5),(1189,37,0,23,6),(1190,9,0,24,1),(1191,7,0,24,2),(1192,33,0,24,3),(1193,26,0,24,4),(1194,36,0,24,5),(1195,30,0,24,6),(1196,7,0,25,1),(1197,11,0,25,2),(1198,14,0,25,3),(1199,17,0,25,4),(1200,38,0,25,5),(1201,30,0,25,6),(1202,8,0,26,1),(1203,22,0,26,2),(1204,11,0,26,3),(1205,40,0,26,4),(1206,33,0,26,5),(1207,18,0,26,6),(1208,18,0,27,1),(1209,23,0,27,2),(1210,39,0,27,3),(1211,18,0,27,4),(1212,40,0,27,5),(1213,20,0,27,6),(1214,19,0,28,1),(1215,23,0,28,2),(1216,38,0,28,3),(1217,20,0,28,4),(1218,4,0,28,5),(1219,24,0,28,6),(1220,11,0,29,1),(1221,8,0,29,2),(1222,31,0,29,3),(1223,28,0,29,4),(1224,20,0,29,5),(1225,31,0,29,6),(1226,17,0,30,1),(1227,14,0,30,2),(1228,10,0,30,3),(1229,15,0,30,4),(1230,11,0,30,5),(1231,11,0,30,6),(1232,4,0,31,1),(1233,5,0,31,2),(1234,4,0,31,3),(1235,28,0,31,4),(1236,13,0,31,5),(1237,12,0,31,6),(1238,17,0,32,1),(1239,27,0,32,2),(1240,11,0,32,3),(1241,10,0,32,4),(1242,29,0,32,5),(1243,14,0,32,6),(1244,28,0,33,1),(1245,25,0,33,2),(1246,36,0,33,3),(1247,12,0,33,4),(1248,33,0,33,5),(1249,15,0,33,6),(1250,14,0,34,2),(1251,39,0,34,3),(1252,16,0,34,4),(1253,19,0,34,5),(1254,35,0,34,6),(1255,39,0,34,7),(1256,27,0,38,2),(1257,4,0,38,3),(1258,18,0,38,4),(1259,19,0,38,5),(1260,10,0,38,6),(1261,29,0,38,7),(1262,12,0,39,2),(1263,16,0,39,3),(1264,35,0,39,4),(1265,15,0,39,5),(1266,20,0,39,6),(1267,19,0,39,7),(1268,18,0,41,2),(1269,19,0,41,3),(1270,35,0,41,4),(1271,12,0,41,5),(1272,18,0,41,6),(1273,29,0,41,7),(1274,29,0,42,2),(1275,20,0,42,3),(1276,26,0,42,4),(1277,38,0,42,5),(1278,14,0,42,6),(1279,27,0,42,7),(1280,29,0,44,2),(1281,29,0,44,3),(1282,24,0,44,4),(1283,31,0,44,5),(1284,32,0,44,6),(1285,9,0,44,7),(1286,6,0,45,2),(1287,6,0,45,3),(1288,6,0,45,4),(1289,36,0,45,5),(1290,5,0,45,6),(1291,14,0,45,7),(1292,22,0,46,2),(1293,40,0,46,3),(1294,17,0,46,4),(1295,4,0,46,5),(1296,19,0,46,6),(1297,33,0,46,7),(1298,32,0,47,2),(1299,39,0,47,3),(1300,29,0,47,4),(1301,38,0,47,5),(1302,5,0,47,6),(1303,29,0,47,7),(1304,8,0,48,2),(1305,6,0,48,3),(1306,40,0,48,4),(1307,27,0,48,5),(1308,16,0,48,6),(1309,11,0,48,7),(1310,39,0,49,2),(1311,6,0,49,3),(1312,16,0,49,4),(1313,10,0,49,5),(1314,37,0,49,6),(1315,23,0,49,7),(1316,27,0,50,2),(1317,26,0,50,3),(1318,8,0,50,4),(1319,9,0,50,5),(1320,18,0,50,6),(1321,37,0,50,7),(1322,21,0,51,2),(1323,31,0,51,3),(1324,35,0,51,4),(1325,12,0,51,5),(1326,28,0,51,6),(1327,12,0,51,7),(1328,27,0,52,2),(1329,35,0,52,3),(1330,35,0,52,4),(1331,21,0,52,5),(1332,24,0,52,6),(1333,39,0,52,7),(1334,28,0,53,2),(1335,37,0,53,3),(1336,34,0,53,4),(1337,4,0,53,5),(1338,28,0,53,6),(1339,11,0,53,7),(1340,19,0,54,2),(1341,38,0,54,3),(1342,37,0,54,4),(1343,22,0,54,5),(1344,34,0,54,6),(1345,6,0,54,7),(1346,24,0,55,2),(1347,34,0,55,3),(1348,29,0,55,4),(1349,29,0,55,5),(1350,16,0,55,6),(1351,37,0,55,7),(1352,23,0,56,2),(1353,17,0,56,3),(1354,12,0,56,4),(1355,4,0,56,5),(1356,36,0,56,6),(1357,7,0,56,7),(1358,8,0,57,2),(1359,34,0,57,3),(1360,24,0,57,4),(1361,5,0,57,5),(1362,6,0,57,6),(1363,19,0,57,7),(1364,9,0,58,2),(1365,7,0,58,3),(1366,38,0,58,4),(1367,29,0,58,5),(1368,9,0,58,6),(1369,9,0,58,7),(1370,8,0,59,2),(1371,22,0,59,3),(1372,33,0,59,4),(1373,22,0,59,5),(1374,25,0,59,6),(1375,15,0,59,7),(1376,14,0,60,2),(1377,30,0,60,3),(1378,30,0,60,4),(1379,9,0,60,5),(1380,13,0,60,6),(1381,24,0,60,7),(1382,34,0,61,2),(1383,37,0,61,3),(1384,34,0,61,4),(1385,26,0,61,5),(1386,9,0,61,6),(1387,27,0,61,7),(1388,20,0,62,2),(1389,32,0,62,3),(1390,15,0,62,4),(1391,35,0,62,5),(1392,33,0,62,6),(1393,16,0,62,7),(1394,26,0,63,2),(1395,13,0,63,3),(1396,26,0,63,4),(1397,24,0,63,5),(1398,29,0,63,6),(1399,12,0,63,7),(1400,20,0,64,2),(1401,34,0,64,3),(1402,5,0,64,4),(1403,26,0,64,5),(1404,16,0,64,6),(1405,28,0,64,7),(1406,21,0,65,2),(1407,28,0,65,3),(1408,8,0,65,4),(1409,18,0,65,5),(1410,11,0,65,6),(1411,37,0,65,7),(1412,35,0,66,1),(1413,14,0,66,2),(1414,7,0,66,3),(1415,27,0,66,4),(1416,20,0,66,5),(1417,13,0,67,1),(1418,10,0,67,2),(1419,26,0,67,3),(1420,31,0,67,4),(1421,16,0,67,5),(1422,38,0,68,1),(1423,11,0,68,2),(1424,5,0,68,3),(1425,13,0,68,4),(1426,10,0,68,5),(1427,7,0,69,1),(1428,5,0,69,2),(1429,19,0,69,3),(1430,14,0,69,4),(1431,35,0,69,5),(1432,7,0,70,1),(1433,23,0,70,2),(1434,29,0,70,3),(1435,7,0,70,4),(1436,40,0,70,5),(1437,25,0,71,1),(1438,29,0,71,2),(1439,16,0,71,3),(1440,14,0,71,4),(1441,33,0,71,5),(1442,17,0,72,1),(1443,33,0,72,2),(1444,30,0,72,3),(1445,12,0,72,4),(1446,7,0,72,5),(1447,40,0,73,1),(1448,22,0,73,2),(1449,38,0,73,3),(1450,25,0,73,4),(1451,17,0,73,5),(1452,37,0,74,1),(1453,16,0,74,2),(1454,26,0,74,3),(1455,10,0,74,4),(1456,36,0,74,5),(1457,16,0,75,1),(1458,23,0,75,2),(1459,22,0,75,3),(1460,40,0,75,4),(1461,36,0,75,5),(1462,39,0,76,1),(1463,7,0,76,2),(1464,37,0,76,3),(1465,12,0,76,4),(1466,34,0,76,5),(1467,14,0,77,1),(1468,13,0,77,2),(1469,6,0,77,3),(1470,6,0,77,4),(1471,25,0,77,5),(1472,12,0,78,1),(1473,10,0,78,2),(1474,24,0,78,3),(1475,36,0,78,4),(1476,14,0,78,5),(1477,34,0,79,1),(1478,6,0,79,2),(1479,9,0,79,3),(1480,7,0,79,4),(1481,15,0,79,5),(1482,10,0,80,1),(1483,21,0,80,2),(1484,33,0,80,3),(1485,11,0,80,4),(1486,33,0,80,5),(1487,10,0,81,1),(1488,31,0,81,2),(1489,15,0,81,3),(1490,18,0,81,4),(1491,38,0,81,5),(1492,29,0,82,1),(1493,26,0,82,2),(1494,36,0,82,3),(1495,13,0,82,4),(1496,23,0,82,5),(1497,9,0,83,1),(1498,29,0,83,2),(1499,31,0,83,3),(1500,39,0,83,4),(1501,30,0,83,5),(1502,4,0,84,1),(1503,23,0,84,2),(1504,19,0,84,3),(1505,28,0,84,4),(1506,18,0,84,5),(1507,39,0,85,1),(1508,6,0,85,2),(1509,10,0,85,3),(1510,11,0,85,4),(1511,29,0,85,5),(1512,26,0,86,1),(1513,6,0,86,2),(1514,40,0,86,3),(1515,36,0,86,4),(1516,39,0,86,5),(1517,29,0,87,1),(1518,11,0,87,2),(1519,6,0,87,3),(1520,6,0,87,4),(1521,32,0,87,5),(1522,39,0,88,1),(1523,19,0,88,2),(1524,29,0,88,3),(1525,24,0,88,4),(1526,13,0,88,5),(1527,15,0,89,1),(1528,19,0,89,2),(1529,33,0,89,3),(1530,30,0,89,4),(1531,27,0,89,5),(1532,27,0,90,1),(1533,37,0,90,2),(1534,29,0,90,3),(1535,18,0,90,4),(1536,21,0,90,5),(1537,13,0,91,1),(1538,30,0,91,2),(1539,12,0,91,3),(1540,23,0,91,4),(1541,38,0,91,5),(1542,21,0,92,1),(1543,36,0,92,2),(1544,17,0,92,3),(1545,29,0,92,4),(1546,32,0,92,5),(1547,23,0,93,1),(1548,36,0,93,2),(1549,35,0,93,3),(1550,19,0,93,4),(1551,29,0,93,5),(1552,36,0,94,1),(1553,6,0,94,2),(1554,12,0,94,3),(1555,30,0,94,4),(1556,30,0,94,5),(1557,4,0,95,1),(1558,5,0,95,2),(1559,6,0,95,3),(1560,12,0,95,4),(1561,25,0,95,5),(1562,30,0,96,1),(1563,16,0,96,2),(1564,30,0,96,3),(1565,22,0,96,4),(1566,9,0,96,5),(1567,4,0,97,1),(1568,33,0,97,2),(1569,36,0,97,3),(1570,8,0,97,4),(1571,26,0,97,5),(1572,15,0,98,1),(1573,9,0,98,2),(1574,38,0,98,3),(1575,40,0,98,4),(1576,16,0,98,5),(1577,40,0,99,1),(1578,17,0,99,2),(1579,5,0,99,3),(1580,24,0,99,4),(1581,11,0,99,5),(1582,8,0,100,1),(1583,30,0,100,2),(1584,30,0,100,3),(1585,9,0,100,4),(1586,8,0,100,5),(1587,18,0,101,1),(1588,8,0,101,2),(1589,32,0,101,3),(1590,6,0,101,4),(1591,16,0,101,5),(1592,10,0,102,1),(1593,16,0,102,2),(1594,34,0,102,3),(1595,14,0,102,4),(1596,14,0,102,5),(1597,20,0,103,1),(1598,19,0,103,2),(1599,14,0,103,3),(1600,12,0,103,4),(1601,29,0,103,5),(1602,28,0,104,1),(1603,35,0,104,2),(1604,29,0,104,3),(1605,28,0,104,4),(1606,17,0,104,5),(1607,35,0,105,1),(1608,29,0,105,2),(1609,27,0,105,3),(1610,37,0,105,4),(1611,36,0,105,5),(1612,26,0,106,1),(1613,19,0,106,2),(1614,24,0,106,3),(1615,4,0,106,4),(1616,37,0,106,5),(1617,27,0,107,1),(1618,23,0,107,2),(1619,33,0,107,3),(1620,15,0,107,4),(1621,16,0,107,5),(1622,30,0,108,1),(1623,36,0,108,2),(1624,13,0,108,3),(1625,27,0,108,4),(1626,9,0,108,5),(1627,10,0,109,1),(1628,36,0,109,2),(1629,40,0,109,3),(1630,31,0,109,4),(1631,25,0,109,5),(1632,30,0,110,1),(1633,17,0,110,2),(1634,7,0,110,3),(1635,18,0,110,4),(1636,19,0,110,5),(1637,36,0,111,1),(1638,23,0,111,2),(1639,30,0,111,3),(1640,22,0,111,4),(1641,18,0,111,5),(1642,14,0,112,1),(1643,32,0,112,2),(1644,21,0,112,3),(1645,34,0,112,4),(1646,25,0,112,5),(1647,29,0,113,1),(1648,31,0,113,2),(1649,5,0,113,3),(1650,4,0,113,4),(1651,5,0,113,5),(1652,4,0,114,1),(1653,31,0,114,2),(1654,10,0,114,3),(1655,39,0,114,4),(1656,6,0,114,5),(1657,11,0,115,1),(1658,39,0,115,2),(1659,30,0,115,3),(1660,30,0,115,4),(1661,19,0,115,5),(1662,14,0,116,1),(1663,35,0,116,2),(1664,25,0,116,3),(1665,35,0,116,4),(1666,36,0,116,5),(1667,24,0,117,1),(1668,37,0,117,2),(1669,18,0,117,3),(1670,38,0,117,4),(1671,8,0,117,5),(1672,10,0,118,1),(1673,33,0,118,2),(1674,40,0,118,3),(1675,8,0,118,4),(1676,17,0,118,5),(1677,23,0,119,1),(1678,32,0,119,2),(1679,12,0,119,3),(1680,12,0,119,4),(1681,36,0,119,5),(1682,37,0,120,1),(1683,21,0,120,2),(1684,14,0,120,3),(1685,20,0,120,4),(1686,4,0,120,5),(1687,5,0,121,1),(1688,37,0,121,2),(1689,5,0,121,3),(1690,16,0,121,4),(1691,22,0,121,5),(1692,13,0,122,1),(1693,16,0,122,2),(1694,17,0,122,3),(1695,34,0,122,4),(1696,10,0,122,5),(1697,38,0,123,1),(1698,24,0,123,2),(1699,39,0,123,3),(1700,15,0,123,4),(1701,30,0,123,5),(1702,14,0,124,1),(1703,6,0,124,2),(1704,20,0,124,3),(1705,34,0,124,4),(1706,5,0,124,5),(1707,37,0,125,1),(1708,32,0,125,2),(1709,22,0,125,3),(1710,27,0,125,4),(1711,34,0,125,5),(1712,19,0,126,1),(1713,36,0,126,2),(1714,28,0,126,3),(1715,5,0,126,4),(1716,15,0,126,5),(1717,33,0,127,1),(1718,33,0,127,2),(1719,32,0,127,3),(1720,29,0,127,4),(1721,30,0,127,5),(1722,36,0,128,1),(1723,22,0,128,2),(1724,29,0,128,3),(1725,29,0,128,4),(1726,6,0,128,5),(1727,33,0,129,1),(1728,10,0,129,2),(1729,13,0,129,3),(1730,30,0,129,4),(1731,23,0,129,5),(1732,38,0,130,1),(1733,4,0,130,2),(1734,29,0,130,3),(1735,38,0,130,4),(1736,40,0,130,5),(1737,15,0,131,1),(1738,32,0,131,2),(1739,39,0,131,3),(1740,25,0,131,4),(1741,10,0,131,5),(1742,34,0,132,1),(1743,13,0,132,2),(1744,16,0,132,3),(1745,25,0,132,4),(1746,24,0,132,5),(1747,8,0,133,1),(1748,9,0,133,2),(1749,36,0,133,3),(1750,26,0,133,4),(1751,14,0,133,5),(1752,19,0,134,1),(1753,21,0,134,2),(1754,6,0,134,3),(1755,35,0,134,4),(1756,18,0,134,5),(1757,7,0,135,1),(1758,5,0,135,2),(1759,38,0,135,3),(1760,15,0,135,4),(1761,9,0,135,5),(1762,27,0,136,1),(1763,20,0,136,2),(1764,25,0,136,3),(1765,29,0,136,4),(1766,8,0,136,5),(1767,26,0,137,1),(1768,8,0,137,2),(1769,5,0,137,3),(1770,19,0,137,4),(1771,35,0,137,5),(1772,4,0,138,1),(1773,12,0,138,2),(1774,40,0,138,3),(1775,32,0,138,4),(1776,4,0,138,5),(1777,25,0,139,1),(1778,9,0,139,2),(1779,39,0,139,3),(1780,25,0,139,4),(1781,12,0,139,5),(1782,24,0,140,1),(1783,25,0,140,2),(1784,40,0,140,3),(1785,26,0,140,4),(1786,29,0,140,5),(1787,34,0,141,1),(1788,12,0,141,2),(1789,23,0,141,3),(1790,17,0,141,4),(1791,21,0,141,5),(1792,10,0,142,1),(1793,26,0,142,2),(1794,19,0,142,3),(1795,7,0,142,4),(1796,6,0,142,5),(1797,26,0,143,1),(1798,21,0,143,2),(1799,18,0,143,3),(1800,15,0,143,4),(1801,17,0,143,5),(1802,7,0,144,1),(1803,5,0,144,2),(1804,40,0,144,3),(1805,22,0,144,4),(1806,28,0,144,5),(1807,9,0,145,1),(1808,14,0,145,2),(1809,8,0,145,3),(1810,32,0,145,4),(1811,11,0,145,5),(1812,14,0,146,1),(1813,30,0,146,2),(1814,29,0,146,3),(1815,6,0,146,4),(1816,26,0,146,5),(1817,15,0,147,1),(1818,20,0,147,2),(1819,20,0,147,3),(1820,9,0,147,4),(1821,37,0,147,5),(1822,30,0,147,6),(1823,18,0,148,1),(1824,36,0,148,2),(1825,31,0,148,3),(1826,27,0,148,4),(1827,32,0,148,5),(1828,28,0,148,6),(1829,32,0,149,1),(1830,18,0,149,2),(1831,13,0,149,3),(1832,4,0,149,4),(1833,25,0,149,5),(1834,36,0,149,6),(1835,32,0,150,1),(1836,39,0,150,2),(1837,34,0,150,3),(1838,39,0,150,4),(1839,10,0,150,5),(1840,17,0,150,6),(1841,15,0,151,1),(1842,25,0,151,2),(1843,12,0,151,3),(1844,9,0,151,4),(1845,27,0,151,5),(1846,7,0,151,6),(1847,30,0,152,1),(1848,21,0,152,2),(1849,30,0,152,3),(1850,39,0,152,4),(1851,19,0,152,5),(1852,10,0,152,6),(1894,23,0,1,4),(1895,10,0,1,3),(1896,22,0,1,2),(1897,16,0,1,5),(1898,20,0,1,1),(1899,26,0,1,6),(1900,8,0,153,8),(1901,35,0,153,9),(1902,10,0,153,2),(1903,23,0,153,1),(1904,31,0,154,8),(1905,16,0,154,9),(1906,18,0,154,2),(1907,25,0,154,1),(1908,12,0,155,8),(1909,31,0,155,9),(1910,12,0,155,2),(1911,33,0,155,1),(1912,31,0,156,8),(1913,20,0,156,9),(1914,32,0,156,2),(1915,34,0,156,1),(1916,37,0,157,8),(1917,33,0,157,9),(1918,7,0,157,2),(1919,30,0,157,1),(1920,11,0,158,8),(1921,30,0,158,9),(1922,21,0,158,2),(1923,14,0,158,1),(1928,38,0,160,8),(1929,31,0,160,9),(1930,39,0,160,2),(1931,24,0,160,1),(1932,6,0,162,8),(1933,39,0,162,9),(1934,15,0,162,2),(1935,18,0,162,1),(1936,20,0,163,4),(1937,21,0,163,3),(1938,13,0,163,2),(1939,32,0,163,5),(1940,23,0,163,1),(1941,31,0,159,8),(1942,18,0,159,9),(1943,28,0,159,2),(1944,31,0,159,1);
/*!40000 ALTER TABLE `producto_talla` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `talla` WRITE;
/*!40000 ALTER TABLE `talla` DISABLE KEYS */;
INSERT INTO `talla` VALUES (7,'BIG SIZE'),(8,'ESTÁNDAR'),(4,'L'),(3,'M'),(9,'PEQUEÑO'),(2,'S'),(5,'XL'),(1,'XS'),(6,'XXL');
/*!40000 ALTER TABLE `talla` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `tipo_prenda` WRITE;
/*!40000 ALTER TABLE `tipo_prenda` DISABLE KEYS */;
INSERT INTO `tipo_prenda` VALUES (6,'Casaca'),(7,'Chompa'),(8,'Falda'),(4,'Pantalón'),(1,'Polera'),(2,'Polo'),(3,'Short'),(5,'Vestido');
/*!40000 ALTER TABLE `tipo_prenda` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

