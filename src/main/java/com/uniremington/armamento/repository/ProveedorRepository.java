package com.uniremington.armamento.repository;

import java.util.List;

import com.uniremington.armamento.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    List<Proveedor> findByActivoTrueOrderByRazonSocialAsc();
}
