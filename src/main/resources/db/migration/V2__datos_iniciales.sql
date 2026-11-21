DROP PROCEDURE IF EXISTS seed_armamento_initial_data;
DELIMITER $$
CREATE PROCEDURE seed_armamento_initial_data()
BEGIN
IF NOT EXISTS (
    SELECT 1
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = 'inventario_armamento' AND TABLE_NAME = 'roles'
) THEN
INSERT INTO rol (nombre, descripcion) VALUES
    ('ADMINISTRADOR', 'Acceso total a la administración del sistema'),
    ('VENDEDOR', 'Gestión de clientes y ventas'),
    ('ALMACENISTA', 'Gestión de inventario, compras y devoluciones'),
    ('AUDITOR', 'Consulta de auditorías y movimientos');

INSERT INTO categoria (nombre, tipo_control, requiere_permiso, descripcion) VALUES
    ('Armas', 'SERIAL', TRUE, 'Armas sujetas a control y autorización'),
    ('Munición', 'LOTE', TRUE, 'Munición controlada por lote'),
    ('Accesorios', 'GENERAL', FALSE, 'Accesorios y elementos complementarios');

INSERT INTO marca (nombre, pais_origen, activo) VALUES
    ('Andina Tactical', 'Colombia', TRUE),
    ('Protección Nacional', 'Colombia', TRUE);

INSERT INTO cliente (tipo_cliente, tipo_documento, numero_documento, nombre_razon, email, telefono, ciudad, activo) VALUES
    ('PERSONA', 'CC', '1000000001', 'Cliente de demostración', 'cliente@example.test', '3000000001', 'Medellín', TRUE),
    ('EMPRESA', 'NIT', '900000001-1', 'Distribuciones Seguras S.A.S.', 'compras@example.test', '6040000000', 'Bogotá', TRUE);

INSERT INTO proveedor (razon_social, nit, numero_licencia, licencia_vence, contacto_nombre, email, telefono, ciudad, pais, condiciones_pago, activo) VALUES
    ('Suministros Andinos S.A.S.', '900000002-2', 'LIC-PRV-001', '2030-12-31', 'María López', 'ventas@example.test', '6010000000', 'Bogotá', 'Colombia', '30 días', TRUE);

INSERT INTO producto (sku, nombre, descripcion, id_categoria, id_marca, modelo, calibre, unidad_medida, controla_serial, controla_lote, precio_compra_ref, precio_venta, porcentaje_iva, stock_minimo, activo) VALUES
    ('ARM-001', 'Elemento controlado de demostración', 'Registro de muestra para validación del inventario.', 1, 1, 'AT-01', '9 mm', 'unidad', TRUE, FALSE, 1800000.00, 2200000.00, 19.00, 2, TRUE),
    ('MUN-001', 'Munición de demostración', 'Registro de muestra controlado por lote.', 2, 2, 'PN-09', '9 mm', 'caja', FALSE, TRUE, 90000.00, 120000.00, 19.00, 10, TRUE),
    ('ACC-001', 'Accesorio de demostración', 'Elemento de catálogo de muestra.', 3, 1, 'AT-A1', NULL, 'unidad', FALSE, FALSE, 50000.00, 75000.00, 19.00, 5, TRUE);

INSERT INTO almacen (nombre, direccion, ciudad, activo) VALUES
    ('Almacén principal', 'Calle 10 # 20-30', 'Medellín', TRUE),
    ('Almacén secundario', 'Carrera 7 # 12-45', 'Bogotá', TRUE);

INSERT INTO producto_proveedor (id_producto, id_proveedor, codigo_proveedor, ultimo_precio, dias_entrega) VALUES
    (1, 1, 'PROV-ARM-001', 1800000.00, 15),
    (2, 1, 'PROV-MUN-001', 90000.00, 7),
    (3, 1, 'PROV-ACC-001', 50000.00, 5);

INSERT INTO permiso_cliente (id_cliente, id_categoria, tipo_permiso, numero_permiso, autoridad_emisora, fecha_expedicion, fecha_vencimiento, estado) VALUES
    (1, 1, 'COMPRA', 'PERM-DEMO-001', 'Autoridad de control', '2026-01-01', '2027-01-01', 'VIGENTE'),
    (1, 2, 'COMPRA', 'PERM-DEMO-002', 'Autoridad de control', '2026-01-01', '2027-01-01', 'VIGENTE');

INSERT INTO existencia (id_producto, id_almacen, lote, cantidad) VALUES
    (1, 1, '', 3),
    (2, 1, 'LOTE-DEMO-01', 25),
    (3, 1, '', 12),
    (2, 2, 'LOTE-DEMO-02', 10);

INSERT INTO unidad_seriada
    (id_producto, numero_serie, id_almacen, estado, fecha_ingreso, observaciones)
VALUES
    (1, 'SERIE-DEMO-001', 1, 'DISPONIBLE', CURRENT_TIMESTAMP(6), 'Unidad de prueba'),
    (1, 'SERIE-DEMO-002', 1, 'DISPONIBLE', CURRENT_TIMESTAMP(6), 'Unidad de prueba'),
    (1, 'SERIE-DEMO-003', 1, 'DISPONIBLE', CURRENT_TIMESTAMP(6), 'Unidad de prueba');
END IF;
END$$
CALL seed_armamento_initial_data()$$
DROP PROCEDURE seed_armamento_initial_data$$
DELIMITER ;
