package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.UnidadSeriadaVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UnidadSeriadaDao extends JdbcRecordDao<UnidadSeriadaVO> {
    public UnidadSeriadaDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, UnidadSeriadaVO.class, "unidad_seriada", "id_unidad");
    }
}
