
package com.curso.controller;

import com.curso.model.Proveedor;
import com.curso.repository.ProveedorRepository;

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

@WebMvcTest(ProveedorController.class)
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProveedorRepository proveedorRepository;

    // 1. Listar proveedores
    @Test
    void listarDebeDevolverProveedores() throws Exception {

        Proveedor proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Distribuidora");
        proveedor.setApellido("García");
        proveedor.setTelefono(String.valueOf(612345678));
        proveedor.setEmail("proveedor@ejemplo.com");

        when(proveedorRepository.findAll())
                .thenReturn(List.of(proveedor));

        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre")
                        .value("Distribuidora"))
                .andExpect(jsonPath("$[0].email")
                        .value("proveedor@ejemplo.com"));
    }

    // 2. Obtener un proveedor existente
    @Test
    void obtenerDebeDevolverProveedor() throws Exception {

        Proveedor proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Distribuidora");
        proveedor.setEmail("proveedor@ejemplo.com");

        when(proveedorRepository.findById(1L))
                .thenReturn(Optional.of(proveedor));

        mockMvc.perform(get("/api/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value("Distribuidora"));
    }

    // 3. Obtener un proveedor inexistente
    @Test
    void obtenerInexistenteDebeDevolver404() throws Exception {

        when(proveedorRepository.findById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/proveedores/99"))
                .andExpect(status().isNotFound());
    }

    // 4. Crear un proveedor
    @Test
    void crearDebeDevolver201() throws Exception {

        Proveedor guardado = new Proveedor();
        guardado.setId(1L);
        guardado.setNombre("Distribuidora");
        guardado.setApellido("García");
        guardado.setTelefono("612345678");
        guardado.setEmail("proveedor@ejemplo.com");

        when(proveedorRepository.existsByEmail(
                "proveedor@ejemplo.com"))
                .thenReturn(false);

        when(proveedorRepository.save(any(Proveedor.class)))
                .thenReturn(guardado);

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Distribuidora",
                                    "apellido": "García",
                                    "telefono": 612345678,
                                    "email": "proveedor@ejemplo.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre")
                        .value("Distribuidora"))
                .andExpect(jsonPath("$.email")
                        .value("proveedor@ejemplo.com"));
    }

    // 5. Crear un proveedor sin nombre
    @Test
    void crearSinNombreDebeDevolver400() throws Exception {

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "",
                                    "apellido": "García",
                                    "telefono": 612345678,
                                    "email": "proveedor@ejemplo.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // 6. Crear un proveedor con email duplicado
    @Test
    void crearEmailDuplicadoDebeDevolver400() throws Exception {

        when(proveedorRepository.existsByEmail(
                "proveedor@ejemplo.com"))
                .thenReturn(true);

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Distribuidora",
                                    "apellido": "García",
                                    "telefono": 612345678,
                                    "email": "proveedor@ejemplo.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // 7. Actualizar un proveedor existente
    @Test
    void actualizarDebeDevolver200() throws Exception {

        Proveedor existente = new Proveedor();
        existente.setId(1L);
        existente.setNombre("Distribuidora");
        existente.setApellido("García");
        existente.setTelefono("612345678");
        existente.setEmail("proveedor@ejemplo.com");

        when(proveedorRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(proveedorRepository.existsByEmailAndIdNot(
                "nuevo@ejemplo.com", 1L))
                .thenReturn(false);

        when(proveedorRepository.save(any(Proveedor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/proveedores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Nuevo proveedor",
                                    "apellido": "López",
                                    "telefono": 611111111,
                                    "email": "nuevo@ejemplo.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value("Nuevo proveedor"))
                .andExpect(jsonPath("$.email")
                        .value("nuevo@ejemplo.com"));
    }

    // 8. Actualizar un proveedor inexistente
    @Test
    void actualizarInexistenteDebeDevolver404() throws Exception {

        when(proveedorRepository.findById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/proveedores/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Nuevo proveedor",
                                    "apellido": "López",
                                    "telefono": 611111111,
                                    "email": "nuevo@ejemplo.com"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    // 9. Eliminar un proveedor existente
    @Test
    void eliminarDebeDevolver204() throws Exception {

        when(proveedorRepository.existsById(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/proveedores/1"))
                .andExpect(status().isNoContent());

        verify(proveedorRepository).deleteById(1L);
    }

    // 10. Eliminar un proveedor inexistente
    @Test
    void eliminarInexistenteDebeDevolver404() throws Exception {

        when(proveedorRepository.existsById(99L))
                .thenReturn(false);

        mockMvc.perform(delete("/api/proveedores/99"))
                .andExpect(status().isNotFound());
    }
}
