package com.gorkyange.bp.infrastructure.adapter.in.rest;

import com.gorkyange.bp.application.port.in.GenerarReporteUseCase;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ReporteEstadoCuentaResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private final GenerarReporteUseCase generarReporteUseCase;

    public ReporteController(GenerarReporteUseCase generarReporteUseCase) {
        this.generarReporteUseCase = generarReporteUseCase;
    }

    @GetMapping
    public ResponseEntity<?> generarReporte(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "json") String formato) {

        try {
            if ("pdf".equalsIgnoreCase(formato)) {
                // Generar PDF en base64
                String pdfBase64 = generarReporteUseCase.generarReportePdf(clienteId, fechaInicio, fechaFin);
                
                Map<String, String> response = new HashMap<>();
                response.put("formato", "pdf");
                response.put("contenido", pdfBase64);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            } else {
                // Retornar JSON
                ReporteEstadoCuentaResponse reporte = generarReporteUseCase.generarReporte(clienteId, fechaInicio, fechaFin);
                return ResponseEntity.ok(reporte);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
