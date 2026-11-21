package com.uniremington.armamento.controller;

import com.uniremington.armamento.dto.DevolucionFormularioDto;
import com.uniremington.armamento.dto.DetalleDevolucionFormularioDto;
import com.uniremington.armamento.repository.CompraRepository;
import com.uniremington.armamento.repository.ProductoRepository;
import com.uniremington.armamento.repository.VentaRepository;
import com.uniremington.armamento.service.DevolucionService;
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
public class DevolucionController {
    private final DevolucionService devolucionService;
    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;

    @GetMapping("/devoluciones")
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("devoluciones", devolucionService.buscar(
                q, PageRequest.of(Math.max(0, page), 10, Sort.by(Sort.Direction.DESC, "fechaDevolucion"))));
        model.addAttribute("q", q);
        return "devoluciones/lista";
    }

    @GetMapping("/devoluciones/nueva")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String nueva(Model model) {
        cargarTransacciones(model);
        cargarProductos(model);
        DevolucionFormularioDto devolucion = new DevolucionFormularioDto();
        DetalleDevolucionFormularioDto detalle = new DetalleDevolucionFormularioDto();
        detalle.setCantidad(1);
        devolucion.getDetalles().add(detalle);
        model.addAttribute("devolucion", devolucion);
        return "devoluciones/form";
    }

    @PostMapping("/devoluciones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String crear(
            @Valid @ModelAttribute("devolucion") DevolucionFormularioDto devolucion,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarTransacciones(model);
            cargarProductos(model);
            if (devolucion.getDetalles().isEmpty()) {
                DetalleDevolucionFormularioDto detalle = new DetalleDevolucionFormularioDto();
                detalle.setCantidad(1);
                devolucion.getDetalles().add(detalle);
            }
            return "devoluciones/form";
        }
        try {
            var creada = devolucionService.crear(devolucion);
            redirectAttributes.addFlashAttribute("exito", "La devolución quedó pendiente de aprobación.");
            return "redirect:/devoluciones/" + creada.getId();
        } catch (IllegalArgumentException exception) {
            String mensaje = exception.getMessage();
            bindingResult.reject("devolucion.invalida",
                    mensaje == null ? "La devolución no es válida." : mensaje);
            cargarTransacciones(model);
            cargarProductos(model);
            if (devolucion.getDetalles().isEmpty()) {
                DetalleDevolucionFormularioDto detalle = new DetalleDevolucionFormularioDto();
                detalle.setCantidad(1);
                devolucion.getDetalles().add(detalle);
            }
            return "devoluciones/form";
        }
    }

    @GetMapping("/devoluciones/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("devolucion", devolucionService.obtener(id));
        return "devoluciones/detalle";
    }

    @PostMapping("/devoluciones/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String aprobar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        devolucionService.aprobar(id);
        redirectAttributes.addFlashAttribute("exito", "La devolución fue aprobada y el inventario actualizado.");
        return "redirect:/devoluciones/" + id;
    }

    @PostMapping("/devoluciones/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENISTA')")
    public String rechazar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        devolucionService.rechazar(id);
        redirectAttributes.addFlashAttribute("exito", "La devolución fue rechazada.");
        return "redirect:/devoluciones/" + id;
    }

    private void cargarTransacciones(Model model) {
        model.addAttribute("ventas", ventaRepository.findAllByOrderByFechaVentaDesc());
        model.addAttribute("compras", compraRepository.findAllByOrderByFechaCompraDesc());
    }

    private void cargarProductos(Model model) {
        model.addAttribute("productos", productoRepository.findByActivoTrueOrderByNombreAsc());
    }
}
