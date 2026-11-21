package com.uniremington.armamento.controller;

import com.uniremington.armamento.dto.ProductoDto;
import com.uniremington.armamento.exception.RecursoNoEncontradoException;
import com.uniremington.armamento.repository.CategoriaRepository;
import com.uniremington.armamento.repository.MarcaRepository;
import com.uniremington.armamento.service.ProductoService;
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
public class ProductoController {
    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;

    @GetMapping("/productos")
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {
        String sortColumn = switch (sort) {
            case "sku", "precioVenta", "activo" -> sort;
            default -> "nombre";
        };
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        model.addAttribute("productos", productoService.buscar(
                q, PageRequest.of(Math.max(0, page), 10, Sort.by(sortDirection, sortColumn))));
        model.addAttribute("q", q);
        return "productos/lista";
    }

    @GetMapping("/productos/nuevo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String nuevo(Model model) {
        cargarCatalogos(model);
        model.addAttribute("producto", new ProductoDto());
        model.addAttribute("titulo", "Nuevo producto");
        return "productos/form";
    }

    @PostMapping("/productos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String crear(
            @Valid @ModelAttribute("producto") ProductoDto producto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarCatalogos(model);
            model.addAttribute("titulo", "Nuevo producto");
            return "productos/form";
        }
        try {
            productoService.crear(producto);
            redirectAttributes.addFlashAttribute("exito", "El producto se creó correctamente.");
            return "redirect:/productos";
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("sku", "producto.sku.duplicado", exception.getMessage());
            cargarCatalogos(model);
            model.addAttribute("titulo", "Nuevo producto");
            return "productos/form";
        }
    }

    @GetMapping("/productos/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoService.obtener(id));
        return "productos/detalle";
    }

    @GetMapping("/productos/{id}/editar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String editar(@PathVariable Integer id, Model model) {
        cargarCatalogos(model);
        model.addAttribute("producto", productoService.obtener(id));
        model.addAttribute("titulo", "Editar producto");
        return "productos/form";
    }

    @PostMapping("/productos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String actualizar(
            @PathVariable Integer id,
            @Valid @ModelAttribute("producto") ProductoDto producto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarCatalogos(model);
            model.addAttribute("titulo", "Editar producto");
            return "productos/form";
        }
        try {
            productoService.actualizar(id, producto);
            redirectAttributes.addFlashAttribute("exito", "El producto se actualizó correctamente.");
            return "redirect:/productos/" + id;
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("sku", "producto.sku.duplicado", exception.getMessage());
            cargarCatalogos(model);
            model.addAttribute("titulo", "Editar producto");
            return "productos/form";
        }
    }

    @PostMapping("/productos/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String desactivar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        productoService.desactivar(id);
        redirectAttributes.addFlashAttribute("exito", "El producto se desactivó correctamente.");
        return "redirect:/productos";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("categorias", categoriaRepository.findByActivoTrueOrderByNombreAsc());
        model.addAttribute("marcas", marcaRepository.findByActivoTrueOrderByNombreAsc());
    }
}
