package cl.ubiobio.muebleria.DataBase.Cotizacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.ubiobio.muebleria.DataBase.ItemCotizacion.ItemCotizacion;
import cl.ubiobio.muebleria.DataBase.ItemCotizacion.ItemCotizacionRepository;
import cl.ubiobio.muebleria.DataBase.Mueble.Mueble;
import cl.ubiobio.muebleria.DataBase.Mueble.MuebleRepository;
import cl.ubiobio.muebleria.DataBase.Mueble.MuebleService;
import cl.ubiobio.muebleria.DataBase.Variante.Variante;
import cl.ubiobio.muebleria.DataBase.Variante.VarianteRepository;
import cl.ubiobio.muebleria.Patrones.ValidacionStockStrategy;
import cl.ubiobio.muebleria.util.EstadoCotizacion;

import java.util.Optional;

@Service
public class CotizacionServiceImp implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final VarianteRepository varianteRepository;
    private final ItemCotizacionRepository itemCotizacionRepository;
    private final ValidacionStockStrategy validacionStock;
    private final MuebleService muebleService;

    @Autowired
    public CotizacionServiceImp(CotizacionRepository cotizacionRepository,
                                  VarianteRepository varianteRepository,
                                  ItemCotizacionRepository itemCotizacionRepository,
                                  @Qualifier("validacionSimple") ValidacionStockStrategy validacionStock,
                                  MuebleService muebleService) {
        this.cotizacionRepository = cotizacionRepository;
        this.varianteRepository = varianteRepository;
        this.itemCotizacionRepository = itemCotizacionRepository;
        this.validacionStock = validacionStock; 
        this.muebleService = muebleService; 
    }

    @Override
    @Transactional
    public Cotizacion crearNuevaCotizacion() {
        Cotizacion nuevaCotizacion = new Cotizacion();
        return cotizacionRepository.save(nuevaCotizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cotizacion> buscarPorId(Long id) {
        return cotizacionRepository.findById(id);
    }

    @Override
    @Transactional
    public Cotizacion agregarItemACotizacion(Long cotizacionId, Long muebleId, Long varianteId, int cantidad) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
            .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        
        Mueble mueble = muebleService.buscarPorId(muebleId)
            .orElseThrow(() -> new RuntimeException("Mueble no encontrado"));
        
        Variante variante = null;
        if (varianteId != null) {
            variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new RuntimeException("Variante no encontrada"));
        }

        // Sin variante utilizar el precio base del mueble
        ItemCotizacion nuevoItem = new ItemCotizacion();
        nuevoItem.setMueble(mueble);
        nuevoItem.setVariante(variante);
        nuevoItem.setCantidad(cantidad);
        
        cotizacion.addItem(nuevoItem); 
        
        cotizacionRepository.save(cotizacion); // Guardarla cotización, los items se guardan en cascada
        return recalcularTotal(cotizacionId);
    }

    @Override
    @Transactional
    public Cotizacion recalcularTotal(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
            .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        
        double total = 0.0;
        for (ItemCotizacion item : cotizacion.getItems()) {
            total += item.calcularSubtotal();
        }
        
        cotizacion.setTotalCalculado(total);
        return cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional
    public Cotizacion confirmarVenta(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
            .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        
        if (cotizacion.getEstado() == EstadoCotizacion.CONFIRMADA) {
            throw new RuntimeException("Esta cotización ya fue confirmada como venta.");
        }

        // Verificar stock en cada item
        for (ItemCotizacion item : cotizacion.getItems()) {
            Mueble mueble = item.getMueble();
            int cantidadRequerida = item.getCantidad();
            
            if (!validacionStock.validar(mueble, cantidadRequerida)) {
                throw new RuntimeException("Stock insuficiente para el producto: " + mueble.getNombre());
            }
        }
        
        for (ItemCotizacion item : cotizacion.getItems()) {
            Mueble mueble = item.getMueble();
            int nuevoStock = mueble.getStock() - item.getCantidad();
            muebleService.actualizarStock(mueble.getId(), nuevoStock); // <-- BIEN
        }
        
        cotizacion.setEstado(EstadoCotizacion.CONFIRMADA);
        return cotizacionRepository.save(cotizacion);
    }
}