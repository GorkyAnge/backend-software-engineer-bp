#!/bin/bash
# =====================================================
# Script de Inicialización de Base de Datos SQL Server
# Sistema Banco Pichincha
# =====================================================

# Esperar a que SQL Server esté listo
echo "Esperando a que SQL Server esté disponible..."
sleep 30

# Ejecutar el script de base de datos
echo "Ejecutando script BaseDatos.sql..."
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "BancoPichincha2026!" -i /docker-entrypoint-initdb.d/BaseDatos.sql

echo "Base de datos inicializada correctamente."
