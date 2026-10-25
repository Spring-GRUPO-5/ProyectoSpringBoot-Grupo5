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
 * S09-1 Demo 1 y Demo 2: Controlador REST de categorías
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
@RestController
// Prefijo común de todas las rutas de la clase: cada método añade su parte al final.
@RequestMapping("/api/categorias")
public class CategoriaController {
    // Referencia al repositorio, que es quien habla con la base de datos.
    private final CategoriaRepository repository;
    // Inyección por constructor (S06-1): Spring crea el repositorio y lo pasa aquí.
    // Con un único constructor no hace falta ninguna anotación.
    public CategoriaController(CategoriaRepository repository) {
        this.repository = repository;
    }
    // @GetMapping sin ruta atiende GET /api/categorias (el prefijo de la clase).
    @GetMapping
    public List<Categoria> listar() {
        // findAll() ejecuta "select * from categoria". Spring convierte la lista en un array JSON.
        // Al devolver un objeto directamente, el estado es 200 OK.
        return repository.findAll();
    }
    // {id} es un trozo variable de la ruta: en /api/categorias/1 vale 1.
    @GetMapping("/{id}")
    // ResponseEntity permite decidir el código de estado además del cuerpo.
    // @PathVariable entrega el valor de {id} como parámetro Long del método.
    public ResponseEntity<Categoria> obtener(@PathVariable Long id) {
        // findById() devuelve un Optional<Categoria>: puede contener la categoría o estar vacío.
        // map(ResponseEntity::ok): si hay categoría, la envuelve en una respuesta 200 OK.
        //   "ResponseEntity::ok" es una forma abreviada de escribir categoria -> ResponseEntity.ok(categoria).
        // orElse(...): si el Optional está vacío, devuelve una respuesta 404 Not Found sin cuerpo.
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una categoría nueva.
     *
     * Ejemplo:
     *   POST /api/categorias  {"nombre":"Congelados"}  →  201 {"id":5,"nombre":"Congelados"}
     *   POST /api/categorias  {"nombre":"Bebidas"}     →  500 (el nombre ya existe)
     */
    @PostMapping
    // 201 Created es el código que indica que se ha creado un recurso nuevo.
    // Sin esta anotación el estado sería el 200 habitual.
    @ResponseStatus(HttpStatus.CREATED)
    // @RequestBody convierte el JSON del cuerpo de la petición en un objeto Categoria.
    public Categoria crear(@RequestBody Categoria categoria) {
        // Ignoramos cualquier id que envíe el cliente para que siempre se cree una fila nueva:
        // con un id existente, save() actualizaría esa fila en lugar de insertar.
        categoria.setId(null);
        // save() con id null ejecuta un insert; PostgreSQL genera el id y save() devuelve
        // la categoría ya con él. Si el nombre ya existe (columna unique) o falta, la base de
        // datos rechaza la inserción y Spring responde 500.
        return repository.save(categoria);
    }

    /**
     * Modifica el nombre de una categoría existente.
     *
     * Ejemplo:
     *   PUT /api/categorias/1    {"nombre":"Refrescos"}  →  200 {"id":1,"nombre":"Refrescos"}
     *   PUT /api/categorias/999  {"nombre":"Refrescos"}  →  404 (sin cuerpo)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @RequestBody Categoria datos) {
        // Buscamos la categoría actual. Si existe, la expresión "existente -> { ... }" copia
        // el nombre nuevo sobre ella y la guarda (save() ejecuta un update porque ya tiene id).
        // Modificamos el objeto que viene de la base de datos y no guardamos "datos" directamente:
        // así controlamos qué campos se pueden cambiar y el id es siempre el de la URL.
        // Si no existe, devolvemos 404.
        return repository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    return ResponseEntity.ok(repository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Borra una categoría.
     *
     * Ejemplo:
     *   DELETE /api/categorias/5    →  204 (sin cuerpo)
     *   DELETE /api/categorias/999  →  404 (sin cuerpo)
     *   DELETE /api/categorias/1    →  500 (tiene productos: la clave foránea impide borrarla)
     */
    @DeleteMapping("/{id}")
    // ResponseEntity<Void>: la respuesta no lleva cuerpo, solo código de estado.
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Primero comprobamos que existe para poder responder 404 si no es así.
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // deleteById() ejecuta "delete from categoria where id = ?".
        repository.deleteById(id);
        // 204 No Content: se ha borrado y no hay nada que devolver.
        return ResponseEntity.noContent().build();
    }
}
