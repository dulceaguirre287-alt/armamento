package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.AlmacenVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AlmacenDao extends JdbcRecordDao<AlmacenVO> {
    public AlmacenDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, AlmacenVO.class, "almacen", "id_almacen");
    }
}
