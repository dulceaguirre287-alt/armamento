CREATE TABLE rol (
    id_rol INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    descripcion VARCHAR(500),
    PRIMARY KEY (id_rol),
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuario (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    id_rol INT NOT NULL,
    tipo_documento VARCHAR(30),
    numero_documento VARCHAR(50),
    nombres VARCHAR(120) NOT NULL,
    apellidos VARCHAR(120) NOT NULL,
    email VARCHAR(190) NOT NULL,
    telefono VARCHAR(30),
    username VARCHAR(80) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    intentos_fallidos INT NOT NULL DEFAULT 0,
    bloqueado_hasta DATETIME(6),
    ultimo_acceso DATETIME(6),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    actualizado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_usuario),
    CONSTRAINT uk_usuario_username UNIQUE (username),
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT uk_usuario_documento UNIQUE (tipo_documento, numero_documento),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_usuario_nombre (apellidos, nombres),
    INDEX idx_usuario_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE almacen (
    id_almacen INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(250),
    ciudad VARCHAR(100),
    responsable INT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_almacen),
    CONSTRAINT uk_almacen_nombre UNIQUE (nombre),
    INDEX idx_almacen_ciudad (ciudad),
    CONSTRAINT fk_almacen_responsable FOREIGN KEY (responsable) REFERENCES usuario (id_usuario)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE auditoria (
    id_auditoria BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario INT,
    accion VARCHAR(40) NOT NULL,
    tabla VARCHAR(80) NOT NULL,
    registro_id VARCHAR(80),
    datos_antes LONGTEXT,
    datos_despues LONGTEXT,
    ip_origen VARCHAR(45),
    fecha DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_auditoria),
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_auditoria_modulo_fecha (tabla, fecha),
    INDEX idx_auditoria_usuario_fecha (id_usuario, fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categoria (
    id_categoria INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    tipo_control VARCHAR(50),
    requiere_permiso BOOLEAN NOT NULL DEFAULT FALSE,
    descripcion VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_categoria),
    CONSTRAINT uk_categoria_nombre UNIQUE (nombre),
    INDEX idx_categoria_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE marca (
    id_marca INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    pais_origen VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_marca),
    CONSTRAINT uk_marca_nombre UNIQUE (nombre),
    INDEX idx_marca_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cliente (
    id_cliente INT NOT NULL AUTO_INCREMENT,
    tipo_cliente VARCHAR(40) NOT NULL,
    tipo_documento VARCHAR(30) NOT NULL,
    numero_documento VARCHAR(50) NOT NULL,
    nombre_razon VARCHAR(180) NOT NULL,
    email VARCHAR(190),
    telefono VARCHAR(30),
    direccion VARCHAR(250),
    ciudad VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_cliente),
    CONSTRAINT uk_cliente_documento UNIQUE (tipo_documento, numero_documento),
    INDEX idx_cliente_nombre (nombre_razon),
    INDEX idx_cliente_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE proveedor (
    id_proveedor INT NOT NULL AUTO_INCREMENT,
    razon_social VARCHAR(180) NOT NULL,
    nit VARCHAR(30) NOT NULL,
    numero_licencia VARCHAR(80),
    licencia_vence DATE,
    contacto_nombre VARCHAR(120),
    email VARCHAR(190),
    telefono VARCHAR(30),
    direccion VARCHAR(250),
    ciudad VARCHAR(100),
    pais VARCHAR(100),
    condiciones_pago VARCHAR(150),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    actualizado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_proveedor),
    CONSTRAINT uk_proveedor_nit UNIQUE (nit),
    INDEX idx_proveedor_razon_social (razon_social),
    INDEX idx_proveedor_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE producto (
    id_producto INT NOT NULL AUTO_INCREMENT,
    sku VARCHAR(80) NOT NULL,
    nombre VARCHAR(180) NOT NULL,
    descripcion VARCHAR(1000),
    id_categoria INT NOT NULL,
    id_marca INT,
    modelo VARCHAR(100),
    calibre VARCHAR(50),
    unidad_medida VARCHAR(30) NOT NULL DEFAULT 'unidad',
    controla_serial BOOLEAN NOT NULL DEFAULT FALSE,
    controla_lote BOOLEAN NOT NULL DEFAULT FALSE,
    precio_compra_ref DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    precio_venta DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    porcentaje_iva DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    stock_minimo INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    actualizado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_producto),
    CONSTRAINT uk_producto_sku UNIQUE (sku),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_producto_marca FOREIGN KEY (id_marca) REFERENCES marca (id_marca)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_producto_nombre (nombre),
    INDEX idx_producto_categoria_activo (id_categoria, activo),
    INDEX idx_producto_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE producto_proveedor (
    id_producto INT NOT NULL,
    id_proveedor INT NOT NULL,
    codigo_proveedor VARCHAR(80),
    ultimo_precio DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    dias_entrega SMALLINT,
    PRIMARY KEY (id_producto, id_proveedor),
    CONSTRAINT fk_producto_proveedor_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_producto_proveedor_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_producto_proveedor_proveedor (id_proveedor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE permiso_cliente (
    id_permiso INT NOT NULL AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    id_categoria INT NOT NULL,
    tipo_permiso VARCHAR(60) NOT NULL,
    numero_permiso VARCHAR(80) NOT NULL,
    autoridad_emisora VARCHAR(150),
    fecha_expedicion DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'VIGENTE',
    PRIMARY KEY (id_permiso),
    CONSTRAINT uk_permiso_numero UNIQUE (numero_permiso),
    CONSTRAINT fk_permiso_cliente_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_permiso_cliente_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_permiso_cliente_estado (id_cliente, estado, fecha_vencimiento),
    INDEX idx_permiso_categoria (id_categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE compra (
    id_compra BIGINT NOT NULL AUTO_INCREMENT,
    numero_compra VARCHAR(60) NOT NULL,
    id_proveedor INT NOT NULL,
    id_almacen INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_compra DATE NOT NULL,
    fecha_recepcion DATETIME(6),
    numero_factura_proveedor VARCHAR(80),
    guia_transporte VARCHAR(80),
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    descuento DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    iva DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(30) NOT NULL DEFAULT 'BORRADOR',
    observaciones VARCHAR(1000),
    creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_compra),
    CONSTRAINT uk_compra_numero UNIQUE (numero_compra),
    CONSTRAINT fk_compra_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_compra_almacen FOREIGN KEY (id_almacen) REFERENCES almacen (id_almacen)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_compra_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_compra_fecha_estado (fecha_compra, estado),
    INDEX idx_compra_proveedor (id_proveedor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE detalle_compra (
    id_detalle_compra BIGINT NOT NULL AUTO_INCREMENT,
    id_compra BIGINT NOT NULL,
    id_producto INT NOT NULL,
    cantidad_pedida INT NOT NULL,
    cantidad_recibida INT NOT NULL DEFAULT 0,
    precio_unitario DECIMAL(12,2) NOT NULL,
    porcentaje_iva DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    lote VARCHAR(80),
    fecha_vencimiento DATE,
    subtotal DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id_detalle_compra),
    CONSTRAINT fk_detalle_compra_compra FOREIGN KEY (id_compra) REFERENCES compra (id_compra)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_compra_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_detalle_compra_producto (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE venta (
    id_venta BIGINT NOT NULL AUTO_INCREMENT,
    numero_venta VARCHAR(60) NOT NULL,
    id_cliente INT NOT NULL,
    id_usuario INT NOT NULL,
    id_almacen INT NOT NULL,
    id_permiso INT,
    fecha_venta DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    descuento DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    iva DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    metodo_pago VARCHAR(40) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    autorizada_por INT,
    fecha_autorizacion DATETIME(6),
    observaciones VARCHAR(1000),
    PRIMARY KEY (id_venta),
    CONSTRAINT uk_venta_numero UNIQUE (numero_venta),
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_almacen FOREIGN KEY (id_almacen) REFERENCES almacen (id_almacen)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_permiso FOREIGN KEY (id_permiso) REFERENCES permiso_cliente (id_permiso)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_autorizador FOREIGN KEY (autorizada_por) REFERENCES usuario (id_usuario)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_venta_fecha_estado (fecha_venta, estado),
    INDEX idx_venta_cliente (id_cliente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE venta_permiso (
    id_venta BIGINT NOT NULL,
    id_permiso INT NOT NULL,
    PRIMARY KEY (id_venta, id_permiso),
    CONSTRAINT fk_venta_permiso_venta FOREIGN KEY (id_venta) REFERENCES venta (id_venta)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_venta_permiso_permiso FOREIGN KEY (id_permiso) REFERENCES permiso_cliente (id_permiso)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_venta_permiso_permiso (id_permiso)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE detalle_venta (
    id_detalle_venta BIGINT NOT NULL AUTO_INCREMENT,
    id_venta BIGINT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    descuento DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    porcentaje_iva DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    lote VARCHAR(80),
    subtotal DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id_detalle_venta),
    CONSTRAINT fk_detalle_venta_venta FOREIGN KEY (id_venta) REFERENCES venta (id_venta)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_venta_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_detalle_venta_producto (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE devolucion (
    id_devolucion BIGINT NOT NULL AUTO_INCREMENT,
    numero_devolucion VARCHAR(60) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    id_venta BIGINT,
    id_compra BIGINT,
    id_almacen INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_devolucion DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    motivo VARCHAR(250) NOT NULL,
    descripcion VARCHAR(1000),
    total_reembolso DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    aprobada_por INT,
    PRIMARY KEY (id_devolucion),
    CONSTRAINT uk_devolucion_numero UNIQUE (numero_devolucion),
    CONSTRAINT fk_devolucion_venta FOREIGN KEY (id_venta) REFERENCES venta (id_venta)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_devolucion_compra FOREIGN KEY (id_compra) REFERENCES compra (id_compra)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_devolucion_almacen FOREIGN KEY (id_almacen) REFERENCES almacen (id_almacen)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_devolucion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_devolucion_aprobador FOREIGN KEY (aprobada_por) REFERENCES usuario (id_usuario)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_devolucion_fecha_estado (fecha_devolucion, estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE detalle_devolucion (
    id_detalle_devolucion BIGINT NOT NULL AUTO_INCREMENT,
    id_devolucion BIGINT NOT NULL,
    id_producto INT NOT NULL,
    id_unidad BIGINT,
    lote VARCHAR(80),
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    condicion VARCHAR(60) NOT NULL,
    reingresa_stock BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_detalle_devolucion),
    CONSTRAINT fk_detalle_devolucion_devolucion FOREIGN KEY (id_devolucion) REFERENCES devolucion (id_devolucion)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_devolucion_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_detalle_devolucion_producto (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE existencia (
    id_existencia BIGINT NOT NULL AUTO_INCREMENT,
    id_producto INT NOT NULL,
    id_almacen INT NOT NULL,
    lote VARCHAR(80) NOT NULL DEFAULT '',
    fecha_vencimiento DATE,
    cantidad INT NOT NULL DEFAULT 0,
    actualizado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_existencia),
    CONSTRAINT uk_existencia_producto_almacen_lote UNIQUE (id_producto, id_almacen, lote),
    CONSTRAINT fk_existencia_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_existencia_almacen FOREIGN KEY (id_almacen) REFERENCES almacen (id_almacen)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_existencia_almacen (id_almacen),
    INDEX idx_existencia_cantidad (cantidad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE movimiento_inventario (
    id_movimiento BIGINT NOT NULL AUTO_INCREMENT,
    id_producto INT NOT NULL,
    id_almacen INT NOT NULL,
    id_unidad BIGINT,
    lote VARCHAR(80),
    tipo_movimiento VARCHAR(30) NOT NULL,
    cantidad INT NOT NULL,
    saldo_posterior INT NOT NULL,
    costo_unitario DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    referencia_tipo VARCHAR(30) NOT NULL,
    referencia_id BIGINT NOT NULL,
    id_usuario INT NOT NULL,
    fecha DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    observaciones VARCHAR(500),
    PRIMARY KEY (id_movimiento),
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_movimiento_almacen FOREIGN KEY (id_almacen) REFERENCES almacen (id_almacen)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_movimiento_fecha (fecha),
    INDEX idx_movimiento_producto_almacen (id_producto, id_almacen),
    INDEX idx_movimiento_referencia (referencia_tipo, referencia_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE unidad_seriada (
    id_unidad BIGINT NOT NULL AUTO_INCREMENT,
    id_producto INT NOT NULL,
    numero_serie VARCHAR(100) NOT NULL,
    id_almacen INT NOT NULL,
    id_detalle_compra BIGINT,
    id_detalle_venta BIGINT,
    estado VARCHAR(30) NOT NULL DEFAULT 'DISPONIBLE',
    fecha_ingreso DATETIME(6),
    fecha_salida DATETIME(6),
    observaciones VARCHAR(500),
    PRIMARY KEY (id_unidad),
    CONSTRAINT uk_unidad_seriada_numero UNIQUE (numero_serie),
    CONSTRAINT fk_unidad_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_unidad_almacen FOREIGN KEY (id_almacen) REFERENCES almacen (id_almacen)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_unidad_detalle_compra FOREIGN KEY (id_detalle_compra) REFERENCES detalle_compra (id_detalle_compra)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_unidad_detalle_venta FOREIGN KEY (id_detalle_venta) REFERENCES detalle_venta (id_detalle_venta)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_unidad_producto_estado (id_producto, estado),
    INDEX idx_unidad_almacen (id_almacen)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE detalle_devolucion
    ADD CONSTRAINT fk_detalle_devolucion_unidad FOREIGN KEY (id_unidad) REFERENCES unidad_seriada (id_unidad)
        ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE movimiento_inventario
    ADD CONSTRAINT fk_movimiento_unidad FOREIGN KEY (id_unidad) REFERENCES unidad_seriada (id_unidad)
        ON DELETE RESTRICT ON UPDATE CASCADE;
