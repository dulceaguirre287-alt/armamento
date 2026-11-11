package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.CompraVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CompraDao extends JdbcRecordDao<CompraVO> {
    public CompraDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, CompraVO.class, "compra", "id_compra");
    }
}
