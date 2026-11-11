package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.DetalleDevolucionVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DetalleDevolucionDao extends JdbcRecordDao<DetalleDevolucionVO> {
    public DetalleDevolucionDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, DetalleDevolucionVO.class, "detalle_devolucion", "id_detalle_devolucion");
    }
}
