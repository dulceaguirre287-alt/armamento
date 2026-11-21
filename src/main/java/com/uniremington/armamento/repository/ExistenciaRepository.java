package com.uniremington.armamento.repository;

import java.util.Optional;
import java.util.List;

import com.uniremington.armamento.entity.Existencia;
import com.uniremington.armamento.entity.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExistenciaRepository extends JpaRepository<Existencia, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Existencia e where e.producto = :producto and e.almacen.id = :almacenId and e.lote = :lote")
    Optional<Existencia> buscarYBloquear(
            @Param("producto") Producto producto,
            @Param("almacenId") Integer almacenId,
            @Param("lote") String lote);

    @Query("select coalesce(sum(e.cantidad), 0) from Existencia e")
    long totalUnidades();

    @Query("select e.almacen.nombre as etiqueta, sum(e.cantidad) as valor "
            + "from Existencia e group by e.almacen.id, e.almacen.nombre order by e.almacen.nombre")
    List<GraficaConsulta> unidadesPorAlmacen();
}
