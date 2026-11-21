package com.uniremington.armamento.mapper;

import com.uniremington.armamento.dto.ProductoDto;
import com.uniremington.armamento.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    @Mapping(target = "marcaId", source = "marca.id")
    @Mapping(target = "marcaNombre", source = "marca.nombre")
    ProductoDto aDto(Producto producto);

    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "marca", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    Producto aEntidad(ProductoDto dto);
}
