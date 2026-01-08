package com.gorkyange.bp.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.domain.exception.*;
import com.gorkyange.bp.domain.model.Cuenta;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.CuentaRequest;
import com.gorkyange.bp.infrastructure.adapter.in.rest.mapper.CuentaRestMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
@DisplayName("CuentaController Tests")
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrearCuentaUseCase crearCuentaUseCase;

    @MockBean
    private ActualizarCuentaUseCase actualizarCuentaUseCase;

    @MockBean
    private ObtenerCuentaUseCase obtenerCuentaUseCase;

    @MockBean
    private ListarCuentasUseCase listarCuentasUseCase;

    @MockBean
    private EliminarCuentaUseCase eliminarCuentaUseCase;

    @MockBean
    private CuentaRestMapper mapper;

    // ===== POST /cuentas =====

    @Test
    @DisplayName("POST /cuentas - Debe crear cuenta exitosamente")
    void debeCrearCuentaExitosamente() throws Exception {
        // Arrange
        CuentaRequest request = new CuentaRequest();
        request.setNumeroCuenta("478758");
        request.setTipoCuenta("Ahorro");
        request.setSaldoInicial(2000.0);
        request.setEstado(true);
        request.setClienteId(1L);

        Cuenta cuentaCreada = new Cuenta();
        cuentaCreada.setId(1L);
        cuentaCreada.setNumeroCuenta(request.getNumeroCuenta());
        cuentaCreada.setTipoCuenta(request.getTipoCuenta());
        cuentaCreada.setSaldoInicial(request.getSaldoInicial());
        cuentaCreada.setEstado(request.getEstado());
        cuentaCreada.setClienteId(request.getClienteId());

        when(crearCuentaUseCase.crear(any(Cuenta.class))).thenReturn(cuentaCreada);

        // Act & Assert
        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.numeroCuenta", is("478758")))
                .andExpect(jsonPath("$.tipoCuenta", is("Ahorro")))
                .andExpect(jsonPath("$.saldoInicial", is(2000.0)));
    }

    @Test
    @DisplayName("POST /cuentas - Debe fallar con numero de cuenta duplicado")
    void debeFallarConNumeroCuentaDuplicado() throws Exception {
        // Arrange
        CuentaRequest request = new CuentaRequest();
        request.setNumeroCuenta("478758");
        request.setTipoCuenta("Ahorro");
        request.setSaldoInicial(2000.0);
        request.setEstado(true);
        request.setClienteId(1L);

        when(crearCuentaUseCase.crear(any(Cuenta.class)))
                .thenThrow(new CuentaDuplicadaException("478758"));

        // Act & Assert
        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CUENTA_DUPLICADA")));
    }

    @Test
    @DisplayName("POST /cuentas - Debe fallar con saldo inicial negativo")
    void debeFallarConSaldoInicialNegativo() throws Exception {
        // Arrange
        CuentaRequest request = new CuentaRequest();
        request.setNumeroCuenta("478758");
        request.setTipoCuenta("Ahorro");
        request.setSaldoInicial(-100.0);
        request.setEstado(true);
        request.setClienteId(1L);

        when(crearCuentaUseCase.crear(any(Cuenta.class)))
                .thenThrow(DatosInvalidosException.saldoInicialNegativo(-100.0));

        // Act & Assert
        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("DATOS_INVALIDOS")));
    }

    // ===== GET /cuentas/{id} =====

    @Test
    @DisplayName("GET /cuentas/{id} - Debe retornar cuenta por ID")
    void debeRetornarCuentaPorId() throws Exception {
        // Arrange
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("478758");
        cuenta.setTipoCuenta("Ahorro");

        when(obtenerCuentaUseCase.obtenerPorId(1L)).thenReturn(Optional.of(cuenta));

        // Act & Assert
        mockMvc.perform(get("/cuentas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.numeroCuenta", is("478758")));
    }

    @Test
    @DisplayName("GET /cuentas/{id} - Debe retornar 404 cuando no existe")
    void debeRetornar404CuandoCuentaNoExiste() throws Exception {
        // Arrange
        when(obtenerCuentaUseCase.obtenerPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/cuentas/999"))
                .andExpect(status().isNotFound());
    }

    // ===== GET /cuentas =====

    @Test
    @DisplayName("GET /cuentas - Debe retornar lista de cuentas")
    void debeRetornarListaDeCuentas() throws Exception {
        // Arrange
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setId(1L);
        cuenta1.setNumeroCuenta("478758");

        Cuenta cuenta2 = new Cuenta();
        cuenta2.setId(2L);
        cuenta2.setNumeroCuenta("225487");

        List<Cuenta> cuentas = Arrays.asList(cuenta1, cuenta2);
        when(listarCuentasUseCase.listarTodas()).thenReturn(cuentas);

        // Act & Assert
        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numeroCuenta", is("478758")))
                .andExpect(jsonPath("$[1].numeroCuenta", is("225487")));
    }

    @Test
    @DisplayName("GET /cuentas - Debe retornar lista vacia cuando no hay cuentas")
    void debeRetornarListaVaciaCuandoNoHayCuentas() throws Exception {
        // Arrange
        when(listarCuentasUseCase.listarTodas()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===== GET /cuentas?numeroCuenta= =====

    @Test
    @DisplayName("GET /cuentas?numeroCuenta= - Debe retornar cuenta por numero")
    void debeRetornarCuentaPorNumero() throws Exception {
        // Arrange
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("478758");

        when(obtenerCuentaUseCase.obtenerPorNumeroCuenta("478758"))
                .thenReturn(Optional.of(cuenta));

        // Act & Assert
        mockMvc.perform(get("/cuentas").param("numeroCuenta", "478758"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroCuenta", is("478758")));
    }

    @Test
    @DisplayName("GET /cuentas?numeroCuenta= - Debe retornar 404 cuando no existe")
    void debeRetornar404CuandoCuentaPorNumeroNoExiste() throws Exception {
        // Arrange
        when(obtenerCuentaUseCase.obtenerPorNumeroCuenta("999999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/cuentas").param("numeroCuenta", "999999"))
                .andExpect(status().isNotFound());
    }

    // ===== GET /cuentas?clienteId= =====

    @Test
    @DisplayName("GET /cuentas?clienteId= - Debe retornar cuentas por cliente")
    void debeRetornarCuentasPorCliente() throws Exception {
        // Arrange
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setId(1L);
        cuenta1.setNumeroCuenta("478758");
        cuenta1.setClienteId(1L);

        Cuenta cuenta2 = new Cuenta();
        cuenta2.setId(2L);
        cuenta2.setNumeroCuenta("225487");
        cuenta2.setClienteId(1L);

        List<Cuenta> cuentas = Arrays.asList(cuenta1, cuenta2);
        when(listarCuentasUseCase.listarPorCliente(1L)).thenReturn(cuentas);

        // Act & Assert
        mockMvc.perform(get("/cuentas").param("clienteId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].clienteId", is(1)))
                .andExpect(jsonPath("$[1].clienteId", is(1)));
    }

    @Test
    @DisplayName("GET /cuentas?clienteId= - Debe retornar lista vacia cuando cliente no tiene cuentas")
    void debeRetornarListaVaciaCuandoClienteNoTieneCuentas() throws Exception {
        // Arrange
        when(listarCuentasUseCase.listarPorCliente(999L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/cuentas").param("clienteId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===== PUT /cuentas/{id} =====

    @Test
    @DisplayName("PUT /cuentas/{id} - Debe actualizar cuenta exitosamente")
    void debeActualizarCuentaExitosamente() throws Exception {
        // Arrange
        CuentaRequest request = new CuentaRequest();
        request.setNumeroCuenta("478758");
        request.setTipoCuenta("Corriente");
        request.setSaldoInicial(3000.0);
        request.setEstado(true);
        request.setClienteId(1L);

        Cuenta cuentaActualizada = new Cuenta();
        cuentaActualizada.setId(1L);
        cuentaActualizada.setNumeroCuenta(request.getNumeroCuenta());
        cuentaActualizada.setTipoCuenta(request.getTipoCuenta());

        when(actualizarCuentaUseCase.actualizar(eq(1L), any(Cuenta.class)))
                .thenReturn(cuentaActualizada);

        // Act & Assert
        mockMvc.perform(put("/cuentas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.tipoCuenta", is("Corriente")));
    }

    @Test
    @DisplayName("PUT /cuentas/{id} - Debe fallar cuando cuenta no existe")
    void debeFallarActualizarCuandoCuentaNoExiste() throws Exception {
        // Arrange
        CuentaRequest request = new CuentaRequest();
        request.setNumeroCuenta("478758");
        request.setTipoCuenta("Corriente");
        request.setSaldoInicial(3000.0);
        request.setEstado(true);
        request.setClienteId(1L);

        when(actualizarCuentaUseCase.actualizar(eq(999L), any(Cuenta.class)))
                .thenThrow(new CuentaNoEncontradaException(999L));

        // Act & Assert
        mockMvc.perform(put("/cuentas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("CUENTA_NO_ENCONTRADA")));
    }

    // ===== DELETE /cuentas/{id} =====

    @Test
    @DisplayName("DELETE /cuentas/{id} - Debe eliminar cuenta exitosamente")
    void debeEliminarCuentaExitosamente() throws Exception {
        // Arrange
        doNothing().when(eliminarCuentaUseCase).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/cuentas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /cuentas/{id} - Debe fallar cuando cuenta tiene movimientos")
    void debeFallarEliminarCuandoCuentaTieneMovimientos() throws Exception {
        // Arrange
        doThrow(new CuentaConMovimientosException("478758", 5))
                .when(eliminarCuentaUseCase).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/cuentas/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CUENTA_CON_MOVIMIENTOS")));
    }
}
