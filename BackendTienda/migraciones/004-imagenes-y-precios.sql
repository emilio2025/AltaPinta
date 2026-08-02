-- Imagenes y precios del catalogo deportivo de AltaPinta.
-- Generado por migraciones/generar_imagenes.py
-- Las ilustraciones son propias: siluetas SVG, sin material de terceros.

START TRANSACTION;

-- Imagen segun la prenda y el deporte de cada producto
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-camiseta-running.svg' WHERE t.nombre = 'Polo' AND d.nombre = 'Running';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-camiseta-training.svg' WHERE t.nombre = 'Polo' AND d.nombre = 'Training';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-camiseta-futbol.svg' WHERE t.nombre = 'Polo' AND d.nombre = 'Fútbol';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-camiseta-basquet.svg' WHERE t.nombre = 'Polo' AND d.nombre = 'Básquet';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-camiseta-yoga.svg' WHERE t.nombre = 'Polo' AND d.nombre = 'Yoga';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-camiseta-outdoor.svg' WHERE t.nombre = 'Polo' AND d.nombre = 'Outdoor';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-sudadera-running.svg' WHERE t.nombre = 'Polera' AND d.nombre = 'Running';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-sudadera-training.svg' WHERE t.nombre = 'Polera' AND d.nombre = 'Training';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-sudadera-futbol.svg' WHERE t.nombre = 'Polera' AND d.nombre = 'Fútbol';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-sudadera-basquet.svg' WHERE t.nombre = 'Polera' AND d.nombre = 'Básquet';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-sudadera-yoga.svg' WHERE t.nombre = 'Polera' AND d.nombre = 'Yoga';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-sudadera-outdoor.svg' WHERE t.nombre = 'Polera' AND d.nombre = 'Outdoor';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-chaqueta-running.svg' WHERE t.nombre = 'Casaca' AND d.nombre = 'Running';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-chaqueta-training.svg' WHERE t.nombre = 'Casaca' AND d.nombre = 'Training';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-chaqueta-futbol.svg' WHERE t.nombre = 'Casaca' AND d.nombre = 'Fútbol';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-chaqueta-basquet.svg' WHERE t.nombre = 'Casaca' AND d.nombre = 'Básquet';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-chaqueta-yoga.svg' WHERE t.nombre = 'Casaca' AND d.nombre = 'Yoga';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-chaqueta-outdoor.svg' WHERE t.nombre = 'Casaca' AND d.nombre = 'Outdoor';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-media-running.svg' WHERE t.nombre = 'Chompa' AND d.nombre = 'Running';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-media-training.svg' WHERE t.nombre = 'Chompa' AND d.nombre = 'Training';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-media-futbol.svg' WHERE t.nombre = 'Chompa' AND d.nombre = 'Fútbol';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-media-basquet.svg' WHERE t.nombre = 'Chompa' AND d.nombre = 'Básquet';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-media-yoga.svg' WHERE t.nombre = 'Chompa' AND d.nombre = 'Yoga';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-media-outdoor.svg' WHERE t.nombre = 'Chompa' AND d.nombre = 'Outdoor';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-short-running.svg' WHERE t.nombre = 'Short' AND d.nombre = 'Running';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-short-training.svg' WHERE t.nombre = 'Short' AND d.nombre = 'Training';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-short-futbol.svg' WHERE t.nombre = 'Short' AND d.nombre = 'Fútbol';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-short-basquet.svg' WHERE t.nombre = 'Short' AND d.nombre = 'Básquet';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-short-yoga.svg' WHERE t.nombre = 'Short' AND d.nombre = 'Yoga';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-short-outdoor.svg' WHERE t.nombre = 'Short' AND d.nombre = 'Outdoor';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-pantalon-running.svg' WHERE t.nombre = 'Pantalón' AND d.nombre = 'Running';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-pantalon-training.svg' WHERE t.nombre = 'Pantalón' AND d.nombre = 'Training';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-pantalon-futbol.svg' WHERE t.nombre = 'Pantalón' AND d.nombre = 'Fútbol';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-pantalon-basquet.svg' WHERE t.nombre = 'Pantalón' AND d.nombre = 'Básquet';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-pantalon-yoga.svg' WHERE t.nombre = 'Pantalón' AND d.nombre = 'Yoga';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-pantalon-outdoor.svg' WHERE t.nombre = 'Pantalón' AND d.nombre = 'Outdoor';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-vestido-running.svg' WHERE t.nombre = 'Vestido' AND d.nombre = 'Running';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-vestido-training.svg' WHERE t.nombre = 'Vestido' AND d.nombre = 'Training';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-vestido-futbol.svg' WHERE t.nombre = 'Vestido' AND d.nombre = 'Fútbol';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-vestido-basquet.svg' WHERE t.nombre = 'Vestido' AND d.nombre = 'Básquet';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-vestido-yoga.svg' WHERE t.nombre = 'Vestido' AND d.nombre = 'Yoga';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-vestido-outdoor.svg' WHERE t.nombre = 'Vestido' AND d.nombre = 'Outdoor';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-falda-running.svg' WHERE t.nombre = 'Falda' AND d.nombre = 'Running';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-falda-training.svg' WHERE t.nombre = 'Falda' AND d.nombre = 'Training';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-falda-futbol.svg' WHERE t.nombre = 'Falda' AND d.nombre = 'Fútbol';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-falda-basquet.svg' WHERE t.nombre = 'Falda' AND d.nombre = 'Básquet';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-falda-yoga.svg' WHERE t.nombre = 'Falda' AND d.nombre = 'Yoga';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-falda-outdoor.svg' WHERE t.nombre = 'Falda' AND d.nombre = 'Outdoor';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-conjunto-running.svg' WHERE p.tipo_prenda_id IS NULL AND d.nombre = 'Running';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-conjunto-training.svg' WHERE p.tipo_prenda_id IS NULL AND d.nombre = 'Training';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-conjunto-futbol.svg' WHERE p.tipo_prenda_id IS NULL AND d.nombre = 'Fútbol';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-conjunto-basquet.svg' WHERE p.tipo_prenda_id IS NULL AND d.nombre = 'Básquet';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-conjunto-yoga.svg' WHERE p.tipo_prenda_id IS NULL AND d.nombre = 'Yoga';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id JOIN deporte d ON d.id = p.deporte_id SET p.imagen_url = 'http://localhost:8080/imagenes/prenda-conjunto-outdoor.svg' WHERE p.tipo_prenda_id IS NULL AND d.nombre = 'Outdoor';

-- Precio por tipo de prenda. El escalon depende del id, asi que
-- el precio es el mismo cada vez que se ejecuta el script.
--
-- Se redondea a decena y se le resta un centimo para que acabe
-- en .90, como en cualquier tienda: 89.90 y no 87.43.
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id SET p.precio = ROUND((49.90 + (p.id % 8) * 5.71) / 10) * 10 - 0.10 WHERE t.nombre = 'Polo';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id SET p.precio = ROUND((59.90 + (p.id % 8) * 5.71) / 10) * 10 - 0.10 WHERE t.nombre = 'Short';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id SET p.precio = ROUND((99.90 + (p.id % 8) * 8.57) / 10) * 10 - 0.10 WHERE t.nombre = 'Pantalón';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id SET p.precio = ROUND((69.90 + (p.id % 8) * 5.71) / 10) * 10 - 0.10 WHERE t.nombre = 'Falda';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id SET p.precio = ROUND((89.90 + (p.id % 8) * 7.14) / 10) * 10 - 0.10 WHERE t.nombre = 'Vestido';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id SET p.precio = ROUND((129.90 + (p.id % 8) * 8.57) / 10) * 10 - 0.10 WHERE t.nombre = 'Polera';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id SET p.precio = ROUND((149.90 + (p.id % 8) * 8.57) / 10) * 10 - 0.10 WHERE t.nombre = 'Chompa';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id SET p.precio = ROUND((179.90 + (p.id % 8) * 11.43) / 10) * 10 - 0.10 WHERE t.nombre = 'Casaca';
UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id SET p.precio = ROUND((39.90 + (p.id % 8) * 4.29) / 10) * 10 - 0.10 WHERE p.tipo_prenda_id IS NULL;

COMMIT;
