package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.ProveedorVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProveedorDao extends JdbcRecordDao<ProveedorVO> {
    public ProveedorDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, ProveedorVO.class, "proveedor", "id_proveedor");
    }
}
