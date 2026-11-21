package com.uniremington.armamento.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uniremington.armamento.dto.DetalleDevolucionFormularioDto;
import com.uniremington.armamento.entity.Compra;
import com.uniremington.armamento.entity.DetalleCompra;
import com.uniremington.armamento.entity.Producto;
import com.uniremington.armamento.entity.Venta;
import com.uniremington.armamento.mapper.CompraMapper;
import com.uniremington.armamento.mapper.DevolucionMapper;
import com.uniremington.armamento.mapper.VentaMapper;
import com.uniremington.armamento.repository.AlmacenRepository;
import com.uniremington.armamento.repository.ClienteRepository;
import com.uniremington.armamento.repository.CompraRepository;
import com.uniremington.armamento.repository.DevolucionRepository;
import com.uniremington.armamento.repository.DetalleCompraRepository;
import com.uniremington.armamento.repository.DetalleDevolucionRepository;
import com.uniremington.armamento.repository.DetalleVentaRepository;
import com.uniremington.armamento.repository.ExistenciaRepository;
import com.uniremington.armamento.repository.MovimientoInventarioRepository;
import com.uniremington.armamento.repository.PermisoClienteRepository;
import com.uniremington.armamento.repository.ProductoRepository;
import com.uniremington.armamento.repository.ProveedorRepository;
import com.uniremington.armamento.repository.UnidadSeriadaRepository;
import com.uniremington.armamento.repository.UsuarioRepository;
import com.uniremington.armamento.repository.VentaRepository;
import com.uniremington.armamento.service.AuditoriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTests {
    @Mock private VentaRepository ventaRepository;
    @Mock private CompraRepository compraRepository;
    @Mock private DevolucionRepository devolucionRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private AlmacenRepository almacenRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private PermisoClienteRepository permisoRepository;
    @Mock private ExistenciaRepository existenciaRepository;
    @Mock private UnidadSeriadaRepository unidadSeriadaRepository;
    @Mock private DetalleVentaRepository detalleVentaRepository;
    @Mock private DetalleCompraRepository detalleCompraRepository;
    @Mock private DetalleDevolucionRepository detalleDevolucionRepository;
    @Mock private MovimientoInventarioRepository movimientoRepository;
    @Mock private ProveedorRepository proveedorRepository;
    @Mock private VentaMapper ventaMapper;
    @Mock private CompraMapper compraMapper;
    @Mock private DevolucionMapper devolucionMapper;
    @Mock private AuditoriaService auditoriaService;

    @InjectMocks private VentaServiceImpl ventaService;
    @InjectMocks private CompraServiceImpl compraService;
    @InjectMocks private DevolucionServiceImpl devolucionService;

    @Test
    void saleCancellationLocksSaleAndRejectsPendingOrApprovedReturns() {
        Venta venta = new Venta();
        venta.setEstado("PAGADA");
        when(ventaRepository.buscarYBloquear(17L)).thenReturn(Optional.of(venta));
        when(devolucionRepository.existsByVenta_IdAndEstadoNot(17L, "RECHAZADA")).thenReturn(true);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> ventaService.anular(17L));

        assertEquals(
                "No se puede anular la venta mientras tenga devoluciones pendientes o aprobadas.",
                error.getMessage());
        verify(ventaRepository).buscarYBloquear(17L);
    }

    @Test
    void purchaseCancellationLocksPurchaseAndRejectsPendingOrApprovedReturns() {
        Compra compra = new Compra();
        compra.setEstado("RECIBIDA");
        when(compraRepository.buscarYBloquear(23L)).thenReturn(Optional.of(compra));
        when(devolucionRepository.existsByCompra_IdAndEstadoNot(23L, "RECHAZADA")).thenReturn(true);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> compraService.anular(23L));

        assertEquals(
                "No se puede anular la compra mientras tenga devoluciones pendientes o aprobadas.",
                error.getMessage());
        verify(compraRepository).buscarYBloquear(23L);
    }

    @Test
    void purchaseReturnRejectsAmbiguousSourcePrices() {
        Producto producto = new Producto();
        producto.setId(5);
        DetalleCompra first = purchaseLine(producto, new BigDecimal("10.00"));
        DetalleCompra second = purchaseLine(producto, new BigDecimal("12.00"));
        DetalleDevolucionFormularioDto request = new DetalleDevolucionFormularioDto();
        request.setProductoId(5);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> devolucionService.buscarDetalleCompra(List.of(first, second), request));

        assertEquals(
                "La compra tiene varias líneas del producto o lote con precios distintos; "
                        + "no se puede determinar el precio de devolución.",
                error.getMessage());
    }

    private DetalleCompra purchaseLine(Producto producto, BigDecimal price) {
        DetalleCompra detail = new DetalleCompra();
        detail.setProducto(producto);
        detail.setPrecioUnitario(price);
        return detail;
    }
}
