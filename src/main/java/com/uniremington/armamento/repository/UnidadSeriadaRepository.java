package com.uniremington.armamento.repository;

import java.util.List;

import com.uniremington.armamento.entity.DetalleVenta;
import com.uniremington.armamento.entity.DetalleCompra;
import com.uniremington.armamento.entity.Producto;
import com.uniremington.armamento.entity.UnidadSeriada;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UnidadSeriadaRepository extends JpaRepository<UnidadSeriada, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UnidadSeriada u where u.producto = :producto and u.almacen.id = :almacenId "
            + "and u.estado = 'DISPONIBLE' order by u.id")
    List<UnidadSeriada> buscarDisponibles(
            @Param("producto") Producto producto,
            @Param("almacenId") Integer almacenId,
            Pageable pageable);

    List<UnidadSeriada> findByDetalleVenta(DetalleVenta detalleVenta);

    List<UnidadSeriada> findByDetalleCompra(DetalleCompra detalleCompra);

    boolean existsByNumeroSerie(String numeroSerie);
}
