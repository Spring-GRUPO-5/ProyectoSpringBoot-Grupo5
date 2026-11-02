package com.curso.service;

import com.curso.model.Imagen;
import com.curso.model.Producto;
import com.curso.repository.ImagenRepository;
import com.curso.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

@Service
public class ImagenService {

    private final ImagenRepository imagenRepository;
    private final ProductoRepository productoRepository;

    // Ya no requerimos inyectar la propiedad física porque no escribiremos en disco
    public ImagenService(
            ImagenRepository imagenRepository,
            ProductoRepository productoRepository) {
        this.imagenRepository = imagenRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Imagen guardar(Long id, Long productoId, MultipartFile archivo) throws IOException {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe."));

        Imagen imagen;

        // Si tiene ID, buscamos el registro existente para reemplazar la URL en base de datos.
        if (id != null) {
            imagen = imagenRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("La imagen no existe."));
        } else {
            // Si no tiene ID, creamos una entidad de imagen nueva.
            imagen = new Imagen();
        }

        if (archivo != null && !archivo.isEmpty()) {

            // Extraemos únicamente el nombre original del archivo elegido (ej: p3ima1.png)
            String nombreOriginal = Path.of(archivo.getOriginalFilename())
                    .getFileName().toString();

            String extension = "";
            int punto = nombreOriginal.lastIndexOf('.');

            if (punto >= 0) {
                extension = nombreOriginal.substring(punto).toLowerCase(Locale.ROOT);
            }

            if (!extension.matches("\\.(png|jpg|jpeg|webp|gif)")) {
                throw new IllegalArgumentException(
                        "Formato no permitido. Usa PNG, JPG, JPEG, WEBP o GIF.");
            }

            // SE ELIMINÓ: Files.createDirectories, Path destino, y archivo.transferTo.
            // NO se escribe nada en el disco local duro. Solo calculamos el texto de la URL.

            // Asignamos la ruta relativa exacta que coincide con tu init.sql (ej: "pro1/p1ima1.png")
            imagen.setImagenUrl("pro" + productoId + "/" + nombreOriginal);
        }

        imagen.setProducto(producto);

        // Si es una inserción nueva en la app y no se seleccionó archivo, lanzamos la excepción
        if (imagen.getImagenUrl() == null || imagen.getImagenUrl().equals("pendiente")) {
            throw new IllegalArgumentException("Debes seleccionar una imagen.");
        }

        // Se guarda el registro en la BD (Si el ID existía, JPA hace UPDATE; si era nulo, hace INSERT)
        return imagenRepository.save(imagen);
    }

    public java.util.List<Imagen> listar() {
        return imagenRepository.findAll();
    }

    @Transactional
    public void eliminar(Long id) throws IOException {
        // Al eliminar, solo quitamos el registro de la Base de Datos
        // No borramos ningún archivo de la carpeta compartida de desarrollo
        int eliminadas = imagenRepository.eliminarDirectamente(id);

        if (eliminadas == 0) {
            throw new IllegalArgumentException(
                    "No se pudo eliminar el registro de la imagen.");
        }
    }

    public Optional<Imagen> buscarPorId(Long id) {
        return imagenRepository.findById(id);
    }
}
