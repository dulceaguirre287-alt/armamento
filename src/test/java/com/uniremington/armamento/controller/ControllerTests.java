package com.uniremington.armamento.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.uniremington.armamento.model.dao.ProductoDao;
import com.uniremington.armamento.model.dao.ProductoProveedorDao;
import com.uniremington.armamento.model.dao.UsuarioDao;
import com.uniremington.armamento.model.vo.ProductoVO;
import com.uniremington.armamento.model.vo.UsuarioVO;
import com.uniremington.armamento.controller.res.ResController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoDao productoDao;

    @Autowired
    private ProductoProveedorDao productoProveedorDao;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private ResController resController;

    @Test
    void productRestControllerSupportsCrud() throws Exception {
        String productJson = """
                {
                  "sku": "REST-TEST",
                  "nombre": "Producto REST",
                  "descripcion": "Prueba de controladores",
                  "unidadMedida": "unidad",
                  "controlaSerial": true,
                  "controlaLote": false,
                  "precioCompraRef": 12.50,
                  "precioVenta": 20.00,
                  "porcentajeIva": 19.00,
                  "stockMinimo": 2,
                  "activo": true
                }
                """;

        MvcResult createdResult = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProducto").isNumber())
                .andReturn();

        JsonNode created = objectMapper.readTree(createdResult.getResponse().getContentAsString());
        int id = created.get("idProducto").asInt();

        ObjectNode duplicate = (ObjectNode) objectMapper.readTree(productJson);
        duplicate.put("idProducto", id);
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/productos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("REST-TEST"));

        JsonNode updated = created.deepCopy();
        ((ObjectNode) updated).put("nombre", "Producto actualizado");
        mockMvc.perform(put("/api/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto actualizado"));

        mockMvc.perform(delete("/api/productos/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/productos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void controllerReturnsClientErrorsForInvalidResourceAndCompositeKey() throws Exception {
        mockMvc.perform(get("/api/recurso-inexistente"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/productos-proveedor/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void compositeKeyResourceSupportsCreateAndRead() throws Exception {
        String association = """
                {
                  "idProducto": 7001,
                  "idProveedor": 8001,
                  "codigoProveedor": "PROV-REST-01",
                  "ultimoPrecio": 15.75,
                  "diasEntrega": 4
                }
                """;

        mockMvc.perform(post("/api/productos-proveedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(association))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProducto").value(7001));
        mockMvc.perform(get("/api/productos-proveedor/7001,8001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoProveedor").value("PROV-REST-01"));
    }

    @Test
    void userResponsesDoNotExposePasswordHash() throws Exception {
        String userJson = """
                {
                  "username": "rest-user-test",
                  "passwordHash": "sensitive-hash",
                  "activo": true
                }
                """;

        MvcResult createdResult = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn();

        int id = objectMapper.readTree(createdResult.getResponse().getContentAsString())
                .get("idUsuario").asInt();
        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passwordHash").doesNotExist());
    }

    @Test
    void mvcHomeListsRestResources() throws Exception {
        org.junit.jupiter.api.Assertions.assertTrue(resController.resourceNames().contains("productos"));
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/mvc/productos")));
    }

    @Test
    void thymeleafProductScreensSupportCreateReadUpdateAndDelete() throws Exception {
        mockMvc.perform(get("/mvc/productos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.view()
                        .name("resource-form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Crear registro")));

        mockMvc.perform(post("/mvc/productos/guardar")
                        .param("sku", "MVC-CRUD-TEST")
                        .param("nombre", "Producto MVC")
                        .param("unidadMedida", "unidad")
                        .param("controlaSerial", "true")
                        .param("controlaLote", "false")
                        .param("precioCompraRef", "10.25")
                        .param("precioVenta", "20.50")
                        .param("porcentajeIva", "19")
                        .param("activo", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .redirectedUrl("/mvc/productos"));

        ProductoVO created = productoDao.findAll().stream()
                .filter(product -> "MVC-CRUD-TEST".equals(product.sku()))
                .findFirst()
                .orElseThrow();
        String id = created.idProducto().toString();

        mockMvc.perform(get("/mvc/productos"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("MVC-CRUD-TEST")));

        mockMvc.perform(get("/mvc/productos/{id}/editar", id))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.view()
                        .name("resource-form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Producto MVC")));

        mockMvc.perform(post("/mvc/productos/{id}/actualizar", id)
                        .param("idProducto", id)
                        .param("sku", "MVC-CRUD-TEST")
                        .param("nombre", "Producto MVC actualizado")
                        .param("unidadMedida", "unidad")
                        .param("controlaSerial", "true")
                        .param("controlaLote", "false")
                        .param("precioCompraRef", "10.25")
                        .param("precioVenta", "20.50")
                        .param("porcentajeIva", "19")
                        .param("activo", "true"))
                .andExpect(status().is3xxRedirection());

        org.junit.jupiter.api.Assertions.assertEquals(
                "Producto MVC actualizado", productoDao.findById(created.idProducto()).orElseThrow().nombre());

        mockMvc.perform(post("/mvc/productos/{id}/eliminar", id))
                .andExpect(status().is3xxRedirection());
        org.junit.jupiter.api.Assertions.assertTrue(productoDao.findById(created.idProducto()).isEmpty());
    }

    @Test
    void thymeleafCompositeKeyResourceSupportsCrud() throws Exception {
        mockMvc.perform(post("/mvc/productos-proveedor/guardar")
                        .param("idProducto", "7101")
                        .param("idProveedor", "8101")
                        .param("codigoProveedor", "MVC-PROV-01")
                        .param("ultimoPrecio", "17.25")
                        .param("diasEntrega", "5"))
                .andExpect(status().is3xxRedirection());

        org.junit.jupiter.api.Assertions.assertTrue(
                        productoProveedorDao.findById(7101, 8101).isPresent());
        mockMvc.perform(get("/mvc/productos-proveedor/7101,8101/editar"))
                        .andExpect(status().isOk())
                        .andExpect(content().string(org.hamcrest.Matchers.containsString("MVC-PROV-01")));

        mockMvc.perform(post("/mvc/productos-proveedor/7101,8101/actualizar")
                        .param("idProducto", "7101")
                        .param("idProveedor", "8101")
                        .param("codigoProveedor", "MVC-PROV-02")
                        .param("ultimoPrecio", "18.25")
                        .param("diasEntrega", "6"))
                .andExpect(status().is3xxRedirection());
        org.junit.jupiter.api.Assertions.assertEquals(
                "MVC-PROV-02", productoProveedorDao.findById(7101, 8101).orElseThrow().codigoProveedor());

        mockMvc.perform(post("/mvc/productos-proveedor/7101,8101/eliminar"))
                .andExpect(status().is3xxRedirection());
        org.junit.jupiter.api.Assertions.assertTrue(productoProveedorDao.findById(7101, 8101).isEmpty());
    }

    @Test
    void editingUserWithoutNewHashPreservesStoredHash() throws Exception {
        mockMvc.perform(post("/mvc/usuarios/guardar")
                        .param("username", "mvc-user-test")
                        .param("passwordHash", "precomputed-hash")
                        .param("activo", "true"))
                .andExpect(status().is3xxRedirection());

        UsuarioVO created = usuarioDao.findAll().stream()
                .filter(user -> "mvc-user-test".equals(user.username()))
                .findFirst()
                .orElseThrow();
        mockMvc.perform(post("/mvc/usuarios/{id}/actualizar", created.idUsuario())
                        .param("idUsuario", created.idUsuario().toString())
                        .param("username", "mvc-user-test")
                        .param("passwordHash", "")
                        .param("activo", "false"))
                .andExpect(status().is3xxRedirection());

        UsuarioVO updated = usuarioDao.findById(created.idUsuario()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("precomputed-hash", updated.passwordHash());
        org.junit.jupiter.api.Assertions.assertFalse(updated.activo());
    }
}
