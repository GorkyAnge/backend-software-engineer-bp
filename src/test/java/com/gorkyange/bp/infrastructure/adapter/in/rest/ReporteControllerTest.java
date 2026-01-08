package com.gorkyange.bp.infrastructure.adapter.in.rest;

import com.gorkyange.bp.application.port.in.GenerarReporteUseCase;
import com.gorkyange.bp.domain.exception.ClienteNoEncontradoException;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ReporteEstadoCuentaResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
@DisplayName("ReporteController Tests")
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenerarReporteUseCase generarReporteUseCase;

    // ===== GET /reportes?formato=json =====

    @Test
    @DisplayName("GET /reportes - Debe generar reporte en formato JSON")
    void debeGenerarReporteFormatoJson() throws Exception {
        // Arrange
        LocalDate fechaInicio = LocalDate.of(2026, 1, 1);
        LocalDate fechaFin = LocalDate.of(2026, 1, 31);

        ReporteEstadoCuentaResponse reporte = new ReporteEstadoCuentaResponse();
        reporte.setNombreCliente("Jose Lema");
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);
        reporte.setCuentas(new ArrayList<>());

        when(generarReporteUseCase.generarReporte(eq(1L), eq(fechaInicio), eq(fechaFin)))
                .thenReturn(reporte);

        // Act & Assert
        mockMvc.perform(get("/reportes")
                        .param("clienteId", "1")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31")
                        .param("formato", "json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCliente", is("Jose Lema")));
    }

    @Test
    @DisplayName("GET /reportes - Debe usar JSON como formato por defecto")
    void debeUsarJsonComoFormatoPorDefecto() throws Exception {
        // Arrange
        LocalDate fechaInicio = LocalDate.of(2026, 1, 1);
        LocalDate fechaFin = LocalDate.of(2026, 1, 31);

        ReporteEstadoCuentaResponse reporte = new ReporteEstadoCuentaResponse();
        reporte.setNombreCliente("Jose Lema");
        reporte.setCuentas(new ArrayList<>());

        when(generarReporteUseCase.generarReporte(eq(1L), eq(fechaInicio), eq(fechaFin)))
                .thenReturn(reporte);

        // Act & Assert
        mockMvc.perform(get("/reportes")
                        .param("clienteId", "1")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCliente", is("Jose Lema")));
    }

    @Test
    @DisplayName("GET /reportes - Debe fallar cuando cliente no existe")
    void debeFallarCuandoClienteNoExiste() throws Exception {
        // Arrange
        LocalDate fechaInicio = LocalDate.of(2026, 1, 1);
        LocalDate fechaFin = LocalDate.of(2026, 1, 31);

        when(generarReporteUseCase.generarReporte(eq(999L), any(), any()))
                .thenThrow(new ClienteNoEncontradoException(999L));

        // Act & Assert
        mockMvc.perform(get("/reportes")
                        .param("clienteId", "999")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31")
                        .param("formato", "json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("CLIENTE_NO_ENCONTRADO")));
    }

    // ===== GET /reportes?formato=pdf =====

    @Test
    @DisplayName("GET /reportes - Debe generar reporte en formato PDF")
    void debeGenerarReporteFormatoPdf() throws Exception {
        // Arrange
        LocalDate fechaInicio = LocalDate.of(2026, 1, 1);
        LocalDate fechaFin = LocalDate.of(2026, 1, 31);
        String pdfBase64 = "JVBERi0xLjQKJeLjz9MKMSAwIG9iaiA8PAovVHlwZSAvQ2F0YWxvZwovUGFnZXMgMiAwIFIKPj4K";

        when(generarReporteUseCase.generarReportePdf(eq(1L), eq(fechaInicio), eq(fechaFin)))
                .thenReturn(pdfBase64);

        // Act & Assert
        mockMvc.perform(get("/reportes")
                        .param("clienteId", "1")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31")
                        .param("formato", "pdf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formato", is("pdf")))
                .andExpect(jsonPath("$.contenido", is(pdfBase64)));
    }

    @Test
    @DisplayName("GET /reportes - Debe fallar PDF cuando cliente no existe")
    void debeFallarPdfCuandoClienteNoExiste() throws Exception {
        // Arrange
        LocalDate fechaInicio = LocalDate.of(2026, 1, 1);
        LocalDate fechaFin = LocalDate.of(2026, 1, 31);

        when(generarReporteUseCase.generarReportePdf(eq(999L), any(), any()))
                .thenThrow(new ClienteNoEncontradoException(999L));

        // Act & Assert
        mockMvc.perform(get("/reportes")
                        .param("clienteId", "999")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31")
                        .param("formato", "pdf"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("CLIENTE_NO_ENCONTRADO")));
    }
}
