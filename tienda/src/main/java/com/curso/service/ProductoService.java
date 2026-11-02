
package com.curso.service;

import com.curso.model.Producto;
import com.curso.model.Categoria;
import com.curso.model.Proveedor;

import com.curso.repository.ProductoRepository;
import com.curso.repository.CategoriaRepository;
import com.curso.repository.ProveedorRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            ProveedorRepository proveedorRepository) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    // Listar productos
    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    // Buscar producto por ID
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Producto no encontrado."));
    }

    // Guardar o modificar producto
    @Transactional
    public Producto guardar(
            Long id,
            String nombre,
            int cantidad,
            java.math.BigDecimal precio,
            Long categoriaId,
            Long proveedorId) {

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() ->
                        new IllegalArgumentException("La categoría no existe."));

        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("El proveedor no existe."));

        Producto producto;

        if (id != null) {
            producto = buscarPorId(id);
        } else {
            producto = new Producto();
        }

        producto.setNombre(nombre);
        producto.setCantidad(cantidad);
        producto.setPrecio(precio);
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);

        return productoRepository.save(producto);
    }

    // Eliminar producto
    @Transactional
    public void eliminar(Long id) {

        Producto producto = buscarPorId(id);

        // CascadeType.ALL elimina también los registros de Imagen asociados.
        // Los archivos físicos de tienda-imagenes se conservan.
        productoRepository.delete(producto);
    }
}
