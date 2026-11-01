
package com.curso.controller;

import com.curso.model.Proveedor;
import com.curso.service.ProveedorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proveedores")
public class ProveedorWebController {

    private final ProveedorService proveedorService;

    public ProveedorWebController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorService.listar());
        return "proveedores/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("titulo", "Nuevo proveedor");
        return "proveedores/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Model model,
                         RedirectAttributes redirect) {

        return proveedorService.buscarPorId(id)
                .map(proveedor -> {
                    model.addAttribute("proveedor", proveedor);
                    model.addAttribute("titulo", "Modificar proveedor");
                    return "proveedores/formulario";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute(
                            "error", "El proveedor no existe.");
                    return "redirect:/proveedores";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor,
                          RedirectAttributes redirect) {

        try {
            boolean nuevo = proveedor.getId() == null;

            proveedorService.guardar(proveedor);

            redirect.addFlashAttribute(
                    "mensaje",
                    nuevo ? "Proveedor creado correctamente."
                            : "Proveedor actualizado correctamente.");

            return "redirect:/proveedores";

        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            if (proveedor.getId() == null) {
                return "redirect:/proveedores/nuevo";
            }

            return "redirect:/proveedores/editar/" + proveedor.getId();
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirect) {

        try {
            proveedorService.eliminar(id);
            redirect.addFlashAttribute(
                    "mensaje", "Proveedor eliminado correctamente.");

        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/proveedores";
    }
}
