
package com.curso.controller;

import com.curso.model.Imagen;
import com.curso.repository.ProductoRepository;
import com.curso.service.ImagenService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/imagenes")
public class ImagenWebController {

    private final ImagenService imagenService;
    private final ProductoRepository productoRepository;

    public ImagenWebController(
            ImagenService imagenService,
            ProductoRepository productoRepository) {
        this.imagenService = imagenService;
        this.productoRepository = productoRepository;
    }

    // Listar imágenes
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("imagenes", imagenService.listar());
        return "imagenes/lista";
    }

    // Mostrar formulario nuevo
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("imagen", new Imagen());
        model.addAttribute("productos", productoRepository.findAll());
        return "imagenes/formulario";
    }

    // Mostrar formulario para modificar
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirect) {

        Imagen imagen = imagenService.buscarPorId(id).orElse(null);

        if (imagen == null) {
            redirect.addFlashAttribute("error", "Imagen no encontrada.");
            return "redirect:/imagenes";
        }

        model.addAttribute("imagen", imagen);
        model.addAttribute("productos", productoRepository.findAll());
        // NUEVO: Enviamos el tiempo actual para romper la caché en el formulario
        model.addAttribute("cacheBuster", System.currentTimeMillis());

        return "imagenes/formulario";
    }

    @GetMapping("/web-lista")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.web.servlet.ModelAndView mostrarListaWeb() {
        org.springframework.web.servlet.ModelAndView mav = new org.springframework.web.servlet.ModelAndView("productos-lista");

        mav.addObject("productos", productoRepository.findAll());
        // NUEVO: Enviamos el tiempo actual para romper la caché en la lista
        mav.addObject("cacheBuster", System.currentTimeMillis());

        return mav;
    }

    // Guardar o modificar
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Imagen imagen,
            @RequestParam Long productoId,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            RedirectAttributes redirect) {

        try {
            imagenService.guardar(imagen.getId(), productoId, archivo);

            redirect.addFlashAttribute(
                    "mensaje", "Imagen guardada correctamente.");

        } catch (Exception e) {

            redirect.addFlashAttribute("error", e.getMessage());

            if (imagen.getId() != null) {
                return "redirect:/imagenes/editar/" + imagen.getId();
            }

            return "redirect:/imagenes/nuevo";
        }

        return "redirect:/imagenes";
    }

    // Eliminar imagen
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirect) {

        try {
            imagenService.eliminar(id);

            redirect.addFlashAttribute(
                    "mensaje", "Imagen eliminada correctamente.");

        } catch (Exception e) {

            redirect.addFlashAttribute(
                    "error",
                    "No se pudo eliminar la imagen: " + e.getMessage());
        }

        return "redirect:/imagenes";
    }
}
