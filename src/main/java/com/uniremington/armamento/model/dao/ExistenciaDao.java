package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.ExistenciaVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ExistenciaDao extends JdbcRecordDao<ExistenciaVO> {
    public ExistenciaDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, ExistenciaVO.class, "existencia", "id_existencia");
    }
}
