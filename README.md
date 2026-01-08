# 🏦 Sistema Banco Pichincha - Gestión Bancaria

Aplicación REST API para gestión de clientes, cuentas y movimientos bancarios implementada con **Arquitectura Hexagonal** (Puertos y Adaptadores).

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Instalación](#-instalación)
- [Despliegue con Docker](#-despliegue-con-docker)
- [Uso de la API](#-uso-de-la-api)
- [Pruebas](#-pruebas)
- [Documentación](#-documentación)

## ✨ Características

### Funcionalidades Principales

- ✅ **CRUD Completo** de Clientes, Cuentas y Movimientos
- ✅ **Reportes** - Estado de cuenta en JSON y PDF (base64)
- ✅ **Validaciones de Negocio** - Saldo insuficiente, duplicados, relaciones
- ✅ **Manejo de Excepciones** - Mensajes claros y estandarizados
- ✅ **Arquitectura Hexagonal** - Separación de capas y dependencias
- ✅ **Tests Unitarios** - 42 tests con técnica AAA (Arrange-Act-Assert)
- ✅ **Docker Ready** - Despliegue con SQL Server 2022

### Reglas de Negocio Implementadas

- Validación de saldo antes de retiros
- No permitir clientes duplicados (identificación única)
- No permitir cuentas duplicadas (número de cuenta único)
- Validación de relaciones (cliente debe existir antes de crear cuenta)
- Control de estado (no operar con entidades inactivas)
- Cálculo automático de saldo en cada movimiento

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Puertos y Adaptadores)

## Estructura del Proyecto

```
src/main/java/com/gorkyange/bp/
├── domain/                          # Capa de Dominio (núcleo del negocio)
│   └── model/                       # Entidades de dominio
│       └── Persona.java
│
├── application/                     # Capa de Aplicación (casos de uso)
│   ├── port/
│   │   ├── in/                     # Puertos de entrada (interfaces para casos de uso)
│   │   └── out/                    # Puertos de salida (interfaces para repositorios, etc.)
│   └── service/                    # Implementación de casos de uso
│
└── infrastructure/                  # Capa de Infraestructura (adaptadores)
    └── adapter/
        ├── in/
        │   └── rest/               # Controladores REST (adaptadores de entrada)
        └── out/
            └── persistence/        # Repositorios JPA (adaptadores de salida)
```

## Arquitectura Hexagonal

- **Dominio**: Lógica de negocio pura, sin dependencias externas
- **Aplicación**: Casos de uso y puertos (interfaces)
- **Infraestructura**: Adaptadores que conectan con el mundo exterior

## 🛠️ Tecnologías

- Java 17
- Spring Boot 3.2.5
- Maven
- H2 Database (desarrollo)
- SQL Server 2022 (producción/Docker)
- iText7 (generación PDF)
- JUnit 5 + Mockito (testing)

## 🚀 Instalación y Ejecución

### Desarrollo Local (H2)

```bash
./mvnw spring-boot:run
```

La aplicación iniciará en `http://localhost:8080`

### Producción con Docker (SQL Server)

```bash
# 1. Construir y levantar servicios
docker-compose up --build -d

# 2. Inicializar base de datos (IMPORTANTE)
docker exec -it bp-sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "BancoPichincha2026!" -i /docker-entrypoint-initdb.d/BaseDatos.sql

# 3. Verificar
curl http://localhost:8080/actuator/health
```

Ver [DOCKER.md](DOCKER.md) para más detalles.

## 📡 API Endpoints

### Clientes
- `GET /api/clientes` - Listar todos
- `GET /api/clientes/{id}` - Obtener por ID
- `POST /api/clientes` - Crear
- `PUT /api/clientes/{id}` - Actualizar
- `DELETE /api/clientes/{id}` - Eliminar

### Cuentas
- `GET /api/cuentas` - Listar todas
- `POST /api/cuentas` - Crear
- `PUT /api/cuentas/{id}` - Actualizar
- `DELETE /api/cuentas/{id}` - Eliminar

### Movimientos
- `GET /api/movimientos` - Listar todos
- `POST /api/movimientos` - Crear movimiento

### Reportes
- `GET /api/reportes/estado-cuenta?clienteId={id}&fechaInicio={fecha}&fechaFin={fecha}` - JSON
- `GET /api/reportes/estado-cuenta/pdf?clienteId={id}&fechaInicio={fecha}&fechaFin={fecha}` - PDF base64

## 🧪 Tests

```bash
# Ejecutar todos los tests
./mvnw test

# Ver cobertura
./mvnw test jacoco:report
```

## 📚 Documentación

- [HEXAGONAL_ARCHITECTURE.md](HEXAGONAL_ARCHITECTURE.md) - Detalles de arquitectura
- [EXCEPCIONES.md](EXCEPCIONES.md) - Sistema de excepciones
- [TESTS.md](TESTS.md) - Catálogo de tests
- [DOCKER.md](DOCKER.md) - Guía de despliegue Docker
- [Banco_Pichincha_API.postman_collection.json](Banco_Pichincha_API.postman_collection.json) - Colección Postman
