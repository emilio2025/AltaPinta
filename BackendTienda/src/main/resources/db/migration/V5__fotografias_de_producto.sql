-- ============================================================
--  V5 - Fotografias reales en el catalogo de Mujer
--
--  Sustituye los bocetos SVG monocromos por fotografia de producto.
--  Los bocetos se generaron cuando no habia fotos disponibles y
--  cumplieron su funcion, pero una tienda de ropa se vende por la
--  imagen: un dibujo negro no deja ver el corte ni el color.
--
--  El emparejamiento se hizo por tipo_prenda, que es el dato fiable:
--  los archivos venian nombrados por tipo ('Maxi Vestido Lucien.jpg')
--  y dentro de cada tipo se asignaron en orden.
--
--  Productos actualizados: 28
--  Sin fotografia (conservan su boceto): 5
-- ============================================================

-- Cortavientos con capucha running blanco  <-  Casaca Boxy Fit Hedberg.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-casaca-01.jpg' WHERE id = 7;
-- Casaca deportiva con cremallera outdoor blanca  <-  Casaca Crop Fit Alfred.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-casaca-02.jpg' WHERE id = 8;
-- Casaca deportiva running granate  <-  Casaca Tueson.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-casaca-03.jpg' WHERE id = 9;
-- Sudadera térmica de cuello alto training gris  <-  Chompa Carry.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-chompa-01.jpg' WHERE id = 10;
-- Chaqueta de media cremallera térmica outdoor gris  <-  Chompa Crop Fit Calaf.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-chompa-02.jpg' WHERE id = 11;
-- Sudadera térmica de cuello alto training granate  <-  Chompa Crop Fit Jane.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-chompa-03.jpg' WHERE id = 12;
-- Falda deportiva con short interior training negra  <-  Falda Denim York Pank.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-falda-01.jpg' WHERE id = 13;
-- Falda deportiva plisada training gris  <-  Maxi Falda Dreyer.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-falda-02.jpg' WHERE id = 14;
-- Falda-short con malla interior training blanca  <-  Mini Falda Denim Jazz.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-falda-03.jpg' WHERE id = 15;
-- Legging de tiro alto training gris  <-  Pantalón cargo de algodón.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-pantalon-01.jpg' WHERE id = 1;
-- Pantalón jogger outdoor verde militar  <-  Pantalón cargo holgado.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-pantalon-02.jpg' WHERE id = 147;
-- Pantalón de entrenamiento holgado training azul marino  <-  Pantalón cargo Slim.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-pantalon-03.jpg' WHERE id = 148;
-- Polerón técnico con capucha running gris  <-  Polera boxi fit snow.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-polera-01.jpg' WHERE id = 17;
-- Sudadera con capucha de algodón training granate  <-  Polera Regular Fit Amat.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-polera-02.jpg' WHERE id = 18;
-- Sudadera training granate  <-  Polera Regular Fit Cranmer.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-polera-03.jpg' WHERE id = 19;
-- Camiseta de manga larga training verde militar  <-  Polo Baby Tee Owen.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-polo-01.jpg' WHERE id = 20;
-- Camiseta de manga corta de cuello redondo training gris  <-  polo camisero burnett.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-polo-02.jpg' WHERE id = 21;
-- Camiseta de manga corta training negra  <-  polo croptop kyle.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-polo-03.jpg' WHERE id = 22;
-- Camiseta de manga corta de cuello redondo outdoor negra  <-  Polo Regular Fit Milfs.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-polo-04.jpg' WHERE id = 23;
-- Short de tiro medio running azul marino  <-  Short Denim Hawai.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-short-01.jpg' WHERE id = 24;
-- Short deportivo training azul marino  <-  Short goetzmujer.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-short-02.jpg' WHERE id = 25;
-- Short de entrenamiento running azul marino  <-  Short short lovisa.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-short-03.jpg' WHERE id = 26;
-- Short de entrenamiento de secado rápido básquet azul marino  <-  Short slim fit blida.webp
UPDATE producto SET imagen_url = '/imagenes/foto-short-04.webp' WHERE id = 27;
-- Vestido de entrenamiento sin mangas training gris  <-  Maxi Vestido Lucien.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-vestido-01.jpg' WHERE id = 28;
-- Vestido de entrenamiento yoga verde militar  <-  Maxi Vestido Offman.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-vestido-02.jpg' WHERE id = 29;
-- Vestido deportivo con short interior yoga verde militar  <-  Maxi Vestido Race.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-vestido-03.jpg' WHERE id = 30;
-- Vestido deportivo con short interior yoga negro  <-  Maxi Vestido Rosie.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-vestido-04.jpg' WHERE id = 31;
-- Vestido deportivo yoga azul marino  <-  Mini Vestido Merly.jpg
UPDATE producto SET imagen_url = '/imagenes/foto-vestido-05.jpg' WHERE id = 32;

-- Sin fotografia disponible; mantienen su boceto:
--   id 149  (falda)  Falda-short con malla interior training gris
--   id 150  (falda)  Falda-short con malla interior training negra
--   id 151  (falda)  Falda deportiva yoga blanca
--   id 152  (short)  Short de entrenamiento básquet gris
--   id 33  (vestido)  Vestido de entrenamiento sin mangas yoga negro
