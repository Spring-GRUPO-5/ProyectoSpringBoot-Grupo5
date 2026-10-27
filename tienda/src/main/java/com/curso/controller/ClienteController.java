package com.curso.controller;


import com.curso.model.Cliente;
import com.curso.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Listar todos los clientes
    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    // Obtener un cliente por su id
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear un cliente
    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {

        if (cliente.getNombre() == null
                || cliente.getTelefono() == null
                || cliente.getEmail() == null
                || cliente.getFecha_nacimiento() == null) {
            return ResponseEntity.badRequest().build();
        }

        // La fecha de nacimiento debe ser anterior a hoy
        if (!cliente.getFecha_nacimiento().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().build();
        }

        // El email no puede estar repetido
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        // La fecha de alta se pone automáticamente
        if (cliente.getFecha_alta() == null) {
            cliente.setFecha_alta(LocalDateTime.now());
        }

        // Ignoramos el id recibido para crear un cliente nuevo
        cliente.setId(null);

        Cliente guardado = clienteRepository.save(cliente);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // Actualizar un cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(
            @PathVariable Long id,
            @RequestBody Cliente datos) {

        return clienteRepository.findById(id)
                .map(existente -> {

                    if (datos.getNombre() == null
                            || datos.getTelefono() == null
                            || datos.getEmail() == null
                            || datos.getFecha_nacimiento() == null) {
                        return ResponseEntity.badRequest().<Cliente>build();
                    }

                    if (!datos.getFecha_nacimiento().isBefore(LocalDate.now())) {
                        return ResponseEntity.badRequest().<Cliente>build();
                    }

                    if (clienteRepository.existsByEmailAndIdNot(
                            datos.getEmail(), id)) {
                        return ResponseEntity.badRequest().<Cliente>build();
                    }

                    existente.setNombre(datos.getNombre());
                    existente.setApellido(datos.getApellido());
                    existente.setTelefono(datos.getTelefono());
                    existente.setEmail(datos.getEmail());
                    existente.setFecha_nacimiento(datos.getFecha_nacimiento());

                    // No cambiamos la fecha de alta al actualizar

                    return ResponseEntity.ok(clienteRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar un cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        clienteRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

