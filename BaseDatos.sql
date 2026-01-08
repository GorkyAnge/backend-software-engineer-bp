-- =====================================================
-- Script de Base de Datos - Sistema Banco Pichincha
-- Motor: Microsoft SQL Server 2022
-- Fecha: 2026-01-08
-- Arquitectura: Hexagonal
-- =====================================================

-- Verificar y crear base de datos
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'BancoPichincha')
BEGIN
    CREATE DATABASE BancoPichincha;
END
GO

USE BancoPichincha;
GO

-- =====================================================
-- TABLA: clientes
-- Descripción: Almacena información de clientes del banco
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[clientes]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[clientes] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [nombre] NVARCHAR(255) NOT NULL,
        [genero] NVARCHAR(50) NOT NULL,
        [edad] INT NOT NULL,
        [identificacion] NVARCHAR(50) NOT NULL UNIQUE,
        [direccion] NVARCHAR(500) NOT NULL,
        [telefono] NVARCHAR(20) NOT NULL,
        [cliente_id] BIGINT UNIQUE,
        [contrasena] NVARCHAR(255) NOT NULL,
        [estado] BIT NOT NULL DEFAULT 1,
        [fecha_creacion] DATETIME2 DEFAULT GETDATE(),
        [fecha_modificacion] DATETIME2 DEFAULT GETDATE()
    );
    
    CREATE NONCLUSTERED INDEX [IX_clientes_identificacion] ON [dbo].[clientes]([identificacion]);
    CREATE NONCLUSTERED INDEX [IX_clientes_estado] ON [dbo].[clientes]([estado]);
END
GO

-- =====================================================
-- TABLA: cuentas
-- Descripción: Almacena cuentas bancarias de clientes
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[cuentas]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[cuentas] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [numero_cuenta] NVARCHAR(50) NOT NULL UNIQUE,
        [tipo_cuenta] NVARCHAR(50) NOT NULL,
        [saldo_inicial] DECIMAL(18,2) NOT NULL DEFAULT 0.00,
        [estado] BIT NOT NULL DEFAULT 1,
        [cliente_id] BIGINT NOT NULL,
        [fecha_creacion] DATETIME2 DEFAULT GETDATE(),
        [fecha_modificacion] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_cuentas_cliente] FOREIGN KEY ([cliente_id])
            REFERENCES [dbo].[clientes]([id])
            ON DELETE NO ACTION
            ON UPDATE CASCADE
    );
    
    CREATE NONCLUSTERED INDEX [IX_cuentas_numero_cuenta] ON [dbo].[cuentas]([numero_cuenta]);
    CREATE NONCLUSTERED INDEX [IX_cuentas_cliente_id] ON [dbo].[cuentas]([cliente_id]);
    CREATE NONCLUSTERED INDEX [IX_cuentas_estado] ON [dbo].[cuentas]([estado]);
END
GO

-- =====================================================
-- TABLA: movimientos
-- Descripción: Almacena movimientos/transacciones de cuentas
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[movimientos]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[movimientos] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [fecha] DATE NOT NULL,
        [tipo_movimiento] NVARCHAR(50) NOT NULL,
        [valor] DECIMAL(18,2) NOT NULL,
        [saldo] DECIMAL(18,2) NOT NULL,
        [numero_cuenta] NVARCHAR(50) NOT NULL,
        [fecha_creacion] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [FK_movimientos_cuenta] FOREIGN KEY ([numero_cuenta])
            REFERENCES [dbo].[cuentas]([numero_cuenta])
            ON DELETE NO ACTION
            ON UPDATE CASCADE
    );
    
    CREATE NONCLUSTERED INDEX [IX_movimientos_numero_cuenta] ON [dbo].[movimientos]([numero_cuenta]);
    CREATE NONCLUSTERED INDEX [IX_movimientos_fecha] ON [dbo].[movimientos]([fecha]);
    CREATE NONCLUSTERED INDEX [IX_movimientos_tipo] ON [dbo].[movimientos]([tipo_movimiento]);
    CREATE NONCLUSTERED INDEX [IX_movimientos_cuenta_fecha] ON [dbo].[movimientos]([numero_cuenta], [fecha] DESC);
END
GO

-- =====================================================
-- TRIGGERS
-- =====================================================

-- Trigger: Actualizar fecha_modificacion en clientes
IF EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[dbo].[TR_clientes_update]'))
    DROP TRIGGER [dbo].[TR_clientes_update];
GO

CREATE TRIGGER [dbo].[TR_clientes_update]
ON [dbo].[clientes]
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE [dbo].[clientes]
    SET [fecha_modificacion] = GETDATE()
    FROM [dbo].[clientes] c
    INNER JOIN inserted i ON c.id = i.id;
END
GO

-- Trigger: Actualizar fecha_modificacion en cuentas
IF EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[dbo].[TR_cuentas_update]'))
    DROP TRIGGER [dbo].[TR_cuentas_update];
GO

CREATE TRIGGER [dbo].[TR_cuentas_update]
ON [dbo].[cuentas]
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE [dbo].[cuentas]
    SET [fecha_modificacion] = GETDATE()
    FROM [dbo].[cuentas] c
    INNER JOIN inserted i ON c.id = i.id;
END
GO

-- =====================================================
-- VISTAS
-- =====================================================

-- Vista: Saldo actual de cuentas con información de cliente
IF EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_cuentas_completo]'))
    DROP VIEW [dbo].[vw_cuentas_completo];
GO

CREATE VIEW [dbo].[vw_cuentas_completo]
AS
SELECT 
    c.id AS cuenta_id,
    c.numero_cuenta,
    c.tipo_cuenta,
    c.saldo_inicial,
    c.estado AS cuenta_activa,
    cl.id AS cliente_id,
    cl.nombre AS cliente_nombre,
    cl.identificacion AS cliente_identificacion,
    COALESCE(
        (SELECT TOP 1 m.saldo 
         FROM movimientos m 
         WHERE m.numero_cuenta = c.numero_cuenta 
         ORDER BY m.fecha DESC, m.id DESC), 
        c.saldo_inicial
    ) AS saldo_actual,
    c.fecha_creacion,
    c.fecha_modificacion
FROM cuentas c
INNER JOIN clientes cl ON c.cliente_id = cl.id;
GO

-- Vista: Resumen de movimientos por cuenta
IF EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_movimientos_resumen]'))
    DROP VIEW [dbo].[vw_movimientos_resumen];
GO

CREATE VIEW [dbo].[vw_movimientos_resumen]
AS
SELECT 
    m.numero_cuenta,
    COUNT(*) AS total_movimientos,
    SUM(CASE WHEN m.valor > 0 THEN m.valor ELSE 0 END) AS total_depositos,
    SUM(CASE WHEN m.valor < 0 THEN ABS(m.valor) ELSE 0 END) AS total_retiros,
    MIN(m.fecha) AS primer_movimiento,
    MAX(m.fecha) AS ultimo_movimiento
FROM movimientos m
GROUP BY m.numero_cuenta;
GO

-- =====================================================
-- PROCEDIMIENTOS ALMACENADOS
-- =====================================================

-- Procedimiento: Obtener estado de cuenta de un cliente
IF EXISTS (SELECT * FROM sys.procedures WHERE object_id = OBJECT_ID(N'[dbo].[sp_estado_cuenta]'))
    DROP PROCEDURE [dbo].[sp_estado_cuenta];
GO

CREATE PROCEDURE [dbo].[sp_estado_cuenta]
    @p_cliente_id BIGINT,
    @p_fecha_inicio DATE,
    @p_fecha_fin DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        c.numero_cuenta,
        c.tipo_cuenta,
        c.saldo_inicial,
        m.fecha,
        m.tipo_movimiento,
        m.valor,
        m.saldo
    FROM cuentas c
    LEFT JOIN movimientos m ON c.numero_cuenta = m.numero_cuenta
        AND m.fecha BETWEEN @p_fecha_inicio AND @p_fecha_fin
    WHERE c.cliente_id = @p_cliente_id
    ORDER BY c.numero_cuenta, m.fecha DESC, m.id DESC;
END
GO

-- Procedimiento: Verificar saldo disponible
IF EXISTS (SELECT * FROM sys.procedures WHERE object_id = OBJECT_ID(N'[dbo].[sp_verificar_saldo]'))
    DROP PROCEDURE [dbo].[sp_verificar_saldo];
GO

CREATE PROCEDURE [dbo].[sp_verificar_saldo]
    @p_numero_cuenta NVARCHAR(50),
    @p_saldo_actual DECIMAL(18,2) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT @p_saldo_actual = COALESCE(
        (SELECT TOP 1 saldo 
         FROM movimientos 
         WHERE numero_cuenta = @p_numero_cuenta 
         ORDER BY fecha DESC, id DESC),
        (SELECT saldo_inicial 
         FROM cuentas 
         WHERE numero_cuenta = @p_numero_cuenta)
    );
END
GO

-- =====================================================
-- DATOS DE EJEMPLO / PRUEBA
-- =====================================================

-- Limpiar datos existentes (solo para desarrollo)
IF EXISTS (SELECT 1 FROM movimientos)
BEGIN
    DELETE FROM movimientos;
    DBCC CHECKIDENT ('movimientos', RESEED, 0);
END

IF EXISTS (SELECT 1 FROM cuentas)
BEGIN
    DELETE FROM cuentas;
    DBCC CHECKIDENT ('cuentas', RESEED, 0);
END

IF EXISTS (SELECT 1 FROM clientes)
BEGIN
    DELETE FROM clientes;
    DBCC CHECKIDENT ('clientes', RESEED, 0);
END
GO

-- Insertar clientes de ejemplo
SET IDENTITY_INSERT clientes ON;
INSERT INTO clientes (id, nombre, genero, edad, identificacion, direccion, telefono, cliente_id, contrasena, estado) VALUES
(1, N'Jose Lema', N'Masculino', 30, N'1234567890', N'Otavalo sn y principal', N'098254785', 1, N'1234', 1),
(2, N'Marianela Montalvo', N'Femenino', 28, N'0987654321', N'Amazonas y NNUU', N'097548965', 2, N'5678', 1),
(3, N'Juan Osorio', N'Masculino', 35, N'1122334455', N'13 junio y Equinoccial', N'098874587', 3, N'1245', 1);
SET IDENTITY_INSERT clientes OFF;
GO

-- Insertar cuentas de ejemplo
SET IDENTITY_INSERT cuentas ON;
INSERT INTO cuentas (id, numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) VALUES
(1, N'478758', N'Ahorro', 2000.00, 1, 1),
(2, N'225487', N'Corriente', 100.00, 1, 2),
(3, N'495878', N'Ahorro', 0.00, 1, 3),
(4, N'496825', N'Ahorro', 540.00, 1, 2);
SET IDENTITY_INSERT cuentas OFF;
GO

-- Insertar movimientos de ejemplo
SET IDENTITY_INSERT movimientos ON;
INSERT INTO movimientos (id, fecha, tipo_movimiento, valor, saldo, numero_cuenta) VALUES
-- Cuenta 478758 (Jose Lema)
(1, '2026-01-05', N'Depósito', 500.00, 2500.00, N'478758'),
(2, '2026-01-06', N'Retiro', -575.00, 1925.00, N'478758'),

-- Cuenta 225487 (Marianela Montalvo)
(3, '2026-01-05', N'Depósito', 600.00, 700.00, N'225487'),
(4, '2026-01-06', N'Depósito', 150.00, 850.00, N'225487'),

-- Cuenta 495878 (Juan Osorio)
(5, '2026-01-06', N'Depósito', 1000.00, 1000.00, N'495878'),

-- Cuenta 496825 (Marianela Montalvo - segunda cuenta)
(6, '2026-01-07', N'Retiro', -540.00, 0.00, N'496825');
SET IDENTITY_INSERT movimientos OFF;
GO

-- =====================================================
-- CONSULTAS DE VERIFICACIÓN
-- =====================================================

-- Verificar estructura de base de datos
SELECT 
    t.TABLE_NAME AS Tabla,
    COUNT(c.COLUMN_NAME) AS Total_Columnas
FROM INFORMATION_SCHEMA.TABLES t
LEFT JOIN INFORMATION_SCHEMA.COLUMNS c ON t.TABLE_NAME = c.TABLE_NAME
WHERE t.TABLE_TYPE = 'BASE TABLE'
GROUP BY t.TABLE_NAME
ORDER BY t.TABLE_NAME;
GO

-- Verificar datos insertados
SELECT 'Clientes' AS Tipo, COUNT(*) AS Total FROM clientes
UNION ALL
SELECT 'Cuentas', COUNT(*) FROM cuentas
UNION ALL
SELECT 'Movimientos', COUNT(*) FROM movimientos;
GO

-- Verificar vistas
SELECT * FROM vw_cuentas_completo;
GO

-- =====================================================
-- INFORMACIÓN DEL ESQUEMA
-- =====================================================

PRINT '============================================';
PRINT 'BaseDatos.sql - Sistema Banco Pichincha';
PRINT 'Motor: Microsoft SQL Server 2022';
PRINT 'Fecha: 2026-01-08';
PRINT 'Versión: 1.0';
PRINT 'Arquitectura: Hexagonal';
PRINT '============================================';
GO

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
