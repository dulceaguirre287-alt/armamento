package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.PermisoClienteVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PermisoClienteDao extends JdbcRecordDao<PermisoClienteVO> {
    public PermisoClienteDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, PermisoClienteVO.class, "permiso_cliente", "id_permiso");
    }
}
