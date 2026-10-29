package com.curso.controller;

import com.curso.model.Categoria;
import com.curso.repository.CategoriaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CategoriaRepository repository;
    //Listar Categoriaa
    @Test
    void listarDebeDevolverLasCategorias() throws Exception{
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Informática");

        when(repository.findAll()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre")
                        .value("Informática"));
    }

    // 2. Consultar una categoría existente
    @Test
    void obtenerDebeDevolverLaCategoria() throws Exception {

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Informática");

        when(repository.findById(1L))
                .thenReturn(Optional.of(categoria));

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value("Informática"));
    }

    // 3. Consultar una categoría inexistente
    @Test
    void obtenerInexistenteDebeDevolver404() throws Exception {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categorias/99"))
                .andExpect(status().isNotFound());
    }

    // 4. Crear una categoría
    @Test
    void crearDebeDevolver201() throws Exception {

        Categoria guardada = new Categoria();
        guardada.setId(1L);
        guardada.setNombre("Informática");

        when(repository.existsByNombre("Informática"))
                .thenReturn(false);

        when(repository.save(any(Categoria.class)))
                .thenReturn(guardada);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Informática"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre")
                        .value("Informática"));
    }

    // 5. Crear una categoría con nombre vacío
    @Test
    void crearConNombreVacioDebeDevolver400() throws Exception {

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // 6. Evitar categorías duplicadas
    @Test
    void crearDuplicadaDebeDevolver400() throws Exception {

        when(repository.existsByNombre("Informática"))
                .thenReturn(true);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Informática"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // 7. Actualizar una categoría
    @Test
    void actualizarDebeDevolver200() throws Exception {

        Categoria existente = new Categoria();
        existente.setId(1L);
        existente.setNombre("Informática");

        when(repository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(repository.existsByNombreAndIdNot(
                "Tecnología", 1L))
                .thenReturn(false);

        when(repository.save(any(Categoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Tecnología"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value("Tecnología"));
    }

    // 8. Actualizar una categoría inexistente
    @Test
    void actualizarInexistenteDebeDevolver404() throws Exception {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/categorias/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Tecnología"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    // 9. Eliminar una categoría existente
    @Test
    void eliminarDebeDevolver204() throws Exception {

        when(repository.existsById(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());

        verify(repository).deleteById(1L);
    }

    // 10. Eliminar una categoría inexistente
    @Test
    void eliminarInexistenteDebeDevolver404() throws Exception {

        when(repository.existsById(99L))
                .thenReturn(false);

        mockMvc.perform(delete("/api/categorias/99"))
                .andExpect(status().isNotFound());
    }
}
