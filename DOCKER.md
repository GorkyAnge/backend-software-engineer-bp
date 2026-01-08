# 🐳 Despliegue con Docker - Sistema Banco Pichincha

## 📋 Requisitos Previos

- Docker Desktop instalado (Windows/Mac) o Docker Engine (Linux)
- Docker Compose 2.0 o superior
- **Mínimo 4GB de RAM** (SQL Server requiere 2GB)
- Puertos **1433** y **8080** libres

## 🚀 Despliegue Rápido

### 1. Construir y Ejecutar

```bash
# Construir las imágenes y ejecutar los contenedores
docker-compose up --build -d

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs solo de la aplicación
docker-compose logs -f app

# Ver logs solo de SQL Server
docker-compose logs -f sqlserver
```

### 2. Inicializar Base de Datos

⚠️ **IMPORTANTE**: SQL Server NO ejecuta automáticamente scripts de inicialización. Debes ejecutar el script manualmente:

```bash
# Opción 1: Desde el contenedor de SQL Server
docker exec -it bp-sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "BancoPichincha2026!" -i /docker-entrypoint-initdb.d/BaseDatos.sql

# Opción 2: Desde tu máquina (si tienes sqlcmd instalado)
sqlcmd -S localhost,1433 -U sa -P "BancoPichincha2026!" -i BaseDatos.sql

# Opción 3: Usando el script proporcionado
bash init-db.sh
```

### 3. Verificar Estado

```bash
# Ver estado de los contenedores
docker-compose ps

# Ver salud de los servicios
docker-compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"

# Health check de la aplicación
curl http://localhost:8080/actuator/health
```

### 4. Acceder a la Aplicación

- **API REST**: http://localhost:8080
- **Actuator Health**: http://localhost:8080/actuator/health
- **SQL Server**: localhost:1433
  - Usuario: `sa`
  - Contraseña: `BancoPichincha2026!`
  - Base de datos: `BancoPichincha`

## 📁 Estructura de Archivos Docker

```
bp/
├── Dockerfile                      # Imagen de la aplicación Spring Boot
├── docker-compose.yml              # Orquestación de servicios (SQL Server + App)
├── .dockerignore                   # Archivos excluidos del build
├── BaseDatos.sql                   # Script de inicialización de BD (T-SQL)
├── init-db.sh                      # Script auxiliar para inicializar BD
└── src/main/resources/
    ├── application.properties      # Configuración desarrollo (H2)
    └── application-docker.properties # Configuración Docker (SQL Server)
```

## 🐳 Servicios Configurados

### SQL Server 2022 (sqlserver)

- **Imagen**: mcr.microsoft.com/mssql/server:2022-latest
- **Puerto**: 1433:1433
- **Volumen**: Datos persistentes en `bp-sqlserver-data`
- **Inicialización**: Script `BaseDatos.sql` debe ejecutarse **manualmente**
- **Health Check**: sqlcmd cada 10 segundos
- **Usuario SA**: sa
- **Contraseña**: BancoPichincha2026!

### Aplicación Spring Boot (app)

- **Imagen**: Construida con Dockerfile multi-stage
- **Puerto**: 8080:8080
- **Profile**: `docker`
- **Conexión BD**: `jdbc:sqlserver://sqlserver:1433;databaseName=BancoPichincha`
- **Health Check**: `/actuator/health` cada 30 segundos
- **Memoria**: 512MB max, 256MB initial

## 🔧 Comandos Útiles

### Gestión de Contenedores

```bash
# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (CUIDADO: borra datos)
docker-compose down -v

# Reiniciar solo la aplicación
docker-compose restart app

# Reiniciar solo SQL Server
docker-compose restart sqlserver

# Reconstruir solo la aplicación
docker-compose up --build --force-recreate --no-deps app
```

### Debugging

```bash
# Entrar al contenedor de la aplicación
docker exec -it bp-app sh

# Acceder a SQL Server con sqlcmd
docker exec -it bp-sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "BancoPichincha2026!"

# Ver variables de entorno de la aplicación
docker exec bp-app env

# Ver uso de recursos
docker stats bp-app bp-sqlserver
```

### Logs y Monitoreo

```bash
# Logs de los últimos 100 líneas
docker-compose logs --tail=100

# Logs desde hace 5 minutos
docker-compose logs --since 5m

# Seguir logs con timestamp
docker-compose logs -f --timestamps
```

## 📊 Base de Datos

### Tablas Creadas

1. **clientes** - Información de clientes
2. **cuentas** - Cuentas bancarias
3. **movimientos** - Transacciones

### Objetos Adicionales

- **Vistas**: `vw_cuentas_completo`, `vw_movimientos_resumen`
- **Stored Procedures**: `sp_estado_cuenta`, `sp_verificar_saldo`
- **Triggers**: `TR_clientes_update`, `TR_cuentas_update`
- **Índices**: Optimización en identificacion, numero_cuenta, fecha

### Datos de Prueba

El script `BaseDatos.sql` incluye:

- 3 clientes de ejemplo
- 4 cuentas de prueba
- 6 movimientos iniciales

### Conectarse a SQL Server

```bash
# Desde el contenedor
docker exec -it bp-sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "BancoPichincha2026!"

# Desde tu máquina (si tienes sqlcmd instalado)
sqlcmd -S localhost,1433 -U sa -P "BancoPichincha2026!"
```

### Queries Útiles

```sql
-- Usar la base de datos
USE BancoPichincha;
GO

-- Ver todas las tablas
SELECT name FROM sys.tables ORDER BY name;
GO

-- Ver clientes
SELECT * FROM clientes;
GO

-- Ver estado de cuentas con información completa
SELECT * FROM vw_cuentas_completo;
GO

-- Ver resumen de movimientos
SELECT * FROM vw_movimientos_resumen;
GO

-- Estado de cuenta de un cliente (usando SP)
EXEC sp_estado_cuenta @p_cliente_id = 1, @p_fecha_inicio = '2026-01-01', @p_fecha_fin = '2026-12-31';
GO

-- Salir de sqlcmd
QUIT
```

## 🔒 Seguridad

### Producción

Para producción, cambiar:

1. **Contraseñas**: Usar variables de entorno o secrets

   ```bash
   export SA_PASSWORD='SuperSecurePassword123!'
   docker-compose up -d
   ```

2. **Puertos**: No exponer SQL Server externamente

   ```yaml
   sqlserver:
     ports: [] # Eliminar exposición
   ```

3. **SSL/TLS**: Habilitar conexiones seguras

   ```properties
   spring.datasource.url=jdbc:sqlserver://sqlserver:1433;databaseName=BancoPichincha;encrypt=true;trustServerCertificate=false
   ```

4. **Autenticación**: Crear usuario específico de aplicación en lugar de usar SA
   ```sql
   CREATE LOGIN app_user WITH PASSWORD = 'SecurePassword123!';
   USE BancoPichincha;
   CREATE USER app_user FOR LOGIN app_user;
   GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO app_user;
   ```

## 📈 Escalamiento

### Horizontal (Múltiples Instancias)

```bash
# Escalar aplicación a 3 instancias
docker-compose up --scale app=3 -d

# Load balancer requerido (nginx, traefik, etc.)
```

### Vertical (Más Recursos)

```yaml
# En docker-compose.yml
app:
  deploy:
    resources:
      limits:
        cpus: "2"
        memory: 1024M
      reservations:
        memory: 512M
```

## 🧪 Testing

### Probar Endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Listar clientes
curl http://localhost:8080/api/clientes

# Crear cliente
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test Docker",
    "genero": "Masculino",
    "edad": 30,
    "identificacion": "9999999999",
    "direccion": "Docker Street",
    "telefono": "0999999999",
    "clienteId": 999,
    "contrasena": "test123",
    "estado": true
  }'

# Generar reporte JSON
curl "http://localhost:8080/api/reportes/estado-cuenta?clienteId=1&fechaInicio=2026-01-01&fechaFin=2026-12-31"

# Generar reporte PDF (base64)
curl "http://localhost:8080/api/reportes/estado-cuenta/pdf?clienteId=1&fechaInicio=2026-01-01&fechaFin=2026-12-31"
```

## 🛠️ Troubleshooting

### SQL Server no inicia

```bash
# Ver logs detallados
docker-compose logs sqlserver

# Verificar requisitos de contraseña (8+ chars, mayúsculas, minúsculas, números, símbolos)
# Verificar memoria asignada a Docker (mínimo 2GB recomendado para SQL Server)

# Reiniciar con logs en tiempo real
docker-compose down
docker-compose up sqlserver
```

### La aplicación no inicia

```bash
# Ver logs detallados
docker-compose logs app

# Verificar que SQL Server esté saludable
docker-compose ps sqlserver

# Reiniciar en orden
docker-compose down
docker-compose up -d sqlserver
# Esperar 30-40 segundos hasta que SQL Server esté healthy
docker-compose up -d app
```

### Error de conexión a SQL Server

```bash
# Verificar conectividad desde la app
docker exec bp-app ping -c 3 sqlserver

# Verificar variables de entorno
docker exec bp-app env | grep SPRING_DATASOURCE

# Probar conexión manualmente desde SQL Server
docker exec bp-sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "BancoPichincha2026!" -Q "SELECT 1"
```

### Base de datos no inicializada

```bash
# Verificar si el script está montado
docker exec bp-sqlserver ls -la /docker-entrypoint-initdb.d/

# Ejecutar script manualmente
docker exec -it bp-sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "BancoPichincha2026!" -i /docker-entrypoint-initdb.d/BaseDatos.sql

# Verificar que la BD existe
docker exec bp-sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "BancoPichincha2026!" -Q "SELECT name FROM sys.databases"
```

### Puerto en uso

```bash
# Windows
netstat -ano | findstr :1433
taskkill /F /PID <PID>

# Linux/Mac
lsof -i :1433
kill -9 <PID>

# O cambiar puerto en docker-compose.yml
ports:
  - "1434:1433"  # Usar 1434 en host
```

### Limpiar todo y empezar de nuevo

```bash
# CUIDADO: Esto eliminará todos los datos
docker-compose down -v --remove-orphans
docker system prune -a --volumes
docker-compose up --build -d
```

## 📝 Configuración Avanzada

### Variables de Entorno

Crear archivo `.env`:

```env
# SQL Server
SA_PASSWORD=BancoPichincha2026!
ACCEPT_EULA=Y
MSSQL_PID=Developer

# App
SPRING_PROFILES_ACTIVE=docker
JAVA_OPTS=-Xmx512m -Xms256m
```

Luego ejecutar:

```bash
docker-compose --env-file .env up -d
```

### Persistencia de Logs

```yaml
# Agregar a docker-compose.yml
app:
  volumes:
    - ./logs:/app/logs
```

### Backup de Base de Datos

```bash
# Crear backup
docker exec bp-sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "BancoPichincha2026!" \
  -Q "BACKUP DATABASE BancoPichincha TO DISK = '/var/opt/mssql/backup/BancoPichincha_$(date +%Y%m%d).bak' WITH FORMAT"

# Copiar backup a tu máquina
docker cp bp-sqlserver:/var/opt/mssql/backup/BancoPichincha_20260108.bak ./backup/

# Restaurar backup
docker cp ./backup/BancoPichincha_20260108.bak bp-sqlserver:/var/opt/mssql/backup/
docker exec bp-sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "BancoPichincha2026!" \
  -Q "RESTORE DATABASE BancoPichincha FROM DISK = '/var/opt/mssql/backup/BancoPichincha_20260108.bak' WITH REPLACE"
```

## 🎯 Comandos de Producción

```bash
# Build optimizado para producción
docker-compose build --no-cache --pull

# Ejecutar en modo daemon
docker-compose up -d

# Ver solo errores
docker-compose logs --tail=100 | grep ERROR

# Actualizar sin downtime (requiere load balancer)
docker-compose up -d --no-deps --scale app=2 app
docker-compose up -d --no-deps --scale app=1 app
```

## 📖 Referencias

- [SQL Server on Docker Documentation](https://learn.microsoft.com/en-us/sql/linux/sql-server-linux-docker-container-deployment)
- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot with SQL Server](https://spring.io/guides/gs/accessing-data-jpa/)

---

**Fecha**: 2026-01-08  
**Versión**: 1.0  
**Arquitectura**: Hexagonal  
**Stack**: Spring Boot 3.2.5 + SQL Server 2022 + Docker
