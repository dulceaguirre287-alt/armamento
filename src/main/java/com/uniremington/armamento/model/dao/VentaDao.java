package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.VentaVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VentaDao extends JdbcRecordDao<VentaVO> {
    public VentaDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, VentaVO.class, "venta", "id_venta");
    }
}
