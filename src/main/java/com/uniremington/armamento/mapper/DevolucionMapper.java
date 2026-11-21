package com.uniremington.armamento.mapper;

import com.uniremington.armamento.dto.DevolucionVistaDto;
import com.uniremington.armamento.dto.DetalleDevolucionVistaDto;
import com.uniremington.armamento.entity.Devolucion;
import com.uniremington.armamento.entity.DetalleDevolucion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DevolucionMapper {
    @Mapping(target = "ventaId", source = "venta.id")
    @Mapping(target = "ventaNumero", source = "venta.numeroVenta")
    @Mapping(target = "compraId", source = "compra.id")
    @Mapping(target = "compraNumero", source = "compra.numeroCompra")
    @Mapping(target = "almacenNombre", source = "almacen.nombre")
    @Mapping(target = "usuarioNombre", expression = "java(devolucion.getUsuario().getNombres() + \" \" + devolucion.getUsuario().getApellidos())")
    DevolucionVistaDto aVista(Devolucion devolucion);

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoSku", source = "producto.sku")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    @Mapping(target = "unidadId", source = "unidad.id")
    @Mapping(target = "numeroSerie", source = "unidad.numeroSerie")
    DetalleDevolucionVistaDto detalleAVista(DetalleDevolucion detalle);
}
