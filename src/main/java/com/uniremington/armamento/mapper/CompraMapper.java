package com.uniremington.armamento.mapper;

import com.uniremington.armamento.dto.CompraVistaDto;
import com.uniremington.armamento.dto.DetalleCompraVistaDto;
import com.uniremington.armamento.entity.Compra;
import com.uniremington.armamento.entity.DetalleCompra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompraMapper {
    @Mapping(target = "proveedorNombre", source = "proveedor.razonSocial")
    @Mapping(target = "almacenNombre", source = "almacen.nombre")
    @Mapping(target = "usuarioNombre", expression = "java(compra.getUsuario().getNombres() + \" \" + compra.getUsuario().getApellidos())")
    CompraVistaDto aVista(Compra compra);

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoSku", source = "producto.sku")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    DetalleCompraVistaDto detalleAVista(DetalleCompra detalle);
}
