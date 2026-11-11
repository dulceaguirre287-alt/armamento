package com.uniremington.armamento.controller.mvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.uniremington.armamento.controller.res.ResController;
import com.uniremington.armamento.controller.res.ResController.FieldInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class Controller {
    private final ResController resController;

    public Controller(ResController resController) {
        this.resController = resController;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("resources", resController.resourceNames());
        return "index";
    }

    @GetMapping("/mvc/{resource}")
    public String list(@PathVariable String resource, Model model) {
        List<FieldInfo> fields = resController.resourceFields(resource);
        model.addAttribute("resource", resource);
        model.addAttribute("fields", fields.stream().filter(FieldInfo::displayable).toList());
        model.addAttribute("rows", resController.resourceRows(resource));
        model.addAttribute("title", title(resource));
        return "resource-list";
    }

    @GetMapping("/mvc/{resource}/nuevo")
    public String createForm(@PathVariable String resource, Model model) {
        populateForm(resource, Map.of(), false, null, model);
        return "resource-form";
    }

    @GetMapping("/mvc/{resource}/{id}/editar")
    public String editForm(
            @PathVariable String resource,
            @PathVariable String id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Map<String, String>> values = resController.resourceFormValues(resource, id);
        if (values.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el registro solicitado.");
            return "redirect:/mvc/" + resource;
        }
        populateForm(resource, values.get(), true, id, model);
        return "resource-form";
    }

    @PostMapping("/mvc/{resource}/guardar")
    public String create(
            @PathVariable String resource,
            @RequestParam Map<String, String> form,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            resController.create(resource, resController.formBody(resource, form));
            redirectAttributes.addFlashAttribute("success", "Registro creado correctamente.");
            return "redirect:/mvc/" + resource;
        } catch (ResponseStatusException exception) {
            populateForm(resource, form, false, null, model);
            model.addAttribute("error", exception.getReason());
            return "resource-form";
        }
    }

    @PostMapping("/mvc/{resource}/{id}/actualizar")
    public String update(
            @PathVariable String resource,
            @PathVariable String id,
            @RequestParam Map<String, String> form,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<Object> response = resController.update(
                    resource, id, resController.formBody(resource, form));
            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("success", "Registro actualizado correctamente.");
                return "redirect:/mvc/" + resource;
            }
            redirectAttributes.addFlashAttribute("error", "No se encontró el registro solicitado.");
            return "redirect:/mvc/" + resource;
        } catch (ResponseStatusException exception) {
            populateForm(resource, form, true, id, model);
            model.addAttribute("error", exception.getReason());
            return "resource-form";
        }
    }

    @PostMapping("/mvc/{resource}/{id}/eliminar")
    public String delete(
            @PathVariable String resource,
            @PathVariable String id,
            RedirectAttributes redirectAttributes) {
        ResponseEntity<Void> response = resController.delete(resource, id);
        if (response.getStatusCode().is2xxSuccessful()) {
            redirectAttributes.addFlashAttribute("success", "Registro eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se encontró el registro solicitado.");
        }
        return "redirect:/mvc/" + resource;
    }

    private void populateForm(
            String resource,
            Map<String, String> values,
            boolean editing,
            String id,
            Model model) {
        model.addAttribute("resource", resource);
        model.addAttribute("fields", resController.resourceFields(resource));
        model.addAttribute("values", values);
        model.addAttribute("editing", editing);
        model.addAttribute("title", title(resource));
        model.addAttribute("id", id);
        model.addAttribute("action", editing
                ? "/mvc/" + resource + "/" + id + "/actualizar"
                : "/mvc/" + resource + "/guardar");
    }

    private static String title(String resource) {
        String title = resource.replace('-', ' ');
        return Character.toUpperCase(title.charAt(0)) + title.substring(1);
    }
}
