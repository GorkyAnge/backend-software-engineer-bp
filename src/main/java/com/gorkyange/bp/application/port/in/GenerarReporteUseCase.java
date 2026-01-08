package com.gorkyange.bp.application.port.in;

import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ReporteEstadoCuentaResponse;

import java.time.LocalDate;

public interface GenerarReporteUseCase {
    ReporteEstadoCuentaResponse generarReporte(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
    String generarReportePdf(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
