package com.uniremington.armamento.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Locale;

import com.uniremington.armamento.dto.DetalleVentaFormularioDto;
import com.uniremington.armamento.dto.VentaFormularioDto;
import com.uniremington.armamento.dto.VentaVistaDto;
import com.uniremington.armamento.entity.Almacen;
import com.uniremington.armamento.entity.Cliente;
import com.uniremington.armamento.entity.DetalleVenta;
import com.uniremington.armamento.entity.Existencia;
import com.uniremington.armamento.entity.MovimientoInventario;
import com.uniremington.armamento.entity.PermisoCliente;
import com.uniremington.armamento.entity.Producto;
import com.uniremington.armamento.entity.UnidadSeriada;
import com.uniremington.armamento.entity.Usuario;
import com.uniremington.armamento.entity.Venta;
import com.uniremington.armamento.exception.RecursoNoEncontradoException;
import com.uniremington.armamento.mapper.VentaMapper;
import com.uniremington.armamento.repository.AlmacenRepository;
import com.uniremington.armamento.repository.ClienteRepository;
import com.uniremington.armamento.repository.DetalleVentaRepository;
import com.uniremington.armamento.repository.DevolucionRepository;
import com.uniremington.armamento.repository.ExistenciaRepository;
import com.uniremington.armamento.repository.MovimientoInventarioRepository;
import com.uniremington.armamento.repository.PermisoClienteRepository;
import com.uniremington.armamento.repository.ProductoRepository;
import com.uniremington.armamento.repository.UnidadSeriadaRepository;
import com.uniremington.armamento.repository.UsuarioRepository;
import com.uniremington.armamento.repository.VentaRepository;
import com.uniremington.armamento.service.AuditoriaService;
import com.uniremington.armamento.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {
    private static final BigDecimal CIEN = new BigDecimal("100");

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final AlmacenRepository almacenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PermisoClienteRepository permisoRepository;
    private final ExistenciaRepository existenciaRepository;
    private final DevolucionRepository devolucionRepository;
    private final UnidadSeriadaRepository unidadSeriadaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final VentaMapper ventaMapper;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional(readOnly = true)
    public Page<VentaVistaDto> buscar(String consulta, Pageable pageable) {
        String termino = consulta == null ? "" : consulta.trim();
        Page<Venta> ventas = termino.isEmpty()
                ? ventaRepository.findAll(pageable)
                : ventaRepository.findByNumeroVentaContainingIgnoreCase(termino, pageable);
        return ventas.map(ventaMapper::aVista);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaVistaDto obtener(Long id) {
        return ventaMapper.aVista(ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", id)));
    }

    @Override
    @Transactional
    public VentaVistaDto crear(VentaFormularioDto dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", dto.getClienteId()));
        Almacen almacen = almacenRepository.findById(dto.getAlmacenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén", dto.getAlmacenId()));
        Usuario usuario = usuarioActual();
        if (!cliente.isActivo() || !almacen.isActivo()) {
            throw new IllegalArgumentException("El cliente y el almacén deben estar activos.");
        }
        validarMetodoPago(dto.getMetodoPago());

        Venta venta = new Venta();
        venta.setNumeroVenta("VTA-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        venta.setCliente(cliente);
        venta.setAlmacen(almacen);
        venta.setUsuario(usuario);
        venta.setMetodoPago(dto.getMetodoPago().trim().toUpperCase(Locale.ROOT));
        venta.setEstado("PAGADA");
        venta.setObservaciones(dto.getObservaciones());

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        Map<Integer, PermisoCliente> permisos = new LinkedHashMap<>();
        ventaRepository.saveAndFlush(venta);

        for (DetalleVentaFormularioDto item : dto.getDetalles()) {
            if (item.getProductoId() == null || item.getCantidad() <= 0 || item.getCantidad() > 10000) {
                throw new IllegalArgumentException("La cantidad de cada producto debe estar entre 1 y 10.000.");
            }
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", item.getProductoId()));
            if (!producto.isActivo()) {
                throw new IllegalArgumentException("El producto " + producto.getSku() + " está inactivo.");
            }
            String lote = resolverLote(producto, item.getLote());
            if (producto.getCategoria().isRequierePermiso()) {
                PermisoCliente permiso = permisoRepository
                        .findByClienteIdAndCategoriaIdAndEstadoAndFechaExpedicionLessThanEqualAndFechaVencimientoGreaterThanEqual(
                                cliente.getId(), producto.getCategoria().getId(), "VIGENTE",
                                LocalDate.now(), LocalDate.now())
                        .stream().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "El cliente no tiene un permiso vigente para la categoría "
                                        + producto.getCategoria().getNombre() + "."));
                permisos.put(permiso.getId(), permiso);
            }

            Existencia existencia = existenciaRepository.buscarYBloquear(producto, almacen.getId(), lote)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No hay existencias del producto " + producto.getSku() + " en el almacén seleccionado."));
            if (existencia.getCantidad() < item.getCantidad()) {
                throw new IllegalArgumentException("Existencia insuficiente para el producto " + producto.getSku()
                        + ". Disponible: " + existencia.getCantidad() + ".");
            }

            BigDecimal lineaSubtotal = producto.getPrecioVenta()
                    .multiply(BigDecimal.valueOf(item.getCantidad())).setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineaIva = lineaSubtotal.multiply(producto.getPorcentajeIva())
                    .divide(CIEN, 2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(lineaSubtotal);
            iva = iva.add(lineaIva);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta());
            detalle.setDescuento(BigDecimal.ZERO);
            detalle.setPorcentajeIva(producto.getPorcentajeIva());
            detalle.setLote(producto.isControlaLote() ? lote : null);
            detalle.setSubtotal(lineaSubtotal);
            detalle = detalleVentaRepository.saveAndFlush(detalle);
            venta.getDetalles().add(detalle);

            if (producto.isControlaSerial()) {
                descontarUnidadesSeriales(producto, almacen, detalle);
            }
            existencia.setCantidad(existencia.getCantidad() - item.getCantidad());
            existenciaRepository.save(existencia);
            guardarMovimiento(producto, almacen, usuario, lote, detalle, existencia.getCantidad(),
                    -item.getCantidad(), producto.getPrecioCompraRef(), "VENTA");
        }

        venta.setSubtotal(subtotal);
        venta.setIva(iva);
        venta.setDescuento(BigDecimal.ZERO);
        venta.setTotal(subtotal.add(iva));
        venta.setPermisos(new ArrayList<>(permisos.values()));
        if (!permisos.isEmpty()) {
            venta.setPermiso(permisos.values().iterator().next());
        }
        Venta guardada = ventaRepository.save(venta);
        VentaVistaDto resultado = ventaMapper.aVista(guardada);
        auditoriaService.registrar("CREAR", "venta", guardada.getId(), null, resultado);
        return resultado;
    }

    @Override
    @Transactional
    public void anular(Long id) {
        Venta venta = ventaRepository.buscarYBloquear(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", id));
        if ("ANULADA".equals(venta.getEstado())) {
            throw new IllegalArgumentException("La venta ya se encuentra anulada.");
        }
        if (devolucionRepository.existsByVenta_IdAndEstadoNot(id, "RECHAZADA")) {
            throw new IllegalArgumentException(
                    "No se puede anular la venta mientras tenga devoluciones pendientes o aprobadas.");
        }
        VentaVistaDto antes = ventaMapper.aVista(venta);
        Usuario usuario = usuarioActual();

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            String lote = detalle.getLote() == null ? "" : detalle.getLote();
            Existencia existencia = existenciaRepository.buscarYBloquear(
                    producto, venta.getAlmacen().getId(), lote)
                    .orElseThrow(() -> new IllegalStateException(
                            "No se encontró la existencia requerida para anular la venta."));
            existencia.setCantidad(existencia.getCantidad() + detalle.getCantidad());
            existenciaRepository.save(existencia);

            if (producto.isControlaSerial()) {
                List<UnidadSeriada> unidades = unidadSeriadaRepository.findByDetalleVenta(detalle);
                if (unidades.size() != detalle.getCantidad()
                        || unidades.stream().anyMatch(unidad -> !"VENDIDA".equals(unidad.getEstado()))) {
                    throw new IllegalStateException(
                            "Las unidades seriadas asociadas no están todas en estado vendido.");
                }
                for (UnidadSeriada unidad : unidades) {
                    unidad.setEstado("DISPONIBLE");
                    unidad.setFechaSalida(null);
                    unidad.setDetalleVenta(null);
                }
            }
            guardarMovimiento(producto, venta.getAlmacen(), usuario, lote, detalle, existencia.getCantidad(),
                    detalle.getCantidad(), producto.getPrecioCompraRef(), "ANULACION_VENTA");
        }

        venta.setEstado("ANULADA");
        VentaVistaDto despues = ventaMapper.aVista(ventaRepository.save(venta));
        auditoriaService.registrar("ANULAR", "venta", id, antes, despues);
    }

    private void descontarUnidadesSeriales(Producto producto, Almacen almacen, DetalleVenta detalle) {
        List<UnidadSeriada> unidades = unidadSeriadaRepository.buscarDisponibles(
                producto, almacen.getId(), PageRequest.of(0, detalle.getCantidad()));
        if (unidades.size() != detalle.getCantidad()) {
            throw new IllegalArgumentException(
                    "No hay suficientes unidades seriadas disponibles para el producto " + producto.getSku() + ".");
        }
        for (UnidadSeriada unidad : unidades) {
            unidad.setEstado("VENDIDA");
            unidad.setFechaSalida(LocalDateTime.now());
            unidad.setDetalleVenta(detalle);
        }
    }

    private void guardarMovimiento(
            Producto producto,
            Almacen almacen,
            Usuario usuario,
            String lote,
            DetalleVenta detalle,
            int saldo,
            int cantidad,
            BigDecimal costo,
            String tipo) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setAlmacen(almacen);
        movimiento.setLote(lote.isBlank() ? null : lote);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setSaldoPosterior(saldo);
        movimiento.setCostoUnitario(costo);
        movimiento.setReferenciaTipo("VENTA");
        movimiento.setReferenciaId(detalle.getVenta().getId());
        movimiento.setUsuario(usuario);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservaciones("Detalle #" + detalle.getId());
        movimientoRepository.save(movimiento);
    }

    private Usuario usuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado no existe."));
    }

    private String resolverLote(Producto producto, String loteIngresado) {
        if (!producto.isControlaLote()) {
            return "";
        }
        if (loteIngresado == null || loteIngresado.isBlank()) {
            throw new IllegalArgumentException("Debes indicar el lote del producto " + producto.getSku() + ".");
        }
        return loteIngresado.trim();
    }

    private void validarMetodoPago(String metodoPago) {
        if (metodoPago == null || !List.of("EFECTIVO", "TRANSFERENCIA", "TARJETA", "CONSIGNACION")
                .contains(metodoPago.trim().toUpperCase(Locale.ROOT))) {
            throw new IllegalArgumentException("El método de pago seleccionado no es válido.");
        }
    }
}
