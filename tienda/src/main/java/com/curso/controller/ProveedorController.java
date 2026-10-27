package com.curso.controller;


import com.curso.model.Proveedor;
import com.curso.repository.ProveedorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorRepository proveedorRepository;

    public ProveedorController(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    // Listar todos los proveedores
    @GetMapping
    public List<Proveedor> listar() {
        return proveedorRepository.findAll();
    }

    // Obtener un proveedor por su id
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtener(@PathVariable Long id) {
        return proveedorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear un proveedor
    @PostMapping
    public ResponseEntity<Proveedor> crear(@RequestBody Proveedor proveedor) {

        if (proveedor.getNombre() == null
                || proveedor.getTelefono() == null
                || proveedor.getEmail() == null) {
            return ResponseEntity.badRequest().build();
        }

        // El email no puede estar repetido
        if (proveedorRepository.existsByEmail(proveedor.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        // Ignoramos el id recibido para crear uno nuevo
        proveedor.setId(null);

        Proveedor guardado = proveedorRepository.save(proveedor);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // Actualizar un proveedor
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(
            @PathVariable Long id,
            @RequestBody Proveedor datos) {

        return proveedorRepository.findById(id)
                .map(existente -> {

                    if (datos.getNombre() == null
                            || datos.getTelefono() == null
                            || datos.getEmail() == null) {
                        return ResponseEntity.badRequest().<Proveedor>build();
                    }

                    // Comprobamos que el email no pertenezca a otro proveedor
                    if (proveedorRepository.existsByEmailAndIdNot(
                            datos.getEmail(), id)) {
                        return ResponseEntity.badRequest().<Proveedor>build();
                    }

                    existente.setNombre(datos.getNombre());
                    existente.setApellido(datos.getApellido());
                    existente.setTelefono(datos.getTelefono());
                    existente.setEmail(datos.getEmail());

                    return ResponseEntity.ok(proveedorRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar un proveedor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        if (!proveedorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        proveedorRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

