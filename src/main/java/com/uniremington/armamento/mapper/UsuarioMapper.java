package com.uniremington.armamento.mapper;

import com.uniremington.armamento.dto.UsuarioVistaDto;
import com.uniremington.armamento.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "rolId", source = "rol.id")
    @Mapping(target = "rolNombre", source = "rol.nombre")
    UsuarioVistaDto aVista(Usuario usuario);
}
