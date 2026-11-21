package com.uniremington.armamento.repository;

import com.uniremington.armamento.entity.Almacen;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlmacenRepository extends JpaRepository<Almacen, Integer> {
    List<Almacen> findByActivoTrueOrderByNombreAsc();
}
