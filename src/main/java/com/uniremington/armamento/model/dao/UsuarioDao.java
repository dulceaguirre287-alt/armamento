package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.UsuarioVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDao extends JdbcRecordDao<UsuarioVO> {
    public UsuarioDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, UsuarioVO.class, "usuario", "id_usuario");
    }
}
