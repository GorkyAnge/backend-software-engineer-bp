package com.gorkyange.bp.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorkyange.bp.application.port.in.CrearMovimientoUseCase;
import com.gorkyange.bp.application.port.in.ListarMovimientosUseCase;
import com.gorkyange.bp.domain.exception.*;
import com.gorkyange.bp.domain.model.Movimiento;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.MovimientoRequest;
import com.gorkyange.bp.infrastructure.adapter.in.rest.mapper.MovimientoRestMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovimientoController.class)
@DisplayName("MovimientoController Tests")
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrearMovimientoUseCase crearMovimientoUseCase;

    @MockBean
    private ListarMovimientosUseCase listarMovimientosUseCase;

    @MockBean
    private MovimientoRestMapper mapper;

    // ===== POST /movimientos =====

    @Test
    @DisplayName("POST /movimientos - Debe crear movimiento exitosamente")
    void debeCrearMovimientoExitosamente() throws Exception {
        // Arrange
        MovimientoRequest request = new MovimientoRequest();
        request.setFecha(LocalDate.now());
        request.setTipoMovimiento("Depósito");
        request.setValor(500.0);
        request.setNumeroCuenta("478758");

        Movimiento movimientoCreado = new Movimiento();
        movimientoCreado.setId(1L);
        movimientoCreado.setFecha(request.getFecha());
        movimientoCreado.setTipoMovimiento(request.getTipoMovimiento());
        movimientoCreado.setValor(request.getValor());
        movimientoCreado.setSaldo(2500.0);
        movimientoCreado.setNumeroCuenta(request.getNumeroCuenta());

        when(crearMovimientoUseCase.crear(any(Movimiento.class))).thenReturn(movimientoCreado);

        // Act & Assert
        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.numeroCuenta", is("478758")))
                .andExpect(jsonPath("$.valor", is(500.0)));
    }

    @Test
    @DisplayName("POST /movimientos - Debe fallar con valor cero")
    void debeFallarConValorCero() throws Exception {
        // Arrange
        MovimientoRequest request = new MovimientoRequest();
        request.setFecha(LocalDate.now());
        request.setTipoMovimiento("Depósito");
        request.setValor(0.0);
        request.setNumeroCuenta("478758");

        when(crearMovimientoUseCase.crear(any(Movimiento.class)))
                .thenThrow(MovimientoInvalidoException.valorCero());

        // Act & Assert
        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("MOVIMIENTO_INVALIDO")));
    }

    @Test
    @DisplayName("POST /movimientos - Debe fallar con saldo insuficiente")
    void debeFallarConSaldoInsuficiente() throws Exception {
        // Arrange
        MovimientoRequest request = new MovimientoRequest();
        request.setFecha(LocalDate.now());
        request.setTipoMovimiento("Retiro");
        request.setValor(-500.0);
        request.setNumeroCuenta("478758");

        when(crearMovimientoUseCase.crear(any(Movimiento.class)))
                .thenThrow(new SaldoInsuficienteException(100.0, 500.0));

        // Act & Assert
        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("SALDO_INSUFICIENTE")));
    }

    @Test
    @DisplayName("POST /movimientos - Debe fallar cuando cuenta no existe")
    void debeFallarCuandoCuentaNoExiste() throws Exception {
        // Arrange
        MovimientoRequest request = new MovimientoRequest();
        request.setFecha(LocalDate.now());
        request.setTipoMovimiento("Depósito");
        request.setValor(500.0);
        request.setNumeroCuenta("999999");

        when(crearMovimientoUseCase.crear(any(Movimiento.class)))
                .thenThrow(new CuentaNoEncontradaException("999999"));

        // Act & Assert
        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("CUENTA_NO_ENCONTRADA")));
    }

    @Test
    @DisplayName("POST /movimientos - Debe fallar cuando cuenta esta inactiva")
    void debeFallarCuandoCuentaEstaInactiva() throws Exception {
        // Arrange
        MovimientoRequest request = new MovimientoRequest();
        request.setFecha(LocalDate.now());
        request.setTipoMovimiento("Depósito");
        request.setValor(500.0);
        request.setNumeroCuenta("478758");

        when(crearMovimientoUseCase.crear(any(Movimiento.class)))
                .thenThrow(new CuentaInactivaException("478758"));

        // Act & Assert
        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("CUENTA_INACTIVA")));
    }

    // ===== GET /movimientos =====

    @Test
    @DisplayName("GET /movimientos - Debe retornar lista de movimientos")
    void debeRetornarListaDeMovimientos() throws Exception {
        // Arrange
        Movimiento mov1 = new Movimiento();
        mov1.setId(1L);
        mov1.setNumeroCuenta("478758");
        mov1.setValor(500.0);

        Movimiento mov2 = new Movimiento();
        mov2.setId(2L);
        mov2.setNumeroCuenta("478758");
        mov2.setValor(-100.0);

        List<Movimiento> movimientos = Arrays.asList(mov1, mov2);
        when(listarMovimientosUseCase.listarTodos()).thenReturn(movimientos);

        // Act & Assert
        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].valor", is(500.0)))
                .andExpect(jsonPath("$[1].valor", is(-100.0)));
    }

    @Test
    @DisplayName("GET /movimientos - Debe retornar lista vacia cuando no hay movimientos")
    void debeRetornarListaVaciaCuandoNoHayMovimientos() throws Exception {
        // Arrange
        when(listarMovimientosUseCase.listarTodos()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===== GET /movimientos?numeroCuenta= =====

    @Test
    @DisplayName("GET /movimientos?numeroCuenta= - Debe retornar movimientos por cuenta")
    void debeRetornarMovimientosPorCuenta() throws Exception {
        // Arrange
        Movimiento mov1 = new Movimiento();
        mov1.setId(1L);
        mov1.setNumeroCuenta("478758");
        mov1.setValor(500.0);

        Movimiento mov2 = new Movimiento();
        mov2.setId(2L);
        mov2.setNumeroCuenta("478758");
        mov2.setValor(-100.0);

        List<Movimiento> movimientos = Arrays.asList(mov1, mov2);
        when(listarMovimientosUseCase.listarPorCuenta("478758")).thenReturn(movimientos);

        // Act & Assert
        mockMvc.perform(get("/movimientos").param("numeroCuenta", "478758"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numeroCuenta", is("478758")))
                .andExpect(jsonPath("$[1].numeroCuenta", is("478758")));
    }

    @Test
    @DisplayName("GET /movimientos?numeroCuenta= - Debe retornar lista vacia cuando cuenta no tiene movimientos")
    void debeRetornarListaVaciaCuandoCuentaNoTieneMovimientos() throws Exception {
        // Arrange
        when(listarMovimientosUseCase.listarPorCuenta("999999")).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/movimientos").param("numeroCuenta", "999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===== GET /movimientos?fechaInicio=&fechaFin= =====

    @Test
    @DisplayName("GET /movimientos?fechaInicio=&fechaFin= - Debe retornar movimientos por rango de fechas")
    void debeRetornarMovimientosPorRangoDeFechas() throws Exception {
        // Arrange
        LocalDate fechaInicio = LocalDate.of(2026, 1, 1);
        LocalDate fechaFin = LocalDate.of(2026, 1, 31);

        Movimiento mov1 = new Movimiento();
        mov1.setId(1L);
        mov1.setFecha(LocalDate.of(2026, 1, 5));
        mov1.setValor(500.0);

        Movimiento mov2 = new Movimiento();
        mov2.setId(2L);
        mov2.setFecha(LocalDate.of(2026, 1, 15));
        mov2.setValor(-100.0);

        List<Movimiento> movimientos = Arrays.asList(mov1, mov2);
        when(listarMovimientosUseCase.listarPorFechas(fechaInicio, fechaFin))
                .thenReturn(movimientos);

        // Act & Assert
        mockMvc.perform(get("/movimientos")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /movimientos?fechaInicio=&fechaFin= - Debe retornar lista vacia cuando no hay movimientos en el rango")
    void debeRetornarListaVaciaCuandoNoHayMovimientosEnRango() throws Exception {
        // Arrange
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);
        LocalDate fechaFin = LocalDate.of(2025, 1, 31);

        when(listarMovimientosUseCase.listarPorFechas(fechaInicio, fechaFin))
                .thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/movimientos")
                        .param("fechaInicio", "2025-01-01")
                        .param("fechaFin", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
