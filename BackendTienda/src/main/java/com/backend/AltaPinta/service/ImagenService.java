package com.backend.AltaPinta.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class ImagenService {

    private static final String IMAGENES_DIR = "productos-imagenes/";
    private static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final List<String> TIPOS_PERMITIDOS = List.of("image/jpeg", "image/png", "image/webp");

    public String guardar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Selecciona una imagen");
        }
        if (!TIPOS_PERMITIDOS.contains(file.getContentType())) {
            throw new RuntimeException("Formato no permitido, usa JPG, PNG o WEBP");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new RuntimeException("La imagen no debe superar 5MB");
        }

        try {
            File dir = new File(IMAGENES_DIR);
            if (!dir.exists()) dir.mkdirs();

            String extension = switch (file.getContentType()) {
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> ".jpg";
            };

            String nombreArchivo = UUID.randomUUID() + extension;
            Path destino = Path.of(IMAGENES_DIR, nombreArchivo);
            Files.copy(file.getInputStream(), destino);

            // Se devuelve la ruta RELATIVA, no la URL completa.
            //
            // Antes se guardaba "http://localhost:8080/imagenes/..." en la
            // base de datos, lo que ata los datos a la máquina de desarrollo:
            // al desplegar en otro sitio, todas las imágenes del catálogo
            // apuntarían a un servidor que no existe. Ahora el frontend
            // compone la URL con la dirección de su entorno.
            return "/imagenes/" + nombreArchivo;
        } catch (IOException e) {
            throw new RuntimeException("Error guardando la imagen", e);
        }
    }
}
