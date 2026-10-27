package com.curso.controller;

import com.curso.model.Categoria;
import com.curso.repository.CategoriaRepository;
// Importamos las clases de Spring MVC para construir la respuesta HTTP y declarar las rutas.
// Las trae el starter spring-boot-starter-webmvc; Maven las ha descargado sin que las pidamos.
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * Objetivo: exponer las categorías como un recurso de una API REST con las cuatro
 * operaciones de HTTP (GET, POST, PUT y DELETE).
 *
 * Lo que hace este ejemplo:
 *   - Lista las categorías y recupera una por su id (Demo 1)
 *   - Crea, actualiza y borra categorías, con el código de estado adecuado (Demo 2)
 *
 * Concepto clave: la diferencia entre @Controller (S06-1) y @RestController es una
 * anotación. Lo que devuelve cada método ya no es el nombre de una plantilla, sino
 * los datos, que Spring convierte a JSON; el estado HTTP dice qué ha pasado.
 */
// @RestController es un @Controller que, además, convierte lo que devuelven sus métodos
// a JSON (con la librería Jackson) y lo envía como cuerpo de la respuesta.
// Prefijo común de todas las rutas de la clase: cada método añade su parte al final.

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository repository;

    public CategoriaController(CategoriaRepository repository) {
        this.repository = repository;
    }

    // Devuelve todas las categorías
    @GetMapping
    public List<Categoria> listar() {
        return repository.findAll();
    }

    // Devuelve una categoría por su id
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtener(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crea una categoría nueva
    @PostMapping
    public ResponseEntity<Categoria> crear(@RequestBody Categoria categoria) {

        // Comprobamos que el nombre no esté vacío
        if (categoria.getNombre() == null
                || categoria.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Comprobamos que el nombre no exista ya
        if (repository.existsByNombre(categoria.getNombre())) {
            return ResponseEntity.badRequest().build();
        }

        // Ignoramos el id recibido para crear una categoría nueva
        categoria.setId(null);

        Categoria guardada = repository.save(categoria);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    // Modifica el nombre de una categoría
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(
            @PathVariable Long id,
            @RequestBody Categoria datos) {

        // Comprobamos que la categoría exista
        return repository.findById(id)
                .map(existente -> {

                    if (datos.getNombre() == null
                            || datos.getNombre().isBlank()) {
                        return ResponseEntity.badRequest()
                                .<Categoria>build();
                    }

                    // Comprobamos que el nombre no lo tenga otra categoría
                    if (repository.existsByNombreAndIdNot(
                            datos.getNombre(), id)) {
                        return ResponseEntity.badRequest()
                                .<Categoria>build();
                    }

                    existente.setNombre(datos.getNombre());

                    return ResponseEntity.ok(repository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Elimina una categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        // Comprobamos si existe
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Si tiene productos relacionados, la base de datos puede impedir el borrado
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
