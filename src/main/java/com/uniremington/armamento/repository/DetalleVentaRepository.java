package com.uniremington.armamento.repository;

import com.uniremington.armamento.entity.DetalleVenta;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    @Query("select d.producto.nombre as etiqueta, sum(d.cantidad) as valor "
            + "from DetalleVenta d where d.venta.estado <> 'ANULADA' "
            + "group by d.producto.id, d.producto.nombre order by sum(d.cantidad) desc")
    List<GraficaConsulta> productosMasVendidos();
}
