package com.uniremington.armamento.repository;

import java.util.List;

import com.uniremington.armamento.entity.DetalleDevolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetalleDevolucionRepository extends JpaRepository<DetalleDevolucion, Long> {
    @Query("""
            select coalesce(sum(d.cantidad), 0) from DetalleDevolucion d
            where d.devolucion.tipo = 'VENTA'
              and d.devolucion.venta.id = :ventaId
              and d.producto.id = :productoId
              and d.devolucion.estado <> 'RECHAZADA'
            """)
    long cantidadDevueltaDeVenta(@Param("ventaId") Long ventaId, @Param("productoId") Integer productoId);

    @Query("""
            select coalesce(sum(d.cantidad), 0) from DetalleDevolucion d
            where d.devolucion.tipo = 'COMPRA'
              and d.devolucion.compra.id = :compraId
              and d.producto.id = :productoId
              and d.devolucion.estado <> 'RECHAZADA'
            """)
    long cantidadDevueltaDeCompra(@Param("compraId") Long compraId, @Param("productoId") Integer productoId);

    @Query("""
            select coalesce(sum(d.cantidad), 0) from DetalleDevolucion d
            where d.devolucion.tipo = 'VENTA'
              and d.devolucion.venta.id = :ventaId
              and d.producto.id = :productoId
              and coalesce(d.lote, '') = :lote
              and d.devolucion.estado <> 'RECHAZADA'
            """)
    long cantidadDevueltaDeVentaPorLote(
            @Param("ventaId") Long ventaId, @Param("productoId") Integer productoId, @Param("lote") String lote);

    @Query("""
            select coalesce(sum(d.cantidad), 0) from DetalleDevolucion d
            where d.devolucion.tipo = 'COMPRA'
              and d.devolucion.compra.id = :compraId
              and d.producto.id = :productoId
              and coalesce(d.lote, '') = :lote
              and d.devolucion.estado <> 'RECHAZADA'
            """)
    long cantidadDevueltaDeCompraPorLote(
            @Param("compraId") Long compraId, @Param("productoId") Integer productoId, @Param("lote") String lote);

    List<DetalleDevolucion> findByDevolucionId(Long devolucionId);

    boolean existsByUnidadId(Long unidadId);

    @Query("""
            select case when count(d) > 0 then true else false end from DetalleDevolucion d
            where d.unidad.id = :unidadId and d.devolucion.estado <> 'RECHAZADA'
            """)
    boolean unidadYaDevuelta(@Param("unidadId") Long unidadId);
}
