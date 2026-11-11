package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.ProductoVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductoDao extends JdbcRecordDao<ProductoVO> {
    public ProductoDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, ProductoVO.class, "producto", "id_producto");
    }
}
