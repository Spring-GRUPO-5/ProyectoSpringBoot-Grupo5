
package com.curso.controller;

import com.curso.model.Cliente;
import com.curso.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteWebController {

    private final ClienteService clienteService;

    public ClienteWebController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Mostrar listado de clientes
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "clientes/lista";
    }

    // Mostrar formulario para crear un cliente
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Nuevo cliente");
        return "clientes/formulario";
    }

    // Mostrar el mismo formulario para modificar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Model model,
                         RedirectAttributes redirect) {

        return clienteService.buscarPorId(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("titulo", "Modificar cliente");
                    return "clientes/formulario";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute(
                            "error", "El cliente no existe.");
                    return "redirect:/clientes";
                });
    }

    // Guardar un cliente nuevo o modificado
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente,
                          RedirectAttributes redirect) {
        System.out.println("ID recibido: " + cliente.getId());
        System.out.println("Nombre recibido: " + cliente.getNombre());
        System.out.println("Apellido recibido: " + cliente.getApellido());

        try {
            boolean nuevo = cliente.getId() == null;

            clienteService.guardar(cliente);

            redirect.addFlashAttribute("mensaje",
                    nuevo ? "Cliente creado correctamente."
                            : "Cliente actualizado correctamente.");

            return "redirect:/clientes";

        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            if (cliente.getId() == null) {
                return "redirect:/clientes/nuevo";
            }

            return "redirect:/clientes/editar/" + cliente.getId();
        }
    }

    // Eliminar un cliente
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirect) {

        try {
            clienteService.eliminar(id);
            redirect.addFlashAttribute(
                    "mensaje", "Cliente eliminado correctamente.");

        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/clientes";
    }
}
