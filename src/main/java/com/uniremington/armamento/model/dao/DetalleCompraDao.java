package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.DetalleCompraVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DetalleCompraDao extends JdbcRecordDao<DetalleCompraVO> {
    public DetalleCompraDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, DetalleCompraVO.class, "detalle_compra", "id_detalle_compra");
    }
}
