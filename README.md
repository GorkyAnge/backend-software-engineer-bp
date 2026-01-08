# Software Engineer BP Project

Proyecto con arquitectura hexagonal (puertos y adaptadores).

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

## Tecnologías

- Java 17
- Spring Boot 3.2.5
- Maven

## Ejecutar

```bash
./mvnw spring-boot:run
```
