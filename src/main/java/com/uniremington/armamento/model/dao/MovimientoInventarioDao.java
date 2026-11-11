package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.MovimientoInventarioVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MovimientoInventarioDao extends JdbcRecordDao<MovimientoInventarioVO> {
    public MovimientoInventarioDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, MovimientoInventarioVO.class, "movimiento_inventario", "id_movimiento");
    }
}
