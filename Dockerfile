# =====================================================
# Dockerfile - Sistema Banco Pichincha
# Multi-stage build para optimizar tamaño de imagen
# =====================================================

# ========== Stage 1: Build ==========
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar y empaquetar la aplicación (omitir tests para build más rápido)
RUN mvn clean package -DskipTests

# ========== Stage 2: Runtime ==========
FROM eclipse-temurin:17-jre-alpine

# Metadatos
LABEL maintainer="Banco Pichincha"
LABEL description="Sistema de Gestión Bancaria - Arquitectura Hexagonal"
LABEL version="1.0"

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Directorio de trabajo
WORKDIR /app

# Copiar JAR desde stage de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar propiedad de archivos al usuario spring
RUN chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring:spring

# Puerto expuesto
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
