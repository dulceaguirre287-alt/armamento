package com.uniremington.armamento.mapper;

import com.uniremington.armamento.dto.DetalleVentaVistaDto;
import com.uniremington.armamento.dto.VentaVistaDto;
import com.uniremington.armamento.entity.DetalleVenta;
import com.uniremington.armamento.entity.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VentaMapper {
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNombre", source = "cliente.nombreRazon")
    @Mapping(target = "usuarioNombre", expression = "java(venta.getUsuario().getNombres() + \" \" + venta.getUsuario().getApellidos())")
    @Mapping(target = "almacenId", source = "almacen.id")
    @Mapping(target = "almacenNombre", source = "almacen.nombre")
    VentaVistaDto aVista(Venta venta);

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoSku", source = "producto.sku")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    DetalleVentaVistaDto detalleAVista(DetalleVenta detalle);
}
