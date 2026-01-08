package com.gorkyange.bp.infrastructure.adapter.in.rest.dto;

import java.time.LocalDate;
import java.util.List;

public class ReporteEstadoCuentaResponse {
    private Long clienteId;
    private String nombreCliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<CuentaReporte> cuentas;
    private ResumenReporte resumen;

    public static class CuentaReporte {
        private String numeroCuenta;
        private String tipoCuenta;
        private Double saldoInicial;
        private Double saldoActual;
        private List<MovimientoReporte> movimientos;

        public CuentaReporte() {}

        public CuentaReporte(String numeroCuenta, String tipoCuenta, Double saldoInicial, Double saldoActual, List<MovimientoReporte> movimientos) {
            this.numeroCuenta = numeroCuenta;
            this.tipoCuenta = tipoCuenta;
            this.saldoInicial = saldoInicial;
            this.saldoActual = saldoActual;
            this.movimientos = movimientos;
        }

        public String getNumeroCuenta() {
            return numeroCuenta;
        }

        public void setNumeroCuenta(String numeroCuenta) {
            this.numeroCuenta = numeroCuenta;
        }

        public String getTipoCuenta() {
            return tipoCuenta;
        }

        public void setTipoCuenta(String tipoCuenta) {
            this.tipoCuenta = tipoCuenta;
        }

        public Double getSaldoInicial() {
            return saldoInicial;
        }

        public void setSaldoInicial(Double saldoInicial) {
            this.saldoInicial = saldoInicial;
        }

        public Double getSaldoActual() {
            return saldoActual;
        }

        public void setSaldoActual(Double saldoActual) {
            this.saldoActual = saldoActual;
        }

        public List<MovimientoReporte> getMovimientos() {
            return movimientos;
        }

        public void setMovimientos(List<MovimientoReporte> movimientos) {
            this.movimientos = movimientos;
        }
    }

    public static class MovimientoReporte {
        private LocalDate fecha;
        private String tipoMovimiento;
        private Double valor;
        private Double saldo;

        public MovimientoReporte() {}

        public MovimientoReporte(LocalDate fecha, String tipoMovimiento, Double valor, Double saldo) {
            this.fecha = fecha;
            this.tipoMovimiento = tipoMovimiento;
            this.valor = valor;
            this.saldo = saldo;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public void setFecha(LocalDate fecha) {
            this.fecha = fecha;
        }

        public String getTipoMovimiento() {
            return tipoMovimiento;
        }

        public void setTipoMovimiento(String tipoMovimiento) {
            this.tipoMovimiento = tipoMovimiento;
        }

        public Double getValor() {
            return valor;
        }

        public void setValor(Double valor) {
            this.valor = valor;
        }

        public Double getSaldo() {
            return saldo;
        }

        public void setSaldo(Double saldo) {
            this.saldo = saldo;
        }
    }

    public static class ResumenReporte {
        private Double totalDebitos;
        private Double totalCreditos;
        private Double saldoFinal;

        public ResumenReporte() {}

        public ResumenReporte(Double totalDebitos, Double totalCreditos, Double saldoFinal) {
            this.totalDebitos = totalDebitos;
            this.totalCreditos = totalCreditos;
            this.saldoFinal = saldoFinal;
        }

        public Double getTotalDebitos() {
            return totalDebitos;
        }

        public void setTotalDebitos(Double totalDebitos) {
            this.totalDebitos = totalDebitos;
        }

        public Double getTotalCreditos() {
            return totalCreditos;
        }

        public void setTotalCreditos(Double totalCreditos) {
            this.totalCreditos = totalCreditos;
        }

        public Double getSaldoFinal() {
            return saldoFinal;
        }

        public void setSaldoFinal(Double saldoFinal) {
            this.saldoFinal = saldoFinal;
        }
    }

    public ReporteEstadoCuentaResponse() {}

    public ReporteEstadoCuentaResponse(Long clienteId, String nombreCliente, LocalDate fechaInicio, LocalDate fechaFin, List<CuentaReporte> cuentas, ResumenReporte resumen) {
        this.clienteId = clienteId;
        this.nombreCliente = nombreCliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cuentas = cuentas;
        this.resumen = resumen;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<CuentaReporte> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<CuentaReporte> cuentas) {
        this.cuentas = cuentas;
    }

    public ResumenReporte getResumen() {
        return resumen;
    }

    public void setResumen(ResumenReporte resumen) {
        this.resumen = resumen;
    }
}
