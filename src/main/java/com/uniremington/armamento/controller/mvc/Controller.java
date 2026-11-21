package com.uniremington.armamento.controller.mvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

import com.uniremington.armamento.controller.res.ResController;
import com.uniremington.armamento.controller.res.ResController.FieldInfo;
import com.uniremington.armamento.dto.DashboardDto;
import com.uniremington.armamento.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class Controller {
    private final ResController resController;
    private final DashboardService dashboardService;

    public Controller(ResController resController, DashboardService dashboardService) {
        this.resController = resController;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("resources", resController.resourceNames());
        model.addAttribute("dashboard", dashboardService.cargar());
        model.addAttribute("moduleGroups", moduleGroups());
        return "index";
    }

    @GetMapping("/mvc/{resource}")
    public String list(
            @PathVariable String resource,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {
        List<FieldInfo> fields = resController.resourceFields(resource);
        List<FieldInfo> displayFields = fields.stream().filter(FieldInfo::displayable).toList();
        String sortColumn = displayFields.stream().anyMatch(field -> field.name().equals(sort))
                ? sort : displayFields.stream().findFirst().map(FieldInfo::name).orElse("id");
        Comparator<Map<String, String>> comparator = Comparator.comparing(
                row -> row.getOrDefault(sortColumn, ""), String.CASE_INSENSITIVE_ORDER);
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        List<Map<String, String>> allRows = resController.resourceRows(resource).stream()
                .filter(row -> q.isBlank() || displayFields.stream()
                        .map(field -> row.getOrDefault(field.name(), ""))
                        .anyMatch(value -> value.toLowerCase(java.util.Locale.ROOT)
                                .contains(q.trim().toLowerCase(java.util.Locale.ROOT))))
                .sorted(comparator)
                .toList();
        int pageSize = 15;
        int pageCount = Math.max(1, (int) Math.ceil((double) allRows.size() / pageSize));
        int pageIndex = Math.min(Math.max(0, page), pageCount - 1);
        Page<Map<String, String>> resultPage = new PageImpl<>(
                allRows.subList(pageIndex * pageSize, Math.min((pageIndex + 1) * pageSize, allRows.size())),
                PageRequest.of(pageIndex, pageSize), allRows.size());
        model.addAttribute("resource", resource);
        model.addAttribute("fields", displayFields);
        model.addAttribute("rows", resultPage.getContent());
        model.addAttribute("page", resultPage);
        model.addAttribute("q", q);
        model.addAttribute("sort", sortColumn);
        model.addAttribute("direction", direction);
        model.addAttribute("title", title(resource));
        return "resource-list";
    }

    @GetMapping("/mvc/{resource}/{id}")
    public String detail(
            @PathVariable String resource,
            @PathVariable String id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Map<String, String>> values = resController.resourceFormValues(resource, id);
        if (values.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el registro solicitado.");
            return "redirect:/mvc/" + resource;
        }
        model.addAttribute("resource", resource);
        model.addAttribute("values", values.get());
        model.addAttribute("fields", resController.resourceFields(resource).stream()
                .filter(FieldInfo::displayable).toList());
        model.addAttribute("title", title(resource));
        model.addAttribute("id", id);
        return "resource-detail";
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

    private List<ModuleGroup> moduleGroups() {
        List<String> recursos = resController.resourceNames();
        Map<String, String> rutas = Map.of(
                "productos", "/productos",
                "ventas", "/ventas",
                "compras", "/compras",
                "devoluciones", "/devoluciones",
                "usuarios", "/usuarios");
        Map<String, String> iconos = Map.ofEntries(
                Map.entry("almacenes", "fa-warehouse"),
                Map.entry("auditorias", "fa-clipboard-check"),
                Map.entry("categorias", "fa-layer-group"),
                Map.entry("clientes", "fa-user-group"),
                Map.entry("compras", "fa-cart-shopping"),
                Map.entry("detalles-compra", "fa-list-check"),
                Map.entry("detalles-devolucion", "fa-rotate-left"),
                Map.entry("detalles-venta", "fa-receipt"),
                Map.entry("devoluciones", "fa-arrow-rotate-left"),
                Map.entry("existencias", "fa-boxes-stacked"),
                Map.entry("marcas", "fa-tag"),
                Map.entry("movimientos-inventario", "fa-right-left"),
                Map.entry("permisos-cliente", "fa-id-card"),
                Map.entry("productos", "fa-box"),
                Map.entry("productos-proveedor", "fa-link"),
                Map.entry("proveedores", "fa-truck"),
                Map.entry("roles", "fa-shield-halved"),
                Map.entry("unidades-seriadas", "fa-barcode"),
                Map.entry("usuarios", "fa-users-gear"),
                Map.entry("ventas", "fa-cash-register"));
        List<ModuleGroup> groups = new ArrayList<>();
        groups.add(group("Inventario", List.of(
                "almacenes", "existencias", "movimientos-inventario", "unidades-seriadas"),
                recursos, rutas, iconos, "inventario"));
        groups.add(group("Catálogo", List.of("productos", "categorias", "marcas"),
                recursos, rutas, iconos, "catalogo"));
        groups.add(group("Comercial", List.of("clientes", "permisos-cliente", "ventas",
                "detalles-venta", "devoluciones", "detalles-devolucion"),
                recursos, rutas, iconos, "comercial"));
        groups.add(group("Abastecimiento", List.of("proveedores", "productos-proveedor",
                "compras", "detalles-compra"), recursos, rutas, iconos, "abastecimiento"));
        groups.add(group("Seguridad y control", List.of("usuarios", "roles", "auditorias"),
                recursos, rutas, iconos, "seguridad"));
        return groups;
    }

    private ModuleGroup group(
            String titulo,
            List<String> names,
            List<String> recursos,
            Map<String, String> rutas,
            Map<String, String> iconos,
            String color) {
        List<ModuleCard> cards = names.stream()
                .filter(recursos::contains)
                .map(name -> new ModuleCard(
                        name,
                        title(name),
                        rutas.getOrDefault(name, "/mvc/" + name),
                        iconos.getOrDefault(name, "fa-cube"),
                        color,
                        resController.resourceRows(name).size()))
                .toList();
        return new ModuleGroup(titulo, cards);
    }

    public record ModuleGroup(String title, List<ModuleCard> cards) {
    }

    public record ModuleCard(String resource, String title, String href, String icon, String color, int count) {
    }
}
