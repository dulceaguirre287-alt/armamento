package com.uniremington.armamento.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardDto(
        BigDecimal ventasDelMes,
        BigDecimal comprasDelMes,
        long existenciasTotales,
        long devolucionesDelMes,
        List<String> meses,
        List<BigDecimal> ventasPorMes,
        List<String> productosMasVendidos,
        List<Long> cantidadesMasVendidas,
        List<String> almacenes,
        List<Long> existenciasPorAlmacen,
        List<MovimientoVistaDto> ultimosMovimientos) {
}
