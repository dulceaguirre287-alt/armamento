package com.uniremington.armamento.controller;

import com.uniremington.armamento.dto.VentaFormularioDto;
import com.uniremington.armamento.dto.DetalleVentaFormularioDto;
import com.uniremington.armamento.repository.AlmacenRepository;
import com.uniremington.armamento.repository.ClienteRepository;
import com.uniremington.armamento.repository.ProductoRepository;
import com.uniremington.armamento.service.VentaService;
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
public class VentaController {
    private final VentaService ventaService;
    private final ClienteRepository clienteRepository;
    private final AlmacenRepository almacenRepository;
    private final ProductoRepository productoRepository;

    @GetMapping("/ventas")
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("ventas", ventaService.buscar(
                q, PageRequest.of(Math.max(0, page), 10, Sort.by(Sort.Direction.DESC, "fechaVenta"))));
        model.addAttribute("q", q);
        return "ventas/lista";
    }

    @GetMapping("/ventas/nueva")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public String nueva(Model model) {
        cargarCatalogos(model);
        VentaFormularioDto venta = new VentaFormularioDto();
        DetalleVentaFormularioDto detalle = new DetalleVentaFormularioDto();
        detalle.setCantidad(1);
        venta.getDetalles().add(detalle);
        model.addAttribute("venta", venta);
        return "ventas/form";
    }

    @PostMapping("/ventas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public String crear(
            @Valid @ModelAttribute("venta") VentaFormularioDto venta,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarCatalogos(model);
            if (venta.getDetalles().isEmpty()) {
                DetalleVentaFormularioDto detalle = new DetalleVentaFormularioDto();
                detalle.setCantidad(1);
                venta.getDetalles().add(detalle);
            }
            return "ventas/form";
        }
        try {
            var creada = ventaService.crear(venta);
            redirectAttributes.addFlashAttribute("exito", "La venta se registró correctamente.");
            return "redirect:/ventas/" + creada.getId();
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("venta.invalida", exception.getMessage());
            cargarCatalogos(model);
            if (venta.getDetalles().isEmpty()) {
                DetalleVentaFormularioDto detalle = new DetalleVentaFormularioDto();
                detalle.setCantidad(1);
                venta.getDetalles().add(detalle);
            }
            return "ventas/form";
        }
    }

    @GetMapping("/ventas/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("venta", ventaService.obtener(id));
        return "ventas/detalle";
    }

    @PostMapping("/ventas/{id}/anular")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String anular(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ventaService.anular(id);
        redirectAttributes.addFlashAttribute("exito", "La venta se anuló y el inventario fue restaurado.");
        return "redirect:/ventas/" + id;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("clientes", clienteRepository.findByActivoTrueOrderByNombreRazonAsc());
        model.addAttribute("almacenes", almacenRepository.findByActivoTrueOrderByNombreAsc());
        model.addAttribute("productos", productoRepository.findByActivoTrueOrderByNombreAsc());
    }
}
