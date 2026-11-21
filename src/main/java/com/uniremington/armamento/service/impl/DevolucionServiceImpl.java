package com.uniremington.armamento.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.uniremington.armamento.dto.DevolucionFormularioDto;
import com.uniremington.armamento.dto.DevolucionVistaDto;
import com.uniremington.armamento.dto.DetalleDevolucionFormularioDto;
import com.uniremington.armamento.entity.Almacen;
import com.uniremington.armamento.entity.Compra;
import com.uniremington.armamento.entity.Devolucion;
import com.uniremington.armamento.entity.DetalleCompra;
import com.uniremington.armamento.entity.DetalleDevolucion;
import com.uniremington.armamento.entity.DetalleVenta;
import com.uniremington.armamento.entity.Existencia;
import com.uniremington.armamento.entity.MovimientoInventario;
import com.uniremington.armamento.entity.Producto;
import com.uniremington.armamento.entity.UnidadSeriada;
import com.uniremington.armamento.entity.Usuario;
import com.uniremington.armamento.entity.Venta;
import com.uniremington.armamento.exception.RecursoNoEncontradoException;
import com.uniremington.armamento.mapper.DevolucionMapper;
import com.uniremington.armamento.repository.CompraRepository;
import com.uniremington.armamento.repository.DevolucionRepository;
import com.uniremington.armamento.repository.DetalleDevolucionRepository;
import com.uniremington.armamento.repository.ExistenciaRepository;
import com.uniremington.armamento.repository.MovimientoInventarioRepository;
import com.uniremington.armamento.repository.UnidadSeriadaRepository;
import com.uniremington.armamento.repository.UsuarioRepository;
import com.uniremington.armamento.repository.VentaRepository;
import com.uniremington.armamento.service.AuditoriaService;
import com.uniremington.armamento.service.DevolucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DevolucionServiceImpl implements DevolucionService {
    private final DevolucionRepository devolucionRepository;
    private final DetalleDevolucionRepository detalleRepository;
    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;
    private final ExistenciaRepository existenciaRepository;
    private final UnidadSeriadaRepository unidadRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DevolucionMapper devolucionMapper;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional(readOnly = true)
    public Page<DevolucionVistaDto> buscar(String consulta, Pageable pageable) {
        String termino = consulta == null ? "" : consulta.trim();
        Page<Devolucion> devoluciones = termino.isEmpty()
                ? devolucionRepository.findAll(pageable)
                : devolucionRepository.findByNumeroDevolucionContainingIgnoreCase(termino, pageable);
        return devoluciones.map(devolucionMapper::aVista);
    }

    @Override
    @Transactional(readOnly = true)
    public DevolucionVistaDto obtener(Long id) {
        return devolucionMapper.aVista(devolucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Devolución", id)));
    }

    @Override
    @Transactional
    public DevolucionVistaDto crear(DevolucionFormularioDto dto) {
        String tipo = dto.getTipo() == null ? "" : dto.getTipo().trim().toUpperCase(Locale.ROOT);
        Venta venta = null;
        Compra compra = null;
        Almacen almacen;
        List<DetalleVenta> detallesVenta = List.of();
        List<DetalleCompra> detallesCompra = List.of();
        if ("VENTA".equals(tipo) && dto.getVentaId() != null && dto.getCompraId() == null) {
            venta = ventaRepository.buscarYBloquear(dto.getVentaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Venta", dto.getVentaId()));
            if ("ANULADA".equals(venta.getEstado())) {
                throw new IllegalArgumentException("No se puede devolver una venta anulada.");
            }
            almacen = venta.getAlmacen();
            detallesVenta = venta.getDetalles();
        } else if ("COMPRA".equals(tipo) && dto.getCompraId() != null && dto.getVentaId() == null) {
            compra = compraRepository.buscarYBloquear(dto.getCompraId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Compra", dto.getCompraId()));
            if ("ANULADA".equals(compra.getEstado())) {
                throw new IllegalArgumentException("No se puede devolver una compra anulada.");
            }
            almacen = compra.getAlmacen();
            detallesCompra = compra.getDetalles();
        } else {
            throw new IllegalArgumentException("Selecciona una única venta o compra como origen.");
        }

        Devolucion devolucion = new Devolucion();
        devolucion.setNumeroDevolucion("DEV-" + java.time.LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        devolucion.setTipo(tipo);
        devolucion.setVenta(venta);
        devolucion.setCompra(compra);
        devolucion.setAlmacen(almacen);
        devolucion.setUsuario(usuarioActual());
        devolucion.setMotivo(dto.getMotivo().trim());
        devolucion.setDescripcion(dto.getDescripcion());
        devolucion.setEstado("PENDIENTE");
        devolucion.setTotalReembolso(BigDecimal.ZERO);
        devolucionRepository.saveAndFlush(devolucion);

        Map<String, Integer> cantidadesSolicitadas = new HashMap<>();
        Set<Long> unidadesEnSolicitud = new HashSet<>();
        BigDecimal reembolso = BigDecimal.ZERO;
        for (DetalleDevolucionFormularioDto item : dto.getDetalles()) {
            if (item.getProductoId() == null || item.getCantidad() <= 0 || item.getCantidad() > 10000) {
                throw new IllegalArgumentException("La cantidad de cada producto debe estar entre 1 y 10.000.");
            }
            Producto producto;
            BigDecimal precio;
            String lote;
            if (venta != null) {
                DetalleVenta origen = buscarDetalleVenta(detallesVenta, item);
                producto = origen.getProducto();
                precio = origen.getPrecioUnitario();
                lote = normalizarLote(origen.getLote());
            } else {
                DetalleCompra origen = buscarDetalleCompra(detallesCompra, item);
                producto = origen.getProducto();
                precio = origen.getPrecioUnitario();
                lote = normalizarLote(origen.getLote());
            }
            if (item.getUnidadId() == null && producto.isControlaSerial()) {
                throw new IllegalArgumentException("Selecciona el número de serie de la unidad que devuelves.");
            }
            if (item.getUnidadId() != null && (!producto.isControlaSerial() || item.getCantidad() != 1)) {
                throw new IllegalArgumentException("Cada unidad seriada se devuelve individualmente.");
            }
            if (item.getUnidadId() == null && producto.isControlaLote()
                    && !lote.equals(normalizarLote(item.getLote()))) {
                throw new IllegalArgumentException("El lote no corresponde con la transacción de origen.");
            }
            int acumulado = cantidadesSolicitadas.merge(
                    producto.getId() + "|" + lote, item.getCantidad(), Integer::sum);
            validarCantidadOrigen(tipo, venta, compra, producto, lote, acumulado, detallesVenta, detallesCompra);
            UnidadSeriada unidad = item.getUnidadId() == null ? null : validarUnidad(
                    item.getUnidadId(), tipo, venta, compra, producto);
            if (unidad != null && !unidadesEnSolicitud.add(unidad.getId())) {
                throw new IllegalArgumentException("No puedes incluir dos veces la misma unidad seriada.");
            }
            if (item.getCondicion() == null || item.getCondicion().isBlank()) {
                throw new IllegalArgumentException("Indica la condición de cada producto devuelto.");
            }

            DetalleDevolucion detalle = new DetalleDevolucion();
            detalle.setDevolucion(devolucion);
            detalle.setProducto(producto);
            detalle.setUnidad(unidad);
            detalle.setLote(lote.isBlank() ? null : lote);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precio);
            detalle.setCondicion(item.getCondicion().trim().toUpperCase(Locale.ROOT));
            detalle.setReingresaStock("VENTA".equals(tipo) && item.isReingresaStock());
            detalleRepository.save(detalle);
            devolucion.getDetalles().add(detalle);
            reembolso = reembolso.add(precio.multiply(BigDecimal.valueOf(item.getCantidad())));
        }

        devolucion.setTotalReembolso(reembolso);
        Devolucion guardada = devolucionRepository.save(devolucion);
        DevolucionVistaDto resultado = devolucionMapper.aVista(guardada);
        auditoriaService.registrar("CREAR", "devolucion", guardada.getId(), null, resultado);
        return resultado;
    }

    @Override
    @Transactional
    public void aprobar(Long id) {
        Devolucion devolucion = devolucionRepository.buscarYBloquear(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Devolución", id));
        if (!"PENDIENTE".equals(devolucion.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden aprobar devoluciones pendientes.");
        }
        DevolucionVistaDto antes = devolucionMapper.aVista(devolucion);
        Usuario aprobador = usuarioActual();
        for (DetalleDevolucion detalle : devolucion.getDetalles()) {
            Producto producto = detalle.getProducto();
            String lote = normalizarLote(detalle.getLote());
            Existencia existencia = existenciaRepository.buscarYBloquear(
                    producto, devolucion.getAlmacen().getId(), lote).orElse(null);
            int saldoActual = existencia == null ? 0 : existencia.getCantidad();
            int movimientoCantidad;

            if ("COMPRA".equals(devolucion.getTipo())) {
                if (existencia == null || saldoActual < detalle.getCantidad()) {
                    throw new IllegalArgumentException(
                            "No hay existencias suficientes para devolver el producto al proveedor.");
                }
                if (detalle.getUnidad() != null) {
                    validarEstadoUnidad(detalle.getUnidad(), "DISPONIBLE");
                    detalle.getUnidad().setEstado("EN_TRANSITO");
                }
                existencia.setCantidad(saldoActual - detalle.getCantidad());
                movimientoCantidad = -detalle.getCantidad();
            } else if (detalle.isReingresaStock()) {
                if (existencia == null) {
                    existencia = new Existencia();
                    existencia.setProducto(producto);
                    existencia.setAlmacen(devolucion.getAlmacen());
                    existencia.setLote(lote);
                }
                existencia.setCantidad(saldoActual + detalle.getCantidad());
                if (detalle.getUnidad() != null) {
                    validarEstadoUnidad(detalle.getUnidad(), "VENDIDA");
                    detalle.getUnidad().setEstado("DISPONIBLE");
                    detalle.getUnidad().setAlmacen(devolucion.getAlmacen());
                    detalle.getUnidad().setFechaSalida(null);
                }
                movimientoCantidad = detalle.getCantidad();
            } else {
                if (detalle.getUnidad() != null) {
                    validarEstadoUnidad(detalle.getUnidad(), "VENDIDA");
                    detalle.getUnidad().setEstado("DEVUELTA");
                }
                movimientoCantidad = 0;
            }

            if (existencia != null) {
                existenciaRepository.save(existencia);
                saldoActual = existencia.getCantidad();
            }
            guardarMovimiento(devolucion, detalle, aprobador, movimientoCantidad, saldoActual);
        }
        devolucion.setEstado("APROBADA");
        devolucion.setAprobadaPor(aprobador);
        DevolucionVistaDto despues = devolucionMapper.aVista(devolucionRepository.save(devolucion));
        auditoriaService.registrar("APROBAR", "devolucion", id, antes, despues);
    }

    @Override
    @Transactional
    public void rechazar(Long id) {
        Devolucion devolucion = devolucionRepository.buscarYBloquear(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Devolución", id));
        if (!"PENDIENTE".equals(devolucion.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden rechazar devoluciones pendientes.");
        }
        DevolucionVistaDto antes = devolucionMapper.aVista(devolucion);
        devolucion.setEstado("RECHAZADA");
        DevolucionVistaDto despues = devolucionMapper.aVista(devolucionRepository.save(devolucion));
        auditoriaService.registrar("RECHAZAR", "devolucion", id, antes, despues);
    }

    private void validarCantidadOrigen(
            String tipo,
            Venta venta,
            Compra compra,
            Producto producto,
            String lote,
            int acumulado,
            List<DetalleVenta> ventas,
            List<DetalleCompra> compras) {
        if ("VENTA".equals(tipo)) {
            long vendida = ventas.stream()
                    .filter(detalle -> detalle.getProducto().getId().equals(producto.getId())
                            && normalizarLote(detalle.getLote()).equals(lote))
                    .mapToLong(DetalleVenta::getCantidad).sum();
            long devuelta = detalleRepository.cantidadDevueltaDeVentaPorLote(
                    venta.getId(), producto.getId(), lote);
            if (devuelta + acumulado > vendida) {
                throw new IllegalArgumentException("La cantidad devuelta supera la cantidad vendida.");
            }
        } else {
            long recibida = compras.stream()
                    .filter(detalle -> detalle.getProducto().getId().equals(producto.getId())
                            && normalizarLote(detalle.getLote()).equals(lote))
                    .mapToLong(DetalleCompra::getCantidadRecibida).sum();
            long devuelta = detalleRepository.cantidadDevueltaDeCompraPorLote(
                    compra.getId(), producto.getId(), lote);
            if (devuelta + acumulado > recibida) {
                throw new IllegalArgumentException("La cantidad devuelta supera la cantidad recibida.");
            }
        }
    }

    private DetalleVenta buscarDetalleVenta(List<DetalleVenta> detalles, DetalleDevolucionFormularioDto item) {
        String lote = normalizarLote(item.getLote());
        return detalles.stream()
                .filter(detalle -> detalle.getProducto().getId().equals(item.getProductoId())
                        && normalizarLote(detalle.getLote()).equals(lote))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto o lote no pertenece a la venta indicada."));
    }

    DetalleCompra buscarDetalleCompra(List<DetalleCompra> detalles, DetalleDevolucionFormularioDto item) {
        String lote = normalizarLote(item.getLote());
        List<DetalleCompra> coincidencias = detalles.stream()
                .filter(detalle -> detalle.getProducto().getId().equals(item.getProductoId())
                        && normalizarLote(detalle.getLote()).equals(lote))
                .toList();
        if (coincidencias.isEmpty()) {
            throw new IllegalArgumentException("El producto o lote no pertenece a la compra indicada.");
        }
        BigDecimal precio = coincidencias.get(0).getPrecioUnitario();
        if (coincidencias.stream().anyMatch(detalle -> detalle.getPrecioUnitario().compareTo(precio) != 0)) {
            throw new IllegalArgumentException(
                    "La compra tiene varias líneas del producto o lote con precios distintos; "
                            + "no se puede determinar el precio de devolución.");
        }
        return coincidencias.get(0);
    }

    private UnidadSeriada validarUnidad(
            Long unidadId, String tipo, Venta venta, Compra compra, Producto producto) {
        UnidadSeriada unidad = unidadRepository.findById(unidadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Unidad seriada", unidadId));
        if (!unidad.getProducto().getId().equals(producto.getId()) || detalleRepository.unidadYaDevuelta(unidadId)) {
            throw new IllegalArgumentException("La unidad seriada no corresponde al producto o ya fue devuelta.");
        }
        if ("VENTA".equals(tipo)) {
            if (unidad.getDetalleVenta() == null
                    || !unidad.getDetalleVenta().getVenta().getId().equals(venta.getId())) {
                throw new IllegalArgumentException("La unidad seriada no pertenece a la venta indicada.");
            }
            validarEstadoUnidad(unidad, "VENDIDA");
        } else {
            if (unidad.getDetalleCompra() == null
                    || !unidad.getDetalleCompra().getCompra().getId().equals(compra.getId())) {
                throw new IllegalArgumentException("La unidad seriada no pertenece a la compra indicada.");
            }
            validarEstadoUnidad(unidad, "DISPONIBLE");
        }
        return unidad;
    }

    private void validarEstadoUnidad(UnidadSeriada unidad, String esperado) {
        if (!esperado.equals(unidad.getEstado())) {
            throw new IllegalArgumentException(
                    "La unidad " + unidad.getNumeroSerie() + " no tiene el estado requerido para esta devolución.");
        }
    }

    private void guardarMovimiento(
            Devolucion devolucion,
            DetalleDevolucion detalle,
            Usuario usuario,
            int cantidad,
            int saldo) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(detalle.getProducto());
        movimiento.setAlmacen(devolucion.getAlmacen());
        movimiento.setUnidad(detalle.getUnidad());
        movimiento.setLote(detalle.getLote());
        movimiento.setTipoMovimiento("DEVOLUCION_" + devolucion.getTipo());
        movimiento.setCantidad(cantidad);
        movimiento.setSaldoPosterior(saldo);
        movimiento.setCostoUnitario(detalle.getPrecioUnitario());
        movimiento.setReferenciaTipo("DEVOLUCION");
        movimiento.setReferenciaId(devolucion.getId());
        movimiento.setUsuario(usuario);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservaciones("Devolución " + devolucion.getNumeroDevolucion());
        movimientoRepository.save(movimiento);
    }

    private String normalizarLote(String lote) {
        return lote == null ? "" : lote.trim();
    }

    private Usuario usuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado no existe."));
    }
}
