
package com.curso.service;

import com.curso.model.Proveedor;
import com.curso.repository.ProveedorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Proveedor> listar() {
        return proveedorRepository.findAll();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Proveedor> buscarPorId(Long id) {
        return proveedorRepository.findById(id);
    }

    @Transactional
    public Proveedor guardar(Proveedor datos) {

        validar(datos);

        boolean emailRepetido;

        if (datos.getId() == null) {
            emailRepetido =
                    proveedorRepository.existsByEmail(datos.getEmail());
        } else {
            emailRepetido =
                    proveedorRepository.existsByEmailAndIdNot(
                            datos.getEmail(), datos.getId());
        }

        if (emailRepetido) {
            throw new IllegalArgumentException(
                    "Ya existe un proveedor con ese email.");
        }

        // Alta
        if (datos.getId() == null) {
            return proveedorRepository.save(datos);
        }

        // Modificación
        Proveedor existente = proveedorRepository.findById(datos.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Proveedor no encontrado."));

        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());

        return proveedorRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {

        if (!proveedorRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "El proveedor no existe.");
        }

        proveedorRepository.deleteById(id);
    }

    private void validar(Proveedor proveedor) {

        if (proveedor.getNombre() == null ||
                proveedor.getNombre().isBlank() ||
                proveedor.getTelefono() == null ||
                proveedor.getTelefono().isBlank() ||
                proveedor.getEmail() == null ||
                proveedor.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Nombre, teléfono y email son obligatorios.");
        }
    }
}

