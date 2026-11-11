package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.DetalleVentaVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DetalleVentaDao extends JdbcRecordDao<DetalleVentaVO> {
    public DetalleVentaDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, DetalleVentaVO.class, "detalle_venta", "id_detalle_venta");
    }
}
