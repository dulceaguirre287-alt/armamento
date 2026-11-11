package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.ClienteVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteDao extends JdbcRecordDao<ClienteVO> {
    public ClienteDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, ClienteVO.class, "cliente", "id_cliente");
    }
}
