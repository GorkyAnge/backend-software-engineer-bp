package com.gorkyange.bp.infrastructure.adapter.out.persistence.mapper;

import com.gorkyange.bp.domain.model.Cliente;
import com.gorkyange.bp.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteEntity toEntity(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity();
        entity.setId(cliente.getId());
        entity.setNombre(cliente.getNombre());
        entity.setGenero(cliente.getGenero());
        entity.setEdad(cliente.getEdad());
        entity.setIdentificacion(cliente.getIdentificacion());
        entity.setDireccion(cliente.getDireccion());
        entity.setTelefono(cliente.getTelefono());
        entity.setClienteId(cliente.getClienteId());
        entity.setContrasena(cliente.getContrasena());
        entity.setEstado(cliente.getEstado());
        return entity;
    }

    public Cliente toDomain(ClienteEntity entity) {
        Cliente cliente = new Cliente();
        cliente.setId(entity.getId());
        cliente.setNombre(entity.getNombre());
        cliente.setGenero(entity.getGenero());
        cliente.setEdad(entity.getEdad());
        cliente.setIdentificacion(entity.getIdentificacion());
        cliente.setDireccion(entity.getDireccion());
        cliente.setTelefono(entity.getTelefono());
        cliente.setClienteId(entity.getClienteId());
        cliente.setContrasena(entity.getContrasena());
        cliente.setEstado(entity.getEstado());
        return cliente;
    }
}
