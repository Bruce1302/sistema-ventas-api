create database bd_ventas;

use bd_ventas;

-- 1. Tabla Detalle Venta
CREATE TABLE tienda (
    id VARCHAR(255) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    codigo VARCHAR(255) NOT NULL UNIQUE,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(255),
    facturacion_activa TINYINT(1) DEFAULT 0,
    ruc VARCHAR(11),
    razon_social VARCHAR(255),
    direccion_fiscal VARCHAR(255),
    token_facturacion VARCHAR(255),
    serie_boleta VARCHAR(4),
    serie_factura VARCHAR(4),
    activo TINYINT(1) DEFAULT 1,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6)
);

-- 2. Tabla Categoria
CREATE TABLE categoria (
    id VARCHAR(255) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    activo TINYINT(1) DEFAULT 1,
    tienda_id VARCHAR(255),
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6),
    CONSTRAINT fk_categoria_tienda FOREIGN KEY (tienda_id) REFERENCES tienda (id)
);

-- 3. Tabla Usuario
CREATE TABLE usuario (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    tienda_id VARCHAR(255),
    activo TINYINT(1) DEFAULT 1,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6),
    CONSTRAINT fk_usuario_tienda FOREIGN KEY (tienda_id) REFERENCES tienda (id)
);

-- 4. Tabla Producto
CREATE TABLE producto (
    id VARCHAR(255) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    precio DECIMAL(19,2) NOT NULL,
    stock INT NOT NULL,
    categoria_id VARCHAR(255),
    tienda_id VARCHAR(255),
    activo TINYINT(1) DEFAULT 1,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT fk_producto_tienda FOREIGN KEY (tienda_id) REFERENCES tienda (id)
);


-- 5. Tabla Venta
CREATE TABLE venta (
    id VARCHAR(255) PRIMARY KEY,
    total DECIMAL(19,2) NOT NULL,
    tienda_id VARCHAR(255),
    tipo_comprobante VARCHAR(20) DEFAULT 'NOTA_VENTA',
    serie_numero VARCHAR(20),
    external_id VARCHAR(100),
    estado_sunat VARCHAR(20),
    cliente_tipo_doc VARCHAR(2),
    cliente_num_doc VARCHAR(15),
    cliente_nombre VARCHAR(255),
    idempotency_key VARCHAR(100),
    activo TINYINT(1) DEFAULT 1,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6),
    CONSTRAINT fk_venta_tienda FOREIGN KEY (tienda_id) REFERENCES tienda (id),
    CONSTRAINT uk_venta_tienda_idempotency UNIQUE (tienda_id, idempotency_key)

);

-- 6. Tabla Detalle Venta
CREATE TABLE detalle_venta (
    id VARCHAR(255) PRIMARY KEY,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(19,2) NOT NULL,
    sub_total DECIMAL(19,2) NOT NULL,
    venta_id VARCHAR(255),
    producto_id VARCHAR(255),
    tienda_id VARCHAR(255),
    activo TINYINT(1) DEFAULT 1,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (venta_id) REFERENCES venta (id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT fk_detalle_tienda FOREIGN KEY (tienda_id) REFERENCES tienda (id)

);


-- PostGreSQL
-- 1. Tabla tienda
CREATE TABLE tienda (
    id VARCHAR(255) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    codigo VARCHAR(255) NOT NULL UNIQUE,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(255),
    facturacion_activa BOOLEAN DEFAULT FALSE,
    ruc VARCHAR(11),
    razon_social VARCHAR(255),
    direccion_fiscal VARCHAR(255),
    token_facturacion VARCHAR(255),
    serie_boleta VARCHAR(4),
    serie_factura VARCHAR(4),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6),
    fecha_actualizacion TIMESTAMP(6)
);

-- 2. Tabla Categoria
CREATE TABLE categoria (
    id VARCHAR(255) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    tienda_id VARCHAR(255),
    fecha_creacion TIMESTAMP(6),
    fecha_actualizacion TIMESTAMP(6),
    CONSTRAINT fk_categoria_tienda FOREIGN KEY (tienda_id) REFERENCES tienda (id)
);

-- 3. Tabla Usuario
CREATE TABLE usuario (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    tienda_id VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6),
    fecha_actualizacion TIMESTAMP(6),
    CONSTRAINT fk_usuario_tienda FOREIGN KEY (tienda_id) REFERENCES tienda (id)
);

-- 4. Tabla Producto
CREATE TABLE producto (
    id VARCHAR(255) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    precio DECIMAL(19,2) NOT NULL,
    stock INT NOT NULL,
    categoria_id VARCHAR(255),
    tienda_id VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6),
    fecha_actualizacion TIMESTAMP(6),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT fk_producto_tienda FOREIGN KEY (tienda_id) REFERENCES tienda (id)
);

-- 5. Tabla Venta
CREATE TABLE venta (
    id VARCHAR(255) PRIMARY KEY,
    total DECIMAL(19,2) NOT NULL,
    tienda_id VARCHAR(255),
    tipo_comprobante VARCHAR(20) DEFAULT 'NOTA_VENTA',
    serie_numero VARCHAR(20),
    external_id VARCHAR(100),
    estado_sunat VARCHAR(20),
    cliente_tipo_doc VARCHAR(2),
    cliente_num_doc VARCHAR(15),
    cliente_nombre VARCHAR(255),
    idempotency_key VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6),
    fecha_actualizacion TIMESTAMP(6),
    CONSTRAINT fk_venta_tienda FOREIGN KEY (tienda_id) REFERENCES tienda (id),
    CONSTRAINT uk_venta_tienda_idempotency UNIQUE (tienda_id, idempotency_key)
);

-- 6. Tabla Detalle Venta
CREATE TABLE detalle_venta (
    id VARCHAR(255) PRIMARY KEY,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(19,2) NOT NULL,
    sub_total DECIMAL(19,2) NOT NULL,
    venta_id VARCHAR(255),
    producto_id VARCHAR(255),
    tienda_id VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6),
    fecha_actualizacion TIMESTAMP(6),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (venta_id) REFERENCES venta (id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT fk_detalle_tienda FOREIGN KEY (tienda_id) REFERENCES tienda (id)
);




