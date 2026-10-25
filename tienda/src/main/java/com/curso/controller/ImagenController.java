package com.curso.controller;

import com.curso.model.Imagen;
import com.curso.repository.ImagenRepository;
import com.curso.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenRepository imagenRepository;
    private final ProductoRepository productoRepository;

    // Inyección por constructor de ambos repositorios
    public ImagenController(ImagenRepository imagenRepository, ProductoRepository productoRepository) {
        this.imagenRepository = imagenRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Devuelve todas las imágenes registradas.
     *
     * Ejemplo:
     *   GET /api/imagenes  →  200 [{"id":1,"imagenUrl":"pro1/p1ima1.jpg"}, ...]
     */
    @GetMapping
    public List<Imagen> listar() {
        return imagenRepository.findAll();
    }

    /**
     * Devuelve una imagen por su identificador.
     *
     * Ejemplo:
     *   GET /api/imagenes/1    →  200 {"id":1,"imagenUrl":"pro1/p1ima1.jpg"}
     *   GET /api/imagenes/999  →  404 (sin cuerpo)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Imagen> obtener(@PathVariable Long id) {
        return imagenRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Registra una nueva imagen y la asigna a un producto existente.
     *
     * Ejemplo:
     *   POST /api/imagenes  {"imagenUrl":"pro1/p1ima3.jpg","producto":{"id":1}}
     *                                   →  201 {"id":4,"imagenUrl":"pro1/p1ima3.jpg"}
     *   POST /api/imagenes  {"imagenUrl":"sin-producto.jpg"}  →  400 (sin cuerpo)
     */
    @PostMapping
    public ResponseEntity<Imagen> crear(@RequestBody Imagen imagen) {
        // Validación: Si el JSON no especifica el producto o su ID, la petición es errónea (400)
        if (imagen.getProducto() == null || imagen.getProducto().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Buscamos si el producto al que se le quiere asignar la imagen realmente existe
        return productoRepository.findById(imagen.getProducto().getId())
                .map(producto -> {
                    imagen.setId(null); // Aseguramos la creación de un nuevo registro
                    imagen.setProducto(producto); // Vinculamos la entidad completa
                    return ResponseEntity.status(HttpStatus.CREATED).body(imagenRepository.save(imagen));
                })
                // Si el producto especificado no existe, devolvemos 400 Bad Request
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Borra el registro de una imagen.
     * Al borrarla aquí, gracias al 'on delete cascade' de la BD y JPA, no afecta al producto,
     * simplemente este se queda con una imagen menos.
     *
     * Ejemplo:
     *   DELETE /api/imagenes/3   →  204 (sin cuerpo)
     *   DELETE /api/imagenes/999  →  404 (sin cuerpo)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!imagenRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        imagenRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
