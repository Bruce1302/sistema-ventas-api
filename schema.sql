create database bd_ventas;

use bd_ventas;

-- 1. Tabla Categoria
CREATE TABLE categoria (
    id VARCHAR(255) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    activo TINYINT(1) DEFAULT 1,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6)
);

-- 2. Tabla Usuario
CREATE TABLE usuario (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    activo TINYINT(1) DEFAULT 1,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6)
);

-- 3. Tabla Producto
CREATE TABLE producto (
    id VARCHAR(255) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precio DECIMAL(19,2) NOT NULL,
    stock INT NOT NULL,
    categoria_id VARCHAR(255),
    activo TINYINT(1) DEFAULT 1,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id)
);

-- 4. Tabla Venta
CREATE TABLE venta (
    id VARCHAR(255) PRIMARY KEY,
    total DECIMAL(19,2) NOT NULL,
    activo TINYINT(1) DEFAULT 1,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6)
);

-- 5. Tabla Detalle Venta
CREATE TABLE detalle_venta (
    id VARCHAR(255) PRIMARY KEY,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(19,2) NOT NULL,
    sub_total DECIMAL(19,2) NOT NULL,
    venta_id VARCHAR(255),
    producto_id VARCHAR(255),
    activo TINYINT(1) DEFAULT 1,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (venta_id) REFERENCES venta (id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto (id)
);
