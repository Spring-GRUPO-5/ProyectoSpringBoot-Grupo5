
package com.curso.service;

import com.curso.model.Categoria;
import com.curso.repository.CategoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Transactional
    public Categoria guardar(Categoria datos) {

        validar(datos);

        boolean nombreRepetido;

        if (datos.getId() == null) {
            nombreRepetido =
                    categoriaRepository.existsByNombre(datos.getNombre());
        } else {
            nombreRepetido =
                    categoriaRepository.existsByNombreAndIdNot(
                            datos.getNombre(), datos.getId());
        }

        if (nombreRepetido) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría con ese nombre.");
        }

        // Alta de categoría
        if (datos.getId() == null) {
            return categoriaRepository.save(datos);
        }

        // Modificación de categoría existente
        Categoria existente = categoriaRepository.findById(datos.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La categoría no existe."));

        existente.setNombre(datos.getNombre());

        return categoriaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {

        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "La categoría no existe.");
        }

        categoriaRepository.deleteById(id);
    }

    private void validar(Categoria categoria) {

        if (categoria.getNombre() == null ||
                categoria.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de la categoría es obligatorio.");
        }
    }
}
