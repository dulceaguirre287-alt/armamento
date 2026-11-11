package com.uniremington.armamento.model.dao;

import com.uniremington.armamento.model.vo.ProductoProveedorVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductoProveedorDao extends JdbcRecordDao<ProductoProveedorVO> {
    public ProductoProveedorDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, ProductoProveedorVO.class, "producto_proveedor", "id_producto", "id_proveedor");
    }
}
