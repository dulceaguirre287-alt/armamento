DROP PROCEDURE IF EXISTS importar_catalogos_inventario_armamento;
DELIMITER $$
CREATE PROCEDURE importar_catalogos_inventario_armamento()
migracion: BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.TABLES
        WHERE TABLE_SCHEMA = 'inventario_armamento' AND TABLE_NAME = 'roles'
    ) THEN
        LEAVE migracion;
    END IF;

    IF EXISTS (SELECT 1 FROM inventario_armamento.usuarios)
        OR EXISTS (SELECT 1 FROM inventario_armamento.auditoria)
        OR EXISTS (SELECT 1 FROM inventario_armamento.clientes)
        OR EXISTS (SELECT 1 FROM inventario_armamento.marcas)
        OR EXISTS (SELECT 1 FROM inventario_armamento.proveedores)
        OR EXISTS (SELECT 1 FROM inventario_armamento.productos)
        OR EXISTS (SELECT 1 FROM inventario_armamento.producto_proveedor)
        OR EXISTS (SELECT 1 FROM inventario_armamento.permisos_cliente)
        OR EXISTS (SELECT 1 FROM inventario_armamento.compras)
        OR EXISTS (SELECT 1 FROM inventario_armamento.detalle_compras)
        OR EXISTS (SELECT 1 FROM inventario_armamento.ventas)
        OR EXISTS (SELECT 1 FROM inventario_armamento.detalle_ventas)
        OR EXISTS (SELECT 1 FROM inventario_armamento.devoluciones)
        OR EXISTS (SELECT 1 FROM inventario_armamento.detalle_devoluciones)
        OR EXISTS (SELECT 1 FROM inventario_armamento.existencias)
        OR EXISTS (SELECT 1 FROM inventario_armamento.movimientos_inventario)
        OR EXISTS (SELECT 1 FROM inventario_armamento.unidades_seriadas) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La base inventario_armamento tiene datos operativos que requieren una migracion revisada.';
    END IF;

    INSERT INTO rol (id_rol, nombre, descripcion)
    SELECT id_rol, nombre, descripcion
    FROM inventario_armamento.roles
    ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);

    INSERT INTO categoria (id_categoria, nombre, tipo_control, requiere_permiso, descripcion, activo)
    SELECT id_categoria, nombre, tipo_control, requiere_permiso, descripcion, activo
    FROM inventario_armamento.categorias
    ON DUPLICATE KEY UPDATE
        nombre = VALUES(nombre),
        tipo_control = VALUES(tipo_control),
        requiere_permiso = VALUES(requiere_permiso),
        descripcion = VALUES(descripcion),
        activo = VALUES(activo);

    INSERT INTO almacen (id_almacen, nombre, direccion, ciudad, responsable, activo)
    SELECT id_almacen, nombre, direccion, ciudad, responsable, activo
    FROM inventario_armamento.almacenes
    ON DUPLICATE KEY UPDATE
        nombre = VALUES(nombre),
        direccion = VALUES(direccion),
        ciudad = VALUES(ciudad),
        responsable = VALUES(responsable),
        activo = VALUES(activo);

    INSERT INTO rol (nombre, descripcion)
    VALUES ('ALMACENISTA', 'Gestión de inventario, compras y devoluciones')
    ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);
END$$
CALL importar_catalogos_inventario_armamento()$$
DROP PROCEDURE importar_catalogos_inventario_armamento$$
DELIMITER ;
