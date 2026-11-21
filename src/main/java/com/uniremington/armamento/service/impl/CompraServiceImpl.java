package com.uniremington.armamento.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.uniremington.armamento.dto.CompraFormularioDto;
import com.uniremington.armamento.dto.CompraVistaDto;
import com.uniremington.armamento.dto.DetalleCompraFormularioDto;
import com.uniremington.armamento.entity.Almacen;
import com.uniremington.armamento.entity.Compra;
import com.uniremington.armamento.entity.DetalleCompra;
import com.uniremington.armamento.entity.Existencia;
import com.uniremington.armamento.entity.MovimientoInventario;
import com.uniremington.armamento.entity.Producto;
import com.uniremington.armamento.entity.Proveedor;
import com.uniremington.armamento.entity.UnidadSeriada;
import com.uniremington.armamento.entity.Usuario;
import com.uniremington.armamento.exception.RecursoNoEncontradoException;
import com.uniremington.armamento.mapper.CompraMapper;
import com.uniremington.armamento.repository.AlmacenRepository;
import com.uniremington.armamento.repository.CompraRepository;
import com.uniremington.armamento.repository.DetalleCompraRepository;
import com.uniremington.armamento.repository.DevolucionRepository;
import com.uniremington.armamento.repository.ExistenciaRepository;
import com.uniremington.armamento.repository.MovimientoInventarioRepository;
import com.uniremington.armamento.repository.ProductoRepository;
import com.uniremington.armamento.repository.ProveedorRepository;
import com.uniremington.armamento.repository.UnidadSeriadaRepository;
import com.uniremington.armamento.repository.UsuarioRepository;
import com.uniremington.armamento.service.AuditoriaService;
import com.uniremington.armamento.service.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {
    private static final BigDecimal CIEN = new BigDecimal("100");

    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final AlmacenRepository almacenRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ExistenciaRepository existenciaRepository;
    private final DevolucionRepository devolucionRepository;
    private final UnidadSeriadaRepository unidadSeriadaRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final CompraMapper compraMapper;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional(readOnly = true)
    public Page<CompraVistaDto> buscar(String consulta, Pageable pageable) {
        String termino = consulta == null ? "" : consulta.trim();
        Page<Compra> compras = termino.isEmpty()
                ? compraRepository.findAll(pageable)
                : compraRepository.findByNumeroCompraContainingIgnoreCase(termino, pageable);
        return compras.map(compraMapper::aVista);
    }

    @Override
    @Transactional(readOnly = true)
    public CompraVistaDto obtener(Long id) {
        return compraMapper.aVista(compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra", id)));
    }

    @Override
    @Transactional
    public CompraVistaDto crear(CompraFormularioDto dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", dto.getProveedorId()));
        Almacen almacen = almacenRepository.findById(dto.getAlmacenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén", dto.getAlmacenId()));
        if (!proveedor.isActivo() || !almacen.isActivo()) {
            throw new IllegalArgumentException("El proveedor y el almacén deben estar activos.");
        }
        if (proveedor.getLicenciaVence() != null && proveedor.getLicenciaVence().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La licencia del proveedor está vencida.");
        }

        Compra compra = new Compra();
        compra.setNumeroCompra("CMP-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        compra.setProveedor(proveedor);
        compra.setAlmacen(almacen);
        compra.setUsuario(usuarioActual());
        compra.setNumeroFacturaProveedor(dto.getNumeroFacturaProveedor());
        compra.setGuiaTransporte(dto.getGuiaTransporte());
        compra.setObservaciones(dto.getObservaciones());
        compra.setEstado("RECIBIDA");
        compra.setFechaRecepcion(LocalDateTime.now());
        compra.setSubtotal(BigDecimal.ZERO);
        compra.setIva(BigDecimal.ZERO);
        compra.setTotal(BigDecimal.ZERO);
        compra.setDescuento(BigDecimal.ZERO);
        compraRepository.saveAndFlush(compra);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        Map<String, Existencia> existencias = new HashMap<>();
        for (DetalleCompraFormularioDto item : dto.getDetalles()) {
            if (item.getProductoId() == null || item.getCantidad() <= 0 || item.getCantidad() > 10000
                    || item.getPrecioUnitario() == null
                    || item.getPrecioUnitario().signum() <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe estar entre 1 y 10.000 y el precio debe ser mayor que cero.");
            }
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", item.getProductoId()));
            if (!producto.isActivo()) {
                throw new IllegalArgumentException("El producto " + producto.getSku() + " está inactivo.");
            }
            String lote = resolverLote(producto, item.getLote());
            validarSeries(producto, item);

            BigDecimal importe = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal ivaLinea = importe.multiply(producto.getPorcentajeIva())
                    .divide(CIEN, 2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(importe);
            iva = iva.add(ivaLinea);

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidadPedida(item.getCantidad());
            detalle.setCantidadRecibida(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setPorcentajeIva(producto.getPorcentajeIva());
            detalle.setLote(producto.isControlaLote() ? lote : null);
            detalle.setFechaVencimiento(item.getFechaVencimiento());
            detalle.setSubtotal(importe);
            detalle = detalleCompraRepository.saveAndFlush(detalle);
            compra.getDetalles().add(detalle);

            String clave = producto.getId() + "|" + lote;
            Existencia existencia = existencias.get(clave);
            if (existencia == null) {
                existencia = existenciaRepository.buscarYBloquear(producto, almacen.getId(), lote)
                        .orElseGet(() -> {
                            Existencia nueva = new Existencia();
                            nueva.setProducto(producto);
                            nueva.setAlmacen(almacen);
                            nueva.setLote(lote);
                            return nueva;
                        });
                existencias.put(clave, existencia);
            }
            existencia.setCantidad(existencia.getCantidad() + item.getCantidad());
            if (item.getFechaVencimiento() != null) {
                existencia.setFechaVencimiento(item.getFechaVencimiento());
            }
            existenciaRepository.save(existencia);

            if (producto.isControlaSerial()) {
                registrarSeries(producto, almacen, detalle, item.getNumerosSerie());
            }
            guardarMovimiento(producto, almacen, compra.getUsuario(), lote, compra.getId(),
                    item.getCantidad(), existencia.getCantidad(), item.getPrecioUnitario(), "COMPRA");
        }

        compra.setSubtotal(subtotal);
        compra.setIva(iva);
        compra.setTotal(subtotal.add(iva));
        Compra guardada = compraRepository.save(compra);
        CompraVistaDto resultado = compraMapper.aVista(guardada);
        auditoriaService.registrar("CREAR", "compra", guardada.getId(), null, resultado);
        return resultado;
    }

    @Override
    @Transactional
    public void anular(Long id) {
        Compra compra = compraRepository.buscarYBloquear(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra", id));
        if ("ANULADA".equals(compra.getEstado())) {
            throw new IllegalArgumentException("La compra ya se encuentra anulada.");
        }
        if (devolucionRepository.existsByCompra_IdAndEstadoNot(id, "RECHAZADA")) {
            throw new IllegalArgumentException(
                    "No se puede anular la compra mientras tenga devoluciones pendientes o aprobadas.");
        }
        CompraVistaDto antes = compraMapper.aVista(compra);
        Usuario usuario = usuarioActual();
        for (DetalleCompra detalle : compra.getDetalles()) {
            Producto producto = detalle.getProducto();
            String lote = detalle.getLote() == null ? "" : detalle.getLote();
            Existencia existencia = existenciaRepository.buscarYBloquear(
                    producto, compra.getAlmacen().getId(), lote)
                    .orElseThrow(() -> new IllegalStateException(
                            "No se encontró la existencia requerida para anular la compra."));
            if (existencia.getCantidad() < detalle.getCantidadRecibida()) {
                throw new IllegalArgumentException(
                        "No se puede anular la compra: parte de sus existencias ya fue utilizada.");
            }
            if (producto.isControlaSerial()) {
                List<UnidadSeriada> unidades = unidadSeriadaRepository.findByDetalleCompra(detalle);
                if (unidades.size() != detalle.getCantidadRecibida()
                        || unidades.stream().anyMatch(unidad -> !"DISPONIBLE".equals(unidad.getEstado()))) {
                    throw new IllegalArgumentException(
                            "No se puede anular la compra: una unidad seriada ya fue asignada o vendida.");
                }
                unidades.forEach(unidad -> unidad.setEstado("EN_TRANSITO"));
            }
            existencia.setCantidad(existencia.getCantidad() - detalle.getCantidadRecibida());
            existenciaRepository.save(existencia);
            guardarMovimiento(producto, compra.getAlmacen(), usuario, lote, compra.getId(),
                    -detalle.getCantidadRecibida(), existencia.getCantidad(),
                    detalle.getPrecioUnitario(), "ANULACION_COMPRA");
        }
        compra.setEstado("ANULADA");
        CompraVistaDto despues = compraMapper.aVista(compraRepository.save(compra));
        auditoriaService.registrar("ANULAR", "compra", id, antes, despues);
    }

    private void registrarSeries(
            Producto producto, Almacen almacen, DetalleCompra detalle, List<String> numeros) {
        Set<String> unicos = new HashSet<>();
        for (String numero : numeros) {
            if (numero == null || numero.isBlank() || !unicos.add(numero.trim())
                    || unidadSeriadaRepository.existsByNumeroSerie(numero.trim())) {
                throw new IllegalArgumentException("Los números de serie deben ser únicos y no estar registrados.");
            }
        }
        for (String numero : unicos) {
            UnidadSeriada unidad = new UnidadSeriada();
            unidad.setProducto(producto);
            unidad.setNumeroSerie(numero);
            unidad.setAlmacen(almacen);
            unidad.setDetalleCompra(detalle);
            unidad.setEstado("DISPONIBLE");
            unidadSeriadaRepository.save(unidad);
        }
    }

    private void validarSeries(Producto producto, DetalleCompraFormularioDto item) {
        List<String> series = item.getNumerosSerie() == null ? List.of() : item.getNumerosSerie();
        if (producto.isControlaSerial() && series.size() != item.getCantidad()) {
            throw new IllegalArgumentException(
                    "Debes ingresar un número de serie único por cada unidad del producto " + producto.getSku() + ".");
        }
        if (!producto.isControlaSerial() && !series.stream().allMatch(String::isBlank)) {
            throw new IllegalArgumentException(
                    "Solo los productos controlados por serial aceptan números de serie.");
        }
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

    private void guardarMovimiento(
            Producto producto,
            Almacen almacen,
            Usuario usuario,
            String lote,
            Long compraId,
            int cantidad,
            int saldo,
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
        movimiento.setReferenciaTipo("COMPRA");
        movimiento.setReferenciaId(compraId);
        movimiento.setUsuario(usuario);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservaciones("Compra " + compraId);
        movimientoRepository.save(movimiento);
    }

    private Usuario usuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado no existe."));
    }
}
