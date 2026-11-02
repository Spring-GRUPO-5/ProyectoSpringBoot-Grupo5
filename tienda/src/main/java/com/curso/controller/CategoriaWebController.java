
package com.curso.controller;

import com.curso.model.Categoria;
import com.curso.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaWebController {

    private final CategoriaService categoriaService;

    public CategoriaWebController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listar());
        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("titulo", "Nueva categoría");
        return "categorias/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Model model,
                         RedirectAttributes redirect) {

        return categoriaService.buscarPorId(id)
                .map(categoria -> {
                    model.addAttribute("categoria", categoria);
                    model.addAttribute("titulo", "Modificar categoría");
                    return "categorias/formulario";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute(
                            "error", "La categoría no existe.");
                    return "redirect:/categorias";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria,
                          RedirectAttributes redirect) {

        try {
            boolean nuevo = categoria.getId() == null;

            categoriaService.guardar(categoria);

            redirect.addFlashAttribute(
                    "mensaje",
                    nuevo ? "Categoría creada correctamente."
                            : "Categoría actualizada correctamente.");

            return "redirect:/categorias";

        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            if (categoria.getId() == null) {
                return "redirect:/categorias/nuevo";
            }

            return "redirect:/categorias/editar/" + categoria.getId();
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirect) {

        try {
            categoriaService.eliminar(id);

            redirect.addFlashAttribute(
                    "mensaje", "Categoría eliminada correctamente.");

        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/categorias";
    }
}

