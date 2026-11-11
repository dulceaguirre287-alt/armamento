package com.uniremington.armamento.model.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.uniremington.armamento.model.vo.CompraVO;
import com.uniremington.armamento.model.vo.ProductoProveedorVO;
import com.uniremington.armamento.model.vo.ProductoVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class JdbcRecordDaoTests {
    @Autowired
    private ProductoDao productoDao;

    @Autowired
    private ProductoProveedorDao productoProveedorDao;

    @Autowired
    private CompraDao compraDao;

    @Test
    void savesReadsUpdatesAndDeletesRecordWithGeneratedId() {
        ProductoVO product = new ProductoVO(
                null, "DAO-TEST", "Producto de prueba", "Descripción", null, null,
                "Modelo", "9 mm", "unidad", true, false,
                new BigDecimal("12.50"), new BigDecimal("20.00"), new BigDecimal("19.00"),
                2, true, null, null);

        ProductoVO saved = productoDao.save(product);

        assertNotNull(saved.idProducto());
        ProductoVO loaded = productoDao.findById(saved.idProducto()).orElseThrow();
        assertEquals(saved.idProducto(), loaded.idProducto());
        assertEquals(saved.sku(), loaded.sku());
        assertEquals(0, saved.precioCompraRef().compareTo(loaded.precioCompraRef()));
        assertEquals(0, saved.precioVenta().compareTo(loaded.precioVenta()));

        ProductoVO updated = new ProductoVO(
                saved.idProducto(), saved.sku(), "Producto actualizado", saved.descripcion(),
                saved.idCategoria(), saved.idMarca(), saved.modelo(), saved.calibre(),
                saved.unidadMedida(), saved.controlaSerial(), saved.controlaLote(),
                saved.precioCompraRef(), saved.precioVenta(), saved.porcentajeIva(),
                saved.stockMinimo(), saved.activo(), saved.creadoEn(), saved.actualizadoEn());
        assertEquals(updated, productoDao.save(updated));
        assertEquals("Producto actualizado",
                productoDao.findById(saved.idProducto()).orElseThrow().nombre());

        assertTrue(productoDao.deleteById(saved.idProducto()));
        assertFalse(productoDao.findById(saved.idProducto()).isPresent());
    }

    @Test
    void savesAndReadsCompositePrimaryKeyRecord() {
        ProductoProveedorVO productSupplier = new ProductoProveedorVO(
                7001, 8001, "PROV-01", new BigDecimal("15.75"), (short) 4);

        productoProveedorDao.save(productSupplier);
        ProductoProveedorVO loaded = productoProveedorDao.findById(7001, 8001).orElseThrow();
        assertEquals(productSupplier.idProducto(), loaded.idProducto());
        assertEquals(productSupplier.idProveedor(), loaded.idProveedor());
        assertEquals(productSupplier.codigoProveedor(), loaded.codigoProveedor());
        assertEquals(0, productSupplier.ultimoPrecio().compareTo(loaded.ultimoPrecio()));
        assertTrue(productoProveedorDao.deleteById(7001, 8001));
    }

    @Test
    void mapsLongIdsAndJavaTimeValues() {
        CompraVO purchase = new CompraVO(
                null, "CMP-DAO-TEST", 10, 20, 30,
                LocalDate.of(2026, 11, 10), LocalDateTime.of(2026, 11, 10, 12, 30),
                "FAC-01", null, new BigDecimal("100.00"), BigDecimal.ZERO,
                new BigDecimal("19.00"), new BigDecimal("119.00"),
                "RECIBIDA", "Compra de prueba", LocalDateTime.of(2026, 11, 10, 13, 0));

        CompraVO saved = compraDao.save(purchase);
        CompraVO loaded = compraDao.findById(saved.idCompra()).orElseThrow();

        assertNotNull(saved.idCompra());
        assertEquals(purchase.fechaCompra(), loaded.fechaCompra());
        assertEquals(purchase.fechaRecepcion(), loaded.fechaRecepcion());
        assertEquals(purchase.observaciones(), loaded.observaciones());
        assertEquals(0, purchase.total().compareTo(loaded.total()));
        assertTrue(compraDao.deleteById(saved.idCompra()));
    }
}
