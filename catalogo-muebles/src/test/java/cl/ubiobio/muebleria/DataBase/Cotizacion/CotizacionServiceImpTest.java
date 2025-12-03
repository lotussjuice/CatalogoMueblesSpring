package cl.ubiobio.muebleria.DataBase.Cotizacion;

import cl.ubiobio.muebleria.DataBase.ItemCotizacion.ItemCotizacion;
import cl.ubiobio.muebleria.DataBase.ItemCotizacion.ItemCotizacionRepository;
import cl.ubiobio.muebleria.DataBase.Mueble.Mueble;
import cl.ubiobio.muebleria.DataBase.Mueble.MuebleRepository;
import cl.ubiobio.muebleria.DataBase.Mueble.MuebleService;
import cl.ubiobio.muebleria.DataBase.Variante.Variante;
import cl.ubiobio.muebleria.Patrones.ValidacionStockSimple;
import cl.ubiobio.muebleria.DataBase.Variante.VarianteRepository;
import cl.ubiobio.muebleria.util.EstadoCotizacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/*
    Pruebas unitarias para cotizar empleando mocks y el servicio correspondiente
    Se mockean las dependencias del servicio, incluyendo la interfaz MuebleService
 */
@SpringBootTest(classes = {CotizacionServiceImp.class, ValidacionStockSimple.class})
public class CotizacionServiceImpTest {

    @MockBean
    private CotizacionRepository cotizacionRepository;
    @MockBean
    private VarianteRepository varianteRepository;
    @MockBean
    private MuebleService muebleService;
    @MockBean 
    private MuebleRepository muebleRepository;
    @MockBean
    private ItemCotizacionRepository itemCotizacionRepository;

    // Servicio bajo prueba
    @Autowired
    private CotizacionService cotizacionService; 

    // Objetos de prueba
    private Mueble muebleBase;
    private Variante variantePrueba;
    private Cotizacion cotizacionPrueba;

    // Crear objetos de prueba antes de cada test
    @BeforeEach
    void setUp() {
        muebleBase = new Mueble.MuebleBuilder("Mesa de Centro", 100.0, 10).build();
        muebleBase.setId(10L);

        variantePrueba = new Variante("Barniz Premium", 50.0);
        variantePrueba.setId(20L);

        cotizacionPrueba = new Cotizacion();
        cotizacionPrueba.setId(1L);
    }

    // Calcular precio a cotizar de un mueble agregandole una variante plus
    @Test
    @DisplayName("Test Precios: Agregar item CON variante recalcula total")
    void testAgregarItemConVariante_RecalculaTotal() {
        // 
        // (Base: 100 + Variante: 50) * Cantidad: 2 = 300.0
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacionPrueba));
        when(muebleService.buscarPorId(10L)).thenReturn(Optional.of(muebleBase));
        when(varianteRepository.findById(20L)).thenReturn(Optional.of(variantePrueba));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cotizacion resultado = cotizacionService.agregarItemACotizacion(1L, 10L, 20L, 2);

        // Revisamos resultados
        assertNotNull(resultado);
        assertEquals(1, resultado.getItems().size());
        assertEquals(300.0, resultado.getTotalCalculado());
    }

    @Test
    @DisplayName("Test Precios: Agregar item SIN variante recalcula total")
    void testAgregarItemSinVariante_RecalculaTotal() {
        // (Base: 100 + Variante: null) * Cantidad: 3 = 300.0
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacionPrueba));
        when(muebleService.buscarPorId(10L)).thenReturn(Optional.of(muebleBase));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cotizacion resultado = cotizacionService.agregarItemACotizacion(1L, 10L, null, 3);

        // Verrificamos resultados sin variannte
        assertNotNull(resultado);
        assertEquals(1, resultado.getItems().size());
        assertEquals(300.0, resultado.getTotalCalculado());
    }

    @Test
    @DisplayName("Test Stock/Venta: Confirmar venta con stock suficiente")
    void testConfirmarVenta_StockSuficiente() {
        ItemCotizacion item = new ItemCotizacion();
        item.setMueble(muebleBase);
        item.setCantidad(2); // Stock es 10, queremos 2
        cotizacionPrueba.addItem(item);

        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacionPrueba));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cotizacion resultado = cotizacionService.confirmarVenta(1L);

        assertEquals(EstadoCotizacion.CONFIRMADA, resultado.getEstado());
        // Verificamos llamada al mock correspondiente y disminucion del stock
        verify(muebleService, times(1)).actualizarStock(10L, 8  ); // (10 - 2)
    }

    @Test
    @DisplayName("Test Stock/Venta: Confirmar venta con stock insuficiente")
    void testConfirmarVenta_StockInsuficiente() {
        ItemCotizacion item = new ItemCotizacion();
        item.setMueble(muebleBase);
        item.setCantidad(11); // Stock es 10, queremos 11
        cotizacionPrueba.addItem(item);

        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacionPrueba));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cotizacionService.confirmarVenta(1L);
        });

        assertTrue(exception.getMessage().contains("Stock insuficiente"));

        // Verificar que NUNCA se llamó al mock de MuebleService
        verify(muebleService, never()).actualizarStock(anyLong(), anyInt());
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }
}