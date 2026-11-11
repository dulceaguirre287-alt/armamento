package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.CategoriaVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CategoriaDao extends JdbcRecordDao<CategoriaVO> {
    public CategoriaDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, CategoriaVO.class, "categoria", "id_categoria");
    }
}
