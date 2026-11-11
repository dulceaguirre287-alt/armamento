package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.AuditoriaVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditoriaDao extends JdbcRecordDao<AuditoriaVO> {
    public AuditoriaDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, AuditoriaVO.class, "auditoria", "id_auditoria");
    }
}
