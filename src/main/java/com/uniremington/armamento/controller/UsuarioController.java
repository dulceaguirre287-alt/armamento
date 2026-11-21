package com.uniremington.armamento.controller;

import com.uniremington.armamento.dto.UsuarioFormularioDto;
import com.uniremington.armamento.repository.RolRepository;
import com.uniremington.armamento.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    @GetMapping("/usuarios")
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("usuarios", usuarioService.buscar(
                q, PageRequest.of(Math.max(0, page), 10, Sort.by("apellidos", "nombres"))));
        model.addAttribute("q", q);
        return "usuarios/lista";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevo(Model model) {
        cargarRoles(model);
        model.addAttribute("usuario", new UsuarioFormularioDto());
        model.addAttribute("titulo", "Nuevo usuario");
        return "usuarios/form";
    }

    @PostMapping("/usuarios")
    public String crear(
            @Valid @ModelAttribute("usuario") UsuarioFormularioDto usuario,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarRoles(model);
            model.addAttribute("titulo", "Nuevo usuario");
            return "usuarios/form";
        }
        try {
            usuarioService.crear(usuario);
            redirectAttributes.addFlashAttribute("exito", "El usuario se creó correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("usuario.invalido", exception.getMessage());
            cargarRoles(model);
            model.addAttribute("titulo", "Nuevo usuario");
            return "usuarios/form";
        }
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        cargarRoles(model);
        var vista = usuarioService.obtener(id);
        UsuarioFormularioDto formulario = new UsuarioFormularioDto();
        formulario.setId(vista.getId());
        formulario.setNombres(vista.getNombres());
        formulario.setApellidos(vista.getApellidos());
        formulario.setEmail(vista.getEmail());
        formulario.setUsername(vista.getUsername());
        formulario.setRolId(vista.getRolId());
        formulario.setActivo(vista.isActivo());
        model.addAttribute("usuario", formulario);
        model.addAttribute("titulo", "Editar usuario");
        return "usuarios/form";
    }

    @PostMapping("/usuarios/{id}")
    public String actualizar(
            @PathVariable Integer id,
            @Valid @ModelAttribute("usuario") UsuarioFormularioDto usuario,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarRoles(model);
            model.addAttribute("titulo", "Editar usuario");
            return "usuarios/form";
        }
        try {
            usuarioService.actualizar(id, usuario);
            redirectAttributes.addFlashAttribute("exito", "El usuario se actualizó correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("usuario.invalido", exception.getMessage());
            cargarRoles(model);
            model.addAttribute("titulo", "Editar usuario");
            return "usuarios/form";
        }
    }

    @PostMapping("/usuarios/{id}/desactivar")
    public String desactivar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        usuarioService.desactivar(id);
        redirectAttributes.addFlashAttribute("exito", "El usuario se desactivó correctamente.");
        return "redirect:/usuarios";
    }

    private void cargarRoles(Model model) {
        model.addAttribute("roles", rolRepository.findAll(Sort.by("nombre")));
    }
}
