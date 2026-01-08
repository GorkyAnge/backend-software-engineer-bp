package com.gorkyange.bp.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.domain.exception.*;
import com.gorkyange.bp.domain.model.Cliente;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ClienteRequest;
import com.gorkyange.bp.infrastructure.adapter.in.rest.mapper.ClienteRestMapper;
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

@WebMvcTest(ClienteController.class)
@DisplayName("ClienteController Tests")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrearClienteUseCase crearClienteUseCase;

    @MockBean
    private ActualizarClienteUseCase actualizarClienteUseCase;

    @MockBean
    private ObtenerClienteUseCase obtenerClienteUseCase;

    @MockBean
    private ListarClientesUseCase listarClientesUseCase;

    @MockBean
    private EliminarClienteUseCase eliminarClienteUseCase;

    @MockBean
    private ClienteRestMapper mapper;

    // ===== POST /clientes =====

    @Test
    @DisplayName("POST /clientes - Debe crear cliente exitosamente")
    void debeCrearClienteExitosamente() throws Exception {
        // Arrange
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Jose Lema");
        request.setGenero("Masculino");
        request.setEdad(25);
        request.setIdentificacion("1234567890");
        request.setDireccion("Otavalo sn y principal");
        request.setTelefono("098254785");
        request.setContrasena("1234");
        request.setEstado(true);

        Cliente clienteCreado = new Cliente();
        clienteCreado.setId(1L);
        clienteCreado.setNombre(request.getNombre());
        clienteCreado.setGenero(request.getGenero());
        clienteCreado.setEdad(request.getEdad());
        clienteCreado.setIdentificacion(request.getIdentificacion());
        clienteCreado.setDireccion(request.getDireccion());
        clienteCreado.setTelefono(request.getTelefono());
        clienteCreado.setContrasena(request.getContrasena());
        clienteCreado.setEstado(request.getEstado());

        when(crearClienteUseCase.crear(any(Cliente.class))).thenReturn(clienteCreado);

        // Act & Assert
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Jose Lema")))
                .andExpect(jsonPath("$.identificacion", is("1234567890")));
    }

    @Test
    @DisplayName("POST /clientes - Debe fallar con identificacion duplicada")
    void debeFallarConIdentificacionDuplicada() throws Exception {
        // Arrange
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Jose Lema");
        request.setGenero("Masculino");
        request.setEdad(25);
        request.setIdentificacion("1234567890");
        request.setDireccion("Otavalo sn y principal");
        request.setTelefono("098254785");
        request.setContrasena("1234");
        request.setEstado(true);

        when(crearClienteUseCase.crear(any(Cliente.class)))
                .thenThrow(new ClienteDuplicadoException("1234567890"));

        // Act & Assert
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CLIENTE_DUPLICADO")));
    }

    @Test
    @DisplayName("POST /clientes - Debe fallar con edad invalida")
    void debeFallarConEdadInvalida() throws Exception {
        // Arrange
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Jose Lema");
        request.setGenero("Masculino");
        request.setEdad(15);
        request.setIdentificacion("1234567890");
        request.setDireccion("Otavalo sn y principal");
        request.setTelefono("098254785");
        request.setContrasena("1234");
        request.setEstado(true);

        when(crearClienteUseCase.crear(any(Cliente.class)))
                .thenThrow(DatosInvalidosException.edadInvalida(15));

        // Act & Assert
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("DATOS_INVALIDOS")));
    }

    // ===== GET /clientes/{clienteId} =====

    @Test
    @DisplayName("GET /clientes/{clienteId} - Debe retornar cliente por ID")
    void debeRetornarClientePorId() throws Exception {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Jose Lema");
        cliente.setIdentificacion("1234567890");

        when(obtenerClienteUseCase.obtenerPorId(1L)).thenReturn(Optional.of(cliente));

        // Act & Assert
        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Jose Lema")));
    }

    @Test
    @DisplayName("GET /clientes/{clienteId} - Debe retornar 404 cuando no existe")
    void debeRetornar404CuandoClienteNoExiste() throws Exception {
        // Arrange
        when(obtenerClienteUseCase.obtenerPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/clientes/999"))
                .andExpect(status().isNotFound());
    }

    // ===== GET /clientes =====

    @Test
    @DisplayName("GET /clientes - Debe retornar lista de clientes")
    void debeRetornarListaDeClientes() throws Exception {
        // Arrange
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNombre("Jose Lema");

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNombre("Maria Montalvo");

        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);
        when(listarClientesUseCase.listarTodos()).thenReturn(clientes);

        // Act & Assert
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Jose Lema")))
                .andExpect(jsonPath("$[1].nombre", is("Maria Montalvo")));
    }

    @Test
    @DisplayName("GET /clientes - Debe retornar lista vacia cuando no hay clientes")
    void debeRetornarListaVaciaCuandoNoHayClientes() throws Exception {
        // Arrange
        when(listarClientesUseCase.listarTodos()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===== PUT /clientes/{clienteId} =====

    @Test
    @DisplayName("PUT /clientes/{clienteId} - Debe actualizar cliente exitosamente")
    void debeActualizarClienteExitosamente() throws Exception {
        // Arrange
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Jose Lema Actualizado");
        request.setGenero("Masculino");
        request.setEdad(26);
        request.setIdentificacion("1234567890");
        request.setDireccion("Otavalo sn y principal");
        request.setTelefono("098254785");
        request.setContrasena("1234");
        request.setEstado(true);

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setId(1L);
        clienteActualizado.setNombre(request.getNombre());

        when(actualizarClienteUseCase.actualizar(eq(1L), any(Cliente.class)))
                .thenReturn(clienteActualizado);

        // Act & Assert
        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Jose Lema Actualizado")));
    }

    @Test
    @DisplayName("PUT /clientes/{clienteId} - Debe fallar cuando cliente no existe")
    void debeFallarActualizarCuandoClienteNoExiste() throws Exception {
        // Arrange
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Jose Lema");
        request.setGenero("Masculino");
        request.setEdad(26);
        request.setIdentificacion("1234567890");
        request.setDireccion("Otavalo sn y principal");
        request.setTelefono("098254785");
        request.setContrasena("1234");
        request.setEstado(true);

        when(actualizarClienteUseCase.actualizar(eq(999L), any(Cliente.class)))
                .thenThrow(new ClienteNoEncontradoException(999L));

        // Act & Assert
        mockMvc.perform(put("/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("CLIENTE_NO_ENCONTRADO")));
    }

    // ===== DELETE /clientes/{clienteId} =====

    @Test
    @DisplayName("DELETE /clientes/{clienteId} - Debe eliminar cliente exitosamente")
    void debeEliminarClienteExitosamente() throws Exception {
        // Arrange
        doNothing().when(eliminarClienteUseCase).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /clientes/{clienteId} - Debe fallar cuando cliente tiene cuentas activas")
    void debeFallarEliminarCuandoClienteTieneCuentasActivas() throws Exception {
        // Arrange
        doThrow(new ClienteConCuentasActivasException(1L, 2))
                .when(eliminarClienteUseCase).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CLIENTE_CON_CUENTAS_ACTIVAS")));
    }
}
