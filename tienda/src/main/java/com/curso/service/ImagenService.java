package com.curso.service;

import com.curso.model.Imagen;
import com.curso.model.Producto;
import com.curso.repository.ImagenRepository;
import com.curso.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
@Service
public class ImagenService {

    private final ImagenRepository imagenRepository;
    private final ProductoRepository productoRepository;

    @Value("${tienda.imagenes.ruta-absoluta}")
    private String rutaImagenes;

    public ImagenService(
            ImagenRepository imagenRepository,
            ProductoRepository productoRepository) {

        this.imagenRepository = imagenRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Imagen guardar(Long id, Long productoId, MultipartFile archivo)
            throws IOException {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("El producto no existe."));

        Imagen imagen;

        if (id != null) {
            imagen = imagenRepository.findById(id)
                    .orElseThrow(() ->
                            new IllegalArgumentException("La imagen no existe."));
        } else {
            imagen = new Imagen();
        }

        if (archivo != null && !archivo.isEmpty()) {

            String nombreOriginal = Paths.get(
                    archivo.getOriginalFilename()
            ).getFileName().toString();

            String extension = "";

            int punto = nombreOriginal.lastIndexOf('.');

            if (punto >= 0) {
                extension = nombreOriginal.substring(punto)
                        .toLowerCase(Locale.ROOT);
            }

            if (!extension.matches("\\.(png|jpg|jpeg|webp|gif)")) {
                throw new IllegalArgumentException(
                        "Formato no permitido. Usa PNG, JPG, JPEG, WEBP o GIF."
                );
            }

            // Localizamos la carpeta existente del producto.
            Path carpetaBase = Paths.get(rutaImagenes)
                    .toAbsolutePath()
                    .normalize();

            Path carpetaProducto = carpetaBase
                    .resolve("pro" + productoId)
                    .normalize();

            // No creamos carpetas nuevas.
            if (!Files.isDirectory(carpetaProducto)
                    || !carpetaProducto.startsWith(carpetaBase)) {

                throw new IllegalArgumentException(
                        "No existe la carpeta de imágenes del producto: "
                                + carpetaProducto
                );
            }

            Path destino = carpetaProducto
                    .resolve(nombreOriginal)
                    .normalize();

            if (!destino.startsWith(carpetaProducto)) {
                throw new IllegalArgumentException(
                        "Nombre de archivo no válido."
                );
            }

            // Guardamos la imagen en la carpeta externa existente.
            Files.copy(
                    archivo.getInputStream(),
                    destino,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Guardamos solamente la ruta relativa en la base de datos.
            imagen.setImagenUrl(
                    "pro" + productoId + "/" + nombreOriginal
            );
        }

        imagen.setProducto(producto);

        if (imagen.getImagenUrl() == null
                || imagen.getImagenUrl().isBlank()
                || imagen.getImagenUrl().equals("pendiente")) {

            throw new IllegalArgumentException(
                    "Debes seleccionar una imagen."
            );
        }

        return imagenRepository.save(imagen);
    }

    public List<Imagen> listar() {
        return imagenRepository.findAll();
    }

    @Transactional
    public void eliminar(Long id) {

        if (!imagenRepository.existsById(id)) {
            throw new IllegalArgumentException("La imagen no existe.");
        }

        imagenRepository.eliminarDirectamente(id);
    }

    public Optional<Imagen> buscarPorId(Long id) {
        return imagenRepository.findById(id);
    }
}