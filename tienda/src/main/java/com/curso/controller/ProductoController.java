package com.curso.controller;

import com.curso.model.Producto;
import com.curso.repository.CategoriaRepository;
import com.curso.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * S09-1 Demo 3 y Demo 4: Controlador REST de productos
 *
 * Objetivo: exponer los productos como recurso de la API, comprobando que la categoría
 * que llega en el JSON existe.
 *
 * Lo que hace este ejemplo:
 *   - Lista los productos y recupera uno por su id, con su categoría dentro del JSON
 *   - Crea y actualiza productos validando su categoría, y responde 400 si falta o no existe
 *   - Borra productos con 204, o 404 si no existen
 *
 * Concepto clave: el cliente decide el código de estado que recibe según la causa del fallo.
 * 404 es "ese recurso no existe", 400 es "la petición está mal formada" y 500 es un fallo
 * que la API no ha previsto.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;
    // Este controlador necesita dos repositorios: el de productos para guardarlos y el de
    // categorías para comprobar que la categoría recibida existe.
    private final CategoriaRepository categoriaRepository;

    // Spring entrega los dos repositorios por constructor.
    public ProductoController(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Devuelve todos los productos, cada uno con su categoría.
     *
     * Ejemplo:
     *   GET /api/productos  →  200 [{"id":1,"nombre":"Agua mineral 1,5 L","precio":0.60,
     *                                "categoria":{"id":1,"nombre":"Bebidas"}}, ...]
     */
    @GetMapping
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    /**
     * Devuelve un producto por su identificador.
     *
     * Ejemplo:
     *   GET /api/productos/1    →  200 {"id":1,"nombre":"Agua mineral 1,5 L","precio":0.60,
     *                                   "categoria":{"id":1,"nombre":"Bebidas"}}
     *   GET /api/productos/999  →  404 (sin cuerpo)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        // Mismo patrón que en CategoriaController: 200 si existe y 404 si no.
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea un producto nuevo en una categoría existente.
     *
     * Ejemplo:
     *   POST /api/productos  {"nombre":"Pizza","precio":3.50,"categoria":{"id":1}}
     *                                   →  201 {"id":11,"nombre":"Pizza","precio":3.50,
     *                                           "categoria":{"id":1,"nombre":"Bebidas"}}
     *   POST /api/productos  {"nombre":"Sin categoría","precio":1.00}  →  400 (sin cuerpo)
     */
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        // Si el JSON no trae categoría, o la trae sin id, la petición está mal formada: 400 Bad Request.
        // Se comprueba antes de ir a la base de datos para no provocar un error de restricción.
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        // Buscamos la categoría real en la base de datos. El JSON solo trae su id; necesitamos
        // el objeto completo para relacionarlo con el producto y para devolverlo con su nombre.
        return categoriaRepository.findById(producto.getCategoria().getId())
                .map(categoria -> {
                    // Ignoramos el id del cliente para que siempre se cree una fila nueva.
                    producto.setId(null);
                    producto.setCategoria(categoria);
                    // status(CREATED) devuelve 201, y body(...) añade el producto guardado, ya con su id.
                    return ResponseEntity.status(HttpStatus.CREATED).body(productoRepository.save(producto));
                })
                // Si la categoría no existe también es 400: el error está en lo que ha enviado el cliente.
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Modifica un producto existente.
     *
     * Ejemplo:
     *   PUT /api/productos/1    {"nombre":"Pizza cuatro quesos","precio":4.20,"categoria":{"id":1}}
     *                                   →  200 {"id":1,"nombre":"Pizza cuatro quesos","precio":4.20,
     *                                           "categoria":{"id":1,"nombre":"Bebidas"}}
     *   PUT /api/productos/999  {...}   →  404 (sin cuerpo)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto datos) {
        // Igual que al crear: sin categoría válida en el JSON, 400.
        if (datos.getCategoria() == null || datos.getCategoria().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        // Buscamos primero el producto (404 si no existe) y después su nueva categoría (400 si no existe).
        // Las dos búsquedas devuelven un Optional, por eso un map dentro de otro map.
        return productoRepository.findById(id)
                .map(existente -> categoriaRepository.findById(datos.getCategoria().getId())
                        .map(categoria -> {
                            // Copiamos los campos nuevos sobre el producto que viene de la base de datos.
                            existente.setNombre(datos.getNombre());
                            existente.setPrecio(datos.getPrecio());
                            existente.setCategoria(categoria);
                            return ResponseEntity.ok(productoRepository.save(existente));
                        })
                        // <Producto> indica el tipo del cuerpo, que Java no puede deducir aquí por su cuenta.
                        .orElse(ResponseEntity.badRequest().<Producto>build()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Borra un producto.
     *
     * Ejemplo:
     *   DELETE /api/productos/11   →  204 (sin cuerpo)
     *   DELETE /api/productos/999  →  404 (sin cuerpo)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
