package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.MarcaVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MarcaDao extends JdbcRecordDao<MarcaVO> {
    public MarcaDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, MarcaVO.class, "marca", "id_marca");
    }
}
