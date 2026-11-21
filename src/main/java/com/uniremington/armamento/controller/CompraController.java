package com.uniremington.armamento.controller;

import com.uniremington.armamento.dto.CompraFormularioDto;
import com.uniremington.armamento.dto.DetalleCompraFormularioDto;
import com.uniremington.armamento.repository.AlmacenRepository;
import com.uniremington.armamento.repository.ProductoRepository;
import com.uniremington.armamento.repository.ProveedorRepository;
import com.uniremington.armamento.service.CompraService;
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
public class CompraController {
    private final CompraService compraService;
    private final ProveedorRepository proveedorRepository;
    private final AlmacenRepository almacenRepository;
    private final ProductoRepository productoRepository;

    @GetMapping("/compras")
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("compras", compraService.buscar(
                q, PageRequest.of(Math.max(0, page), 10, Sort.by(Sort.Direction.DESC, "fechaCompra"))));
        model.addAttribute("q", q);
        return "compras/lista";
    }

    @GetMapping("/compras/nueva")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String nueva(Model model) {
        cargarCatalogos(model);
        CompraFormularioDto compra = new CompraFormularioDto();
        DetalleCompraFormularioDto detalle = new DetalleCompraFormularioDto();
        detalle.setCantidad(1);
        compra.getDetalles().add(detalle);
        model.addAttribute("compra", compra);
        return "compras/form";
    }

    @PostMapping("/compras")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String crear(
            @Valid @ModelAttribute("compra") CompraFormularioDto compra,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarCatalogos(model);
            if (compra.getDetalles().isEmpty()) {
                DetalleCompraFormularioDto detalle = new DetalleCompraFormularioDto();
                detalle.setCantidad(1);
                compra.getDetalles().add(detalle);
            }
            return "compras/form";
        }
        try {
            var creada = compraService.crear(compra);
            redirectAttributes.addFlashAttribute("exito", "La compra se recibió correctamente.");
            return "redirect:/compras/" + creada.getId();
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("compra.invalida", exception.getMessage());
            cargarCatalogos(model);
            if (compra.getDetalles().isEmpty()) {
                DetalleCompraFormularioDto detalle = new DetalleCompraFormularioDto();
                detalle.setCantidad(1);
                compra.getDetalles().add(detalle);
            }
            return "compras/form";
        }
    }

    @GetMapping("/compras/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("compra", compraService.obtener(id));
        return "compras/detalle";
    }

    @PostMapping("/compras/{id}/anular")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String anular(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        compraService.anular(id);
        redirectAttributes.addFlashAttribute("exito", "La compra se anuló y el inventario fue ajustado.");
        return "redirect:/compras/" + id;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("proveedores", proveedorRepository.findByActivoTrueOrderByRazonSocialAsc());
        model.addAttribute("almacenes", almacenRepository.findByActivoTrueOrderByNombreAsc());
        model.addAttribute("productos", productoRepository.findByActivoTrueOrderByNombreAsc());
    }
}
