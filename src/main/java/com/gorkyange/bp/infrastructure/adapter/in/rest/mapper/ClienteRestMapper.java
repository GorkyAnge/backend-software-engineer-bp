package com.gorkyange.bp.infrastructure.adapter.in.rest.mapper;

import com.gorkyange.bp.domain.model.Cliente;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ClienteRequest;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ClienteResponse;
import org.springframework.stereotype.Component;

@Component
public class ClienteRestMapper {

    public Cliente toDomain(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setGenero(request.getGenero());
        cliente.setEdad(request.getEdad());
        cliente.setIdentificacion(request.getIdentificacion());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setClienteId(request.getClienteId());
        cliente.setContrasena(request.getContrasena());
        cliente.setEstado(request.getEstado());
        return cliente;
    }

    public ClienteResponse toResponse(Cliente cliente) {
        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNombre(cliente.getNombre());
        response.setGenero(cliente.getGenero());
        response.setEdad(cliente.getEdad());
        response.setIdentificacion(cliente.getIdentificacion());
        response.setDireccion(cliente.getDireccion());
        response.setTelefono(cliente.getTelefono());
        response.setClienteId(cliente.getClienteId());
        response.setEstado(cliente.getEstado());
        return response;
    }
}
