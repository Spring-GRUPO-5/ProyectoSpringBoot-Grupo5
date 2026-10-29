
package com.curso.controller;

import com.curso.model.Categoria;
import com.curso.model.Producto;
import com.curso.model.Proveedor;
import com.curso.repository.CategoriaRepository;
import com.curso.repository.ProductoRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoRepository productoRepository;

    @MockitoBean
    private CategoriaRepository categoriaRepository;

    private Categoria crearCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Bebidas");
        return categoria;
    }

    private Proveedor crearProveedor() {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Distribuidora");
        return proveedor;
    }

    private Producto crearProducto() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Agua mineral");
        producto.setCantidad(20);
        producto.setPrecio(new BigDecimal("0.60"));
        producto.setCategoria(crearCategoria());
        producto.setProveedor(crearProveedor());
        return producto;
    }

    @Test
    void listarDebeDevolverProductos() throws Exception {
        when(productoRepository.findAll())
                .thenReturn(List.of(crearProducto()));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Agua mineral"))
                .andExpect(jsonPath("$[0].cantidad").value(20))
                .andExpect(jsonPath("$[0].categoria.nombre").value("Bebidas"));
    }

    @Test
    void obtenerProductoExistenteDebeDevolver200() throws Exception {
        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(crearProducto()));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Agua mineral"))
                .andExpect(jsonPath("$.precio").value(0.60));
    }

    @Test
    void obtenerProductoInexistenteDebeDevolver404() throws Exception {
        when(productoRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearSinCategoriaDebeDevolver400() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "nombre": "Agua mineral",
                        "cantidad": 20,
                        "precio": 0.60
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearConCategoriaInexistenteDebeDevolver400() throws Exception {
        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "nombre": "Agua mineral",
                        "cantidad": 20,
                        "precio": 0.60,
                        "categoria": {"id": 99},
                        "proveedor": {"id": 1}
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearProductoConCategoriaExistenteDebeDevolver201()
            throws Exception {

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(crearCategoria()));

        when(productoRepository.save(any(Producto.class)))
                .thenReturn(crearProducto());

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "nombre": "Agua mineral",
                        "cantidad": 20,
                        "precio": 0.60,
                        "categoria": {"id": 1},
                        "proveedor": {"id": 1}
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Agua mineral"));
    }

    @Test
    void actualizarProductoExistenteDebeDevolver200()
            throws Exception {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(crearProducto()));

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(crearCategoria()));

        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "nombre": "Agua mineral grande",
                        "cantidad": 30,
                        "precio": 0.90,
                        "categoria": {"id": 1},
                        "proveedor": {"id": 1}
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Agua mineral grande"))
                .andExpect(jsonPath("$.precio").value(0.90));
    }

    @Test
    void actualizarProductoInexistenteDebeDevolver404()
            throws Exception {

        when(productoRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/productos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "nombre": "Agua mineral",
                        "cantidad": 20,
                        "precio": 0.60,
                        "categoria": {"id": 1},
                        "proveedor": {"id": 1}
                    }
                    """))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarProductoExistenteDebeDevolver204() throws Exception {
        when(productoRepository.existsById(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminarProductoInexistenteDebeDevolver404() throws Exception {
        when(productoRepository.existsById(999L))
                .thenReturn(false);

        mockMvc.perform(delete("/api/productos/999"))
                .andExpect(status().isNotFound());
    }
}
