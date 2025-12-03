package cl.ubiobio.muebleria.DataBase.Cotizacion;

import java.util.Optional;
import java.util.List;


public interface CotizacionService {
    
    Cotizacion crearNuevaCotizacion();
    Cotizacion agregarItemACotizacion(Long cotizacionId, Long muebleId, Long varianteId, int cantidad);
    Cotizacion recalcularTotal(Long cotizacionId);
    Optional<Cotizacion> buscarPorId(Long id);
    Cotizacion confirmarVenta(Long cotizacionId);
    List<Cotizacion> listarTodas();
    void eliminarItem(Long itemId);
    Cotizacion obtenerCotizacionActual(); 
    Cotizacion modificarCantidadItem(Long itemId, int nuevaCantidad);
}