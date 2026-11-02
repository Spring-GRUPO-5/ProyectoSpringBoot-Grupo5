
package com.curso.controller;

import com.curso.model.Producto;
import com.curso.service.ProductoService;
import com.curso.repository.CategoriaRepository;
import com.curso.repository.ProveedorRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoWebController {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;

    public ProductoWebController(
            ProductoService productoService,
            CategoriaRepository categoriaRepository,
            ProveedorRepository proveedorRepository) {

        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    // Listar productos
    @GetMapping
    public String listar(Model model) {

        model.addAttribute("productos", productoService.listar());

        return "productos/lista";
    }

    // Mostrar formulario nuevo
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("producto", new Producto());

        cargarDesplegables(model);

        return "productos/formulario";
    }

    // Mostrar formulario modificar
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirect) {

        try {
            model.addAttribute("producto", productoService.buscarPorId(id));

            cargarDesplegables(model);

            return "productos/formulario";

        } catch (Exception e) {

            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/productos";
        }
    }

    // Guardar o modificar
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Producto producto,
            @RequestParam Long categoriaId,
            @RequestParam Long proveedorId,
            RedirectAttributes redirect) {

        try {
            productoService.guardar(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getCantidad(),
                    producto.getPrecio(),
                    categoriaId,
                    proveedorId
            );

            redirect.addFlashAttribute(
                    "mensaje", "Producto guardado correctamente.");

            return "redirect:/productos";

        } catch (Exception e) {

            redirect.addFlashAttribute("error", e.getMessage());

            if (producto.getId() != null) {
                return "redirect:/productos/editar/" + producto.getId();
            }

            return "redirect:/productos/nuevo";
        }
    }

    // Eliminar producto
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirect) {

        try {
            productoService.eliminar(id);

            redirect.addFlashAttribute(
                    "mensaje", "Producto eliminado correctamente.");

        } catch (Exception e) {

            redirect.addFlashAttribute(
                    "error", "No se pudo eliminar el producto: " + e.getMessage());
        }

        return "redirect:/productos";
    }

    // Cargar categorías y proveedores
    private void cargarDesplegables(Model model) {

        model.addAttribute("categorias", categoriaRepository.findAll());

        model.addAttribute("proveedores", proveedorRepository.findAll());
    }
}
