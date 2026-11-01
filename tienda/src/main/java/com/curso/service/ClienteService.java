
package com.curso.service;

import com.curso.model.Cliente;
import com.curso.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Listar todos los clientes
    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    // Buscar un cliente por su ID
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    // Guardar un cliente nuevo o modificar uno existente
    @Transactional
    public Cliente guardar(Cliente datos) {

        validar(datos);

        // Comprobamos que el email no pertenezca a otro cliente
        boolean emailRepetido;

        if (datos.getId() == null) {
            emailRepetido =
                    clienteRepository.existsByEmail(datos.getEmail());
        } else {
            emailRepetido =
                    clienteRepository.existsByEmailAndIdNot(
                            datos.getEmail(), datos.getId());
        }

        if (emailRepetido) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente con ese email.");
        }

        // Alta de un cliente nuevo
        if (datos.getId() == null) {
            datos.setFecha_alta(LocalDateTime.now());
            return clienteRepository.save(datos);
        }

        // Modificación de un cliente existente
        Cliente existente = clienteRepository.findById(datos.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Cliente no encontrado."));

        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());
        existente.setFecha_nacimiento(datos.getFecha_nacimiento());

        // Conservamos la fecha de alta original
        return clienteRepository.save(existente);
    }

    // Eliminar un cliente
    @Transactional
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }

        clienteRepository.deleteById(id);
    }

    // Validaciones comunes para alta y modificación
    private void validar(Cliente cliente) {

        if (cliente.getNombre() == null || cliente.getNombre().isBlank()
                || cliente.getApellido() == null || cliente.getApellido().isBlank()
                || cliente.getTelefono() == null || cliente.getTelefono().isBlank()
                || cliente.getEmail() == null || cliente.getEmail().isBlank()
                || cliente.getFecha_nacimiento() == null) {

            throw new IllegalArgumentException(
                    "Todos los campos obligatorios deben rellenarse.");
        }

        if (!cliente.getFecha_nacimiento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de nacimiento debe ser anterior a hoy.");
        }
    }
}
