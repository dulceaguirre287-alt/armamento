package com.uniremington.armamento.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import com.uniremington.armamento.dto.DashboardDto;
import com.uniremington.armamento.dto.MovimientoVistaDto;
import com.uniremington.armamento.repository.CompraRepository;
import com.uniremington.armamento.repository.DevolucionRepository;
import com.uniremington.armamento.repository.DetalleVentaRepository;
import com.uniremington.armamento.repository.ExistenciaRepository;
import com.uniremington.armamento.repository.MovimientoInventarioRepository;
import com.uniremington.armamento.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;
    private final DevolucionRepository devolucionRepository;
    private final ExistenciaRepository existenciaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    @Transactional(readOnly = true)
    public DashboardDto cargar() {
        LocalDate hoy = LocalDate.now();
        YearMonth mesActual = YearMonth.from(hoy);
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime inicioMesSiguiente = mesActual.plusMonths(1).atDay(1).atStartOfDay();
        BigDecimal ventasMes = ventasEntre(inicioMes, inicioMesSiguiente);
        BigDecimal comprasMes = compraRepository.findByFechaCompraGreaterThanEqualAndFechaCompraLessThanAndEstadoNot(
                        mesActual.atDay(1), mesActual.plusMonths(1).atDay(1), "ANULADA")
                .stream().map(compra -> compra.getTotal() == null ? BigDecimal.ZERO : compra.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long devolucionesMes = devolucionRepository.countByFechaDevolucionGreaterThanEqualAndFechaDevolucionLessThanAndEstado(
                inicioMes, inicioMesSiguiente, "APROBADA");

        List<YearMonth> ultimosSeisMeses = java.util.stream.IntStream.range(0, 6)
                .mapToObj(index -> mesActual.minusMonths(5L - index))
                .toList();
        List<String> meses = ultimosSeisMeses.stream()
                .map(mes -> mes.getMonth().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es-CO")))
                .toList();
        List<BigDecimal> ventasPorMes = ultimosSeisMeses.stream()
                .map(mes -> ventasEntre(mes.atDay(1).atStartOfDay(), mes.plusMonths(1).atDay(1).atStartOfDay()))
                .toList();

        List<com.uniremington.armamento.repository.GraficaConsulta> productos =
                detalleVentaRepository.productosMasVendidos().stream().limit(5).toList();
        List<com.uniremington.armamento.repository.GraficaConsulta> almacenes =
                existenciaRepository.unidadesPorAlmacen();

        return new DashboardDto(
                ventasMes,
                comprasMes,
                existenciaRepository.totalUnidades(),
                devolucionesMes,
                meses,
                ventasPorMes,
                productos.stream().map(com.uniremington.armamento.repository.GraficaConsulta::getEtiqueta).toList(),
                productos.stream().map(com.uniremington.armamento.repository.GraficaConsulta::getValor).toList(),
                almacenes.stream().map(com.uniremington.armamento.repository.GraficaConsulta::getEtiqueta).toList(),
                almacenes.stream().map(com.uniremington.armamento.repository.GraficaConsulta::getValor).toList(),
                movimientoRepository.findTop10ByOrderByFechaDesc().stream()
                        .map(movimiento -> new MovimientoVistaDto(
                                movimiento.getProducto().getNombre(),
                                movimiento.getAlmacen().getNombre(),
                                movimiento.getTipoMovimiento(),
                                movimiento.getCantidad(),
                                movimiento.getSaldoPosterior(),
                                movimiento.getFecha()))
                        .toList());
    }

    private BigDecimal ventasEntre(LocalDateTime desde, LocalDateTime hasta) {
        return ventaRepository.findByFechaVentaGreaterThanEqualAndFechaVentaLessThanAndEstadoNot(
                        desde, hasta, "ANULADA")
                .stream().map(venta -> venta.getTotal() == null ? BigDecimal.ZERO : venta.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
