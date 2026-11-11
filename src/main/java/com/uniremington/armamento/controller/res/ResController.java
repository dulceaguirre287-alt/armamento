package com.uniremington.armamento.controller.res;

import java.net.URI;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.uniremington.armamento.model.dao.AlmacenDao;
import com.uniremington.armamento.model.dao.AuditoriaDao;
import com.uniremington.armamento.model.dao.CategoriaDao;
import com.uniremington.armamento.model.dao.ClienteDao;
import com.uniremington.armamento.model.dao.CompraDao;
import com.uniremington.armamento.model.dao.DetalleCompraDao;
import com.uniremington.armamento.model.dao.DetalleDevolucionDao;
import com.uniremington.armamento.model.dao.DetalleVentaDao;
import com.uniremington.armamento.model.dao.DevolucionDao;
import com.uniremington.armamento.model.dao.ExistenciaDao;
import com.uniremington.armamento.model.dao.JdbcRecordDao;
import com.uniremington.armamento.model.dao.MarcaDao;
import com.uniremington.armamento.model.dao.MovimientoInventarioDao;
import com.uniremington.armamento.model.dao.PermisoClienteDao;
import com.uniremington.armamento.model.dao.ProductoDao;
import com.uniremington.armamento.model.dao.ProductoProveedorDao;
import com.uniremington.armamento.model.dao.ProveedorDao;
import com.uniremington.armamento.model.dao.RolDao;
import com.uniremington.armamento.model.dao.UnidadSeriadaDao;
import com.uniremington.armamento.model.dao.UsuarioDao;
import com.uniremington.armamento.model.dao.VentaDao;
import com.uniremington.armamento.model.vo.AlmacenVO;
import com.uniremington.armamento.model.vo.AuditoriaVO;
import com.uniremington.armamento.model.vo.CategoriaVO;
import com.uniremington.armamento.model.vo.ClienteVO;
import com.uniremington.armamento.model.vo.CompraVO;
import com.uniremington.armamento.model.vo.DetalleCompraVO;
import com.uniremington.armamento.model.vo.DetalleDevolucionVO;
import com.uniremington.armamento.model.vo.DetalleVentaVO;
import com.uniremington.armamento.model.vo.DevolucionVO;
import com.uniremington.armamento.model.vo.ExistenciaVO;
import com.uniremington.armamento.model.vo.MarcaVO;
import com.uniremington.armamento.model.vo.MovimientoInventarioVO;
import com.uniremington.armamento.model.vo.PermisoClienteVO;
import com.uniremington.armamento.model.vo.ProductoProveedorVO;
import com.uniremington.armamento.model.vo.ProductoVO;
import com.uniremington.armamento.model.vo.ProveedorVO;
import com.uniremington.armamento.model.vo.RolVO;
import com.uniremington.armamento.model.vo.UnidadSeriadaVO;
import com.uniremington.armamento.model.vo.UsuarioVO;
import com.uniremington.armamento.model.vo.VentaVO;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class ResController {
    private final ObjectMapper objectMapper;
    private final Map<String, ResourceHandler> resources;

    public ResController(
            ObjectMapper objectMapper,
            AlmacenDao almacenDao,
            AuditoriaDao auditoriaDao,
            CategoriaDao categoriaDao,
            ClienteDao clienteDao,
            CompraDao compraDao,
            DetalleCompraDao detalleCompraDao,
            DetalleDevolucionDao detalleDevolucionDao,
            DetalleVentaDao detalleVentaDao,
            DevolucionDao devolucionDao,
            ExistenciaDao existenciaDao,
            MarcaDao marcaDao,
            MovimientoInventarioDao movimientoInventarioDao,
            PermisoClienteDao permisoClienteDao,
            ProductoProveedorDao productoProveedorDao,
            ProductoDao productoDao,
            ProveedorDao proveedorDao,
            RolDao rolDao,
            UnidadSeriadaDao unidadSeriadaDao,
            UsuarioDao usuarioDao,
            VentaDao ventaDao) {
        this.objectMapper = objectMapper;
        Map<String, ResourceHandler> endpoints = new LinkedHashMap<>();
        endpoints.put("almacenes", endpoint(almacenDao, AlmacenVO.class,
                key("idAlmacen", Integer.class, AlmacenVO::idAlmacen)));
        endpoints.put("auditorias", endpoint(auditoriaDao, AuditoriaVO.class,
                key("idAuditoria", Long.class, AuditoriaVO::idAuditoria)));
        endpoints.put("categorias", endpoint(categoriaDao, CategoriaVO.class,
                key("idCategoria", Integer.class, CategoriaVO::idCategoria)));
        endpoints.put("clientes", endpoint(clienteDao, ClienteVO.class,
                key("idCliente", Integer.class, ClienteVO::idCliente)));
        endpoints.put("compras", endpoint(compraDao, CompraVO.class,
                key("idCompra", Long.class, CompraVO::idCompra)));
        endpoints.put("detalles-compra", endpoint(detalleCompraDao, DetalleCompraVO.class,
                key("idDetalleCompra", Long.class, DetalleCompraVO::idDetalleCompra)));
        endpoints.put("detalles-devolucion", endpoint(detalleDevolucionDao, DetalleDevolucionVO.class,
                key("idDetalleDevolucion", Long.class, DetalleDevolucionVO::idDetalleDevolucion)));
        endpoints.put("detalles-venta", endpoint(detalleVentaDao, DetalleVentaVO.class,
                key("idDetalleVenta", Long.class, DetalleVentaVO::idDetalleVenta)));
        endpoints.put("devoluciones", endpoint(devolucionDao, DevolucionVO.class,
                key("idDevolucion", Long.class, DevolucionVO::idDevolucion)));
        endpoints.put("existencias", endpoint(existenciaDao, ExistenciaVO.class,
                key("idExistencia", Long.class, ExistenciaVO::idExistencia)));
        endpoints.put("marcas", endpoint(marcaDao, MarcaVO.class,
                key("idMarca", Integer.class, MarcaVO::idMarca)));
        endpoints.put("movimientos-inventario", endpoint(movimientoInventarioDao, MovimientoInventarioVO.class,
                key("idMovimiento", Long.class, MovimientoInventarioVO::idMovimiento)));
        endpoints.put("permisos-cliente", endpoint(permisoClienteDao, PermisoClienteVO.class,
                key("idPermiso", Integer.class, PermisoClienteVO::idPermiso)));
        endpoints.put("productos-proveedor", endpoint(productoProveedorDao, ProductoProveedorVO.class,
                key("idProducto", Integer.class, ProductoProveedorVO::idProducto),
                key("idProveedor", Integer.class, ProductoProveedorVO::idProveedor)));
        endpoints.put("productos", endpoint(productoDao, ProductoVO.class,
                key("idProducto", Integer.class, ProductoVO::idProducto)));
        endpoints.put("proveedores", endpoint(proveedorDao, ProveedorVO.class,
                key("idProveedor", Integer.class, ProveedorVO::idProveedor)));
        endpoints.put("roles", endpoint(rolDao, RolVO.class,
                key("idRol", Integer.class, RolVO::idRol)));
        endpoints.put("unidades-seriadas", endpoint(unidadSeriadaDao, UnidadSeriadaVO.class,
                key("idUnidad", Long.class, UnidadSeriadaVO::idUnidad)));
        endpoints.put("usuarios", endpoint(usuarioDao, UsuarioVO.class,
                key("idUsuario", Integer.class, UsuarioVO::idUsuario)));
        endpoints.put("ventas", endpoint(ventaDao, VentaVO.class,
                key("idVenta", Long.class, VentaVO::idVenta)));
        resources = Map.copyOf(endpoints);
    }

    @GetMapping("/{resource}")
    public List<?> findAll(@PathVariable String resource) {
        return endpointFor(resource).findAll();
    }

    @GetMapping("/{resource}/{id}")
    public ResponseEntity<Object> findById(@PathVariable String resource, @PathVariable String id) {
        return endpointFor(resource).findById(id)
                .map(value -> ResponseEntity.ok((Object) value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{resource}")
    public ResponseEntity<Object> create(
            @PathVariable String resource,
            @RequestBody JsonNode body) {
        CreatedResource created = endpointFor(resource).create(body);
        URI location = URI.create("/api/" + resource + "/" + created.id());
        return ResponseEntity.created(location).body(created.value());
    }

    @PutMapping("/{resource}/{id}")
    public ResponseEntity<Object> update(
            @PathVariable String resource,
            @PathVariable String id,
            @RequestBody JsonNode body) {
        Optional<Object> updated = endpointFor(resource).update(id, body);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{resource}/{id}")
    public ResponseEntity<Void> delete(@PathVariable String resource, @PathVariable String id) {
        return endpointFor(resource).delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    public List<String> resourceNames() {
        return resources.keySet().stream().sorted().toList();
    }

    public List<FieldInfo> resourceFields(String resource) {
        return endpointFor(resource).fields();
    }

    public List<Map<String, String>> resourceRows(String resource) {
        ResourceHandler endpoint = endpointFor(resource);
        List<FieldInfo> fields = endpoint.fields();
        return endpoint.findAll().stream().map(row -> {
            Map<String, String> values = new LinkedHashMap<>();
            values.put("id", endpoint.identifier(row));
            for (FieldInfo field : fields) {
                if (field.displayable()) {
                    JsonNode value = row.get(field.name());
                    values.put(field.name(), value == null || value.isNull() ? "" : value.asText());
                }
            }
            return Map.copyOf(values);
        }).toList();
    }

    public Optional<Map<String, String>> resourceFormValues(String resource, String id) {
        ResourceHandler endpoint = endpointFor(resource);
        return endpoint.findById(id).map(row -> {
            Map<String, String> values = new LinkedHashMap<>();
            for (FieldInfo field : endpoint.fields()) {
                if (field.secret()) {
                    values.put(field.name(), "");
                    continue;
                }
                JsonNode value = row.get(field.name());
                String text = value == null || value.isNull() ? "" : value.asText();
                if ("datetime-local".equals(field.inputType()) && text.length() >= 16) {
                    text = text.substring(0, 16);
                }
                values.put(field.name(), text);
            }
            return Map.copyOf(values);
        });
    }

    public JsonNode formBody(String resource, Map<String, String> form) {
        ObjectNode body = objectMapper.createObjectNode();
        for (FieldInfo field : resourceFields(resource)) {
            if (!form.containsKey(field.name())) {
                continue;
            }
            String value = form.get(field.name());
            if (value == null || value.isBlank()) {
                body.putNull(field.name());
            } else {
                body.put(field.name(), value);
            }
        }
        return body;
    }

    private ResourceHandler endpointFor(String resource) {
        ResourceHandler endpoint = resources.get(resource);
        if (endpoint == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado");
        }
        return endpoint;
    }

    @SafeVarargs
    private final <T> ResourceHandler endpoint(
            JdbcRecordDao<T> dao,
            Class<T> recordType,
            KeyPart<T>... keyParts) {
        return new RecordResource<>(dao, recordType, objectMapper, List.of(keyParts));
    }

    private static <T, K> KeyPart<T> key(String name, Class<K> type, Function<T, K> accessor) {
        return new KeyPart<>(name, type, accessor);
    }

    private record KeyPart<T>(String name, Class<?> type, Function<T, ?> accessor) {
    }

    public record FieldInfo(
            String name,
            String label,
            String inputType,
            boolean primaryKey,
            boolean generated,
            boolean required,
            boolean secret,
            boolean displayable) {
    }

    private record CreatedResource(Object value, String id) {
    }

    private interface ResourceHandler {
        List<JsonNode> findAll();

        Optional<JsonNode> findById(String id);

        CreatedResource create(JsonNode body);

        Optional<Object> update(String id, JsonNode body);

        boolean delete(String id);

        List<FieldInfo> fields();

        String identifier(JsonNode row);
    }

    private static final class RecordResource<T> implements ResourceHandler {
        private final JdbcRecordDao<T> dao;
        private final Class<T> recordType;
        private final ObjectMapper objectMapper;
        private final List<KeyPart<T>> keyParts;

        private RecordResource(
                JdbcRecordDao<T> dao,
                Class<T> recordType,
                ObjectMapper objectMapper,
                List<KeyPart<T>> keyParts) {
            this.dao = dao;
            this.recordType = recordType;
            this.objectMapper = objectMapper;
            this.keyParts = keyParts;
        }

        @Override
        public List<JsonNode> findAll() {
            return dao.findAll().stream().map(this::responseValue).toList();
        }

        @Override
        public Optional<JsonNode> findById(String id) {
            Object[] keyValues = parseId(id);
            return dao.findById(keyValues).map(this::responseValue);
        }

        @Override
        public CreatedResource create(JsonNode body) {
            T record = readRecord(body);
            Object[] requestedKey = keyValues(record);
            boolean hasCompleteKey = Arrays.stream(requestedKey).allMatch(Objects::nonNull);
            if (keyParts.size() > 1 && !hasCompleteKey) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El cuerpo debe incluir todos los campos de la clave primaria");
            }
            if (hasCompleteKey && dao.findById(requestedKey).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un registro con esa clave");
            }
            T saved = dao.save(record);
            Object[] keyValues = keyValues(saved);
            if (Arrays.stream(keyValues).anyMatch(Objects::isNull)) {
                throw new IllegalStateException("El DAO no devolvió todos los valores de la clave primaria");
            }
            return new CreatedResource(responseValue(saved), formatId(keyValues));
        }

        @Override
        public Optional<Object> update(String id, JsonNode body) {
            Object[] requestedKey = parseId(id);
            T record = readRecord(body);
            Object[] bodyKey = keyValues(record);
            if (!Arrays.deepEquals(requestedKey, bodyKey)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La clave del cuerpo debe coincidir con la ruta");
            }
            if (dao.findById(requestedKey).isEmpty()) {
                return Optional.empty();
            }
            record = preserveExistingPasswordHash(record, requestedKey);
            return Optional.of(responseValue(dao.save(record)));
        }

        @Override
        public boolean delete(String id) {
            return dao.deleteById(parseId(id));
        }

        @Override
        public List<FieldInfo> fields() {
            List<FieldInfo> fields = new ArrayList<>();
            for (var component : recordType.getRecordComponents()) {
                String name = component.getName();
                boolean primaryKey = keyParts.stream().anyMatch(part -> part.name().equals(name));
                boolean generated = primaryKey && keyParts.size() == 1;
                boolean secret = name.toLowerCase().contains("password") || name.toLowerCase().contains("hash");
                fields.add(new FieldInfo(
                        name,
                        label(name),
                        inputType(component.getType(), name),
                        primaryKey,
                        generated,
                        primaryKey && !generated || secret && !generated,
                        secret,
                        !secret));
            }
            return List.copyOf(fields);
        }

        @Override
        public String identifier(JsonNode row) {
            return String.join(",", keyParts.stream()
                    .map(part -> row.path(part.name()).asText())
                    .toList());
        }

        private static String inputType(Class<?> type, String fieldName) {
            if (type == Boolean.class || type == boolean.class) {
                return "boolean";
            }
            if (type == LocalDate.class) {
                return "date";
            }
            if (type == LocalDateTime.class) {
                return "datetime-local";
            }
            if (Number.class.isAssignableFrom(type)) {
                return type == BigDecimal.class ? "number-decimal" : "number";
            }
            if (fieldName.toLowerCase().contains("password") || fieldName.toLowerCase().contains("hash")) {
                return "password";
            }
            if (List.of("descripcion", "observaciones", "datosAntes", "datosDespues").contains(fieldName)) {
                return "textarea";
            }
            return "text";
        }

        private static String label(String name) {
            String spaced = name.replaceAll("([a-z0-9])([A-Z])", "$1 $2");
            return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
        }

        private T readRecord(JsonNode body) {
            try {
                T record = objectMapper.treeToValue(body, recordType);
                if (record == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo no puede ser null");
                }
                return record;
            } catch (JacksonException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo no coincide con el VO", exception);
            }
        }

        private T preserveExistingPasswordHash(T record, Object[] requestedKey) {
            if (recordType != UsuarioVO.class) {
                return record;
            }
            UsuarioVO incoming = (UsuarioVO) record;
            if (incoming.passwordHash() != null && !incoming.passwordHash().isBlank()) {
                return record;
            }
            UsuarioVO existing = (UsuarioVO) dao.findById(requestedKey).orElseThrow();
            ObjectNode merged = objectMapper.valueToTree(record);
            if (existing.passwordHash() == null) {
                merged.putNull("passwordHash");
            } else {
                merged.put("passwordHash", existing.passwordHash());
            }
            try {
                return objectMapper.treeToValue(merged, recordType);
            } catch (JacksonException exception) {
                throw new IllegalStateException("Could not preserve existing password hash", exception);
            }
        }

        private Object[] parseId(String id) {
            String[] parts = id.split(",", -1);
            if (parts.length != keyParts.size() || Arrays.stream(parts).anyMatch(String::isBlank)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de clave primaria inválido");
            }
            try {
                Object[] values = new Object[parts.length];
                for (int index = 0; index < parts.length; index++) {
                    values[index] = objectMapper.convertValue(parts[index], keyParts.get(index).type());
                }
                return values;
            } catch (IllegalArgumentException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de clave primaria inválido", exception);
            }
        }

        private Object[] keyValues(T record) {
            return keyParts.stream().map(part -> part.accessor().apply(record)).toArray();
        }

        private JsonNode responseValue(T record) {
            ObjectNode response = objectMapper.valueToTree(record);
            if (recordType == UsuarioVO.class) {
                response.remove("passwordHash");
            }
            return response;
        }

        private String formatId(Object[] keyValues) {
            return String.join(",", Arrays.stream(keyValues).map(String::valueOf).toList());
        }
    }
}
