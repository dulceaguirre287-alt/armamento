package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.DevolucionVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DevolucionDao extends JdbcRecordDao<DevolucionVO> {
    public DevolucionDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, DevolucionVO.class, "devolucion", "id_devolucion");
    }
}
