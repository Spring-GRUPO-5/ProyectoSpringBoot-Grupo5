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

    public ImagenController(ImagenRepository imagenRepository,
                            ProductoRepository productoRepository) {
        this.imagenRepository = imagenRepository;
        this.productoRepository = productoRepository;
    }

    // Devuelve todas las imágenes
    @GetMapping
    public List<Imagen> listar() {
        return imagenRepository.findAll();
    }

    // Devuelve una imagen por su id
    @GetMapping("/{id}")
    public ResponseEntity<Imagen> obtener(@PathVariable Long id) {
        return imagenRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crea una imagen y la asigna a un producto existente
    @PostMapping
    public ResponseEntity<Imagen> crear(@RequestBody Imagen imagen) {

        // Comprobamos que venga la URL y el producto con su id
        if (imagen.getImagenUrl() == null
                || imagen.getProducto() == null
                || imagen.getProducto().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Buscamos el producto en la base de datos
        return productoRepository.findById(imagen.getProducto().getId())
                .map(producto -> {
                    imagen.setId(null);
                    imagen.setProducto(producto);

                    return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(imagenRepository.save(imagen));
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    // Modifica una imagen existente
    @PutMapping("/{id}")
    public ResponseEntity<Imagen> actualizar(
            @PathVariable Long id,
            @RequestBody Imagen datos) {

        // Comprobamos que venga la URL y el producto con su id
        if (datos.getImagenUrl() == null
                || datos.getProducto() == null
                || datos.getProducto().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Buscamos primero la imagen que queremos modificar
        return imagenRepository.findById(id)
                .map(existente ->

                        // Comprobamos que el producto exista
                        productoRepository.findById(
                                        datos.getProducto().getId())
                                .map(producto -> {

                                    // Actualizamos los datos de la imagen
                                    existente.setImagenUrl(datos.getImagenUrl());
                                    existente.setProducto(producto);

                                    return ResponseEntity.ok(
                                            imagenRepository.save(existente));
                                })

                                // El producto indicado no existe
                                .orElse(ResponseEntity.badRequest()
                                        .<Imagen>build())
                )

                // La imagen que queremos modificar no existe
                .orElse(ResponseEntity.notFound().build());
    }

    // Elimina una imagen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        // Comprobamos si existe la imagen
        if (!imagenRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Borramos solo la imagen
        imagenRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
