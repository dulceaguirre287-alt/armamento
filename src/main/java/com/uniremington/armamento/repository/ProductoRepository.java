package com.uniremington.armamento.repository;

import com.uniremington.armamento.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Page<Producto> findByNombreContainingIgnoreCaseOrSkuContainingIgnoreCase(
            String nombre, String sku, Pageable pageable);

    boolean existsBySkuIgnoreCase(String sku);

    List<Producto> findByActivoTrueOrderByNombreAsc();
}
