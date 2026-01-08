package com.gorkyange.bp.infrastructure.adapter.in.rest;

import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.domain.model.Cliente;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ClienteRequest;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ClienteResponse;
import com.gorkyange.bp.infrastructure.adapter.in.rest.mapper.ClienteRestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final CrearClienteUseCase crearClienteUseCase;
    private final ActualizarClienteUseCase actualizarClienteUseCase;
    private final ObtenerClienteUseCase obtenerClienteUseCase;
    private final ListarClientesUseCase listarClientesUseCase;
    private final EliminarClienteUseCase eliminarClienteUseCase;
    private final ClienteRestMapper mapper;

    public ClienteController(CrearClienteUseCase crearClienteUseCase,
                             ActualizarClienteUseCase actualizarClienteUseCase,
                             ObtenerClienteUseCase obtenerClienteUseCase,
                             ListarClientesUseCase listarClientesUseCase,
                             EliminarClienteUseCase eliminarClienteUseCase,
                             ClienteRestMapper mapper) {
        this.crearClienteUseCase = crearClienteUseCase;
        this.actualizarClienteUseCase = actualizarClienteUseCase;
        this.obtenerClienteUseCase = obtenerClienteUseCase;
        this.listarClientesUseCase = listarClientesUseCase;
        this.eliminarClienteUseCase = eliminarClienteUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@RequestBody ClienteRequest request) {
        Cliente cliente = mapper.toDomain(request);
        Cliente creado = crearClienteUseCase.crear(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(creado));
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteResponse> obtener(@PathVariable Long clienteId) {
        return obtenerClienteUseCase.obtenerPorId(clienteId)
                .map(cliente -> ResponseEntity.ok(mapper.toResponse(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        List<ClienteResponse> clientes = listarClientesUseCase.listarTodos()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable Long clienteId,
                                                       @RequestBody ClienteRequest request) {
        Cliente cliente = mapper.toDomain(request);
        Cliente actualizado = actualizarClienteUseCase.actualizar(clienteId, cliente);
        return ResponseEntity.ok(mapper.toResponse(actualizado));
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long clienteId) {
        eliminarClienteUseCase.eliminar(clienteId);
        return ResponseEntity.noContent().build();
    }
}
