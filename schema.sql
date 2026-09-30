CREATE TABLE IF NOT EXISTS cargo (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    salario_base DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS categoria (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS empleado (
    id SERIAL PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    cargo_id INTEGER REFERENCES cargo(id),
    fecha_contratacion DATE,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS producto (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE,
    nombre VARCHAR(150) UNIQUE NOT NULL,
    descripcion TEXT,
    categoria_id INTEGER REFERENCES categoria(id),
    precio_venta DECIMAL(10,2) NOT NULL,
    existencia INTEGER NOT NULL,
    ruta_imagen VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Insertando datos de muestra
INSERT INTO categoria (nombre, activa) VALUES ('Lacteos', true), ('Bebidas', true), ('Limpieza', true) ON CONFLICT (nombre) DO NOTHING;
INSERT INTO cargo (nombre, descripcion, salario_base) VALUES ('Gerente General', 'Administracion', 30000.0), ('Vendedor', 'Ventas', 10000.0), ('Cajero', 'Finanzas', 12000.0) ON CONFLICT (nombre) DO NOTHING;