package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.RolVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RolDao extends JdbcRecordDao<RolVO> {
    public RolDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, RolVO.class, "rol", "id_rol");
    }
}
