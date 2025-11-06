package cl.ubiobio.muebleria.DataBase.Cotizacion;

import java.util.Optional;


public interface CotizacionService {
    
    Cotizacion crearNuevaCotizacion();
    Cotizacion agregarItemACotizacion(Long cotizacionId, Long muebleId, Long varianteId, int cantidad);
    Cotizacion recalcularTotal(Long cotizacionId);
    Optional<Cotizacion> buscarPorId(Long id);
    Cotizacion confirmarVenta(Long cotizacionId);
    
}