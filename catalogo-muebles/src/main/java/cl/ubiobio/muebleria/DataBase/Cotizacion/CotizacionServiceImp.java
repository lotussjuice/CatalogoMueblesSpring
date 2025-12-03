package cl.ubiobio.muebleria.DataBase.Cotizacion;

import cl.ubiobio.muebleria.DataBase.ItemCotizacion.ItemCotizacion;
import cl.ubiobio.muebleria.DataBase.ItemCotizacion.ItemCotizacionRepository;
import cl.ubiobio.muebleria.DataBase.Mueble.Mueble;
import cl.ubiobio.muebleria.DataBase.Mueble.MuebleService;
import cl.ubiobio.muebleria.DataBase.Variante.Variante;
import cl.ubiobio.muebleria.DataBase.Variante.VarianteRepository;
import cl.ubiobio.muebleria.util.EstadoCotizacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CotizacionServiceImp implements CotizacionService {

    @Autowired private CotizacionRepository cotizacionRepository;
    @Autowired private VarianteRepository varianteRepository;
    @Autowired private ItemCotizacionRepository itemCotizacionRepository;
    @Autowired private MuebleService muebleService;

    @Override
    public Cotizacion crearNuevaCotizacion() {
        Cotizacion nueva = new Cotizacion();
        return cotizacionRepository.save(nueva);
    }

    @Override
    public Cotizacion obtenerCotizacionActual() {
        return cotizacionRepository.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCotizacion.PENDIENTE)
                .findFirst()
                .orElseGet(this::crearNuevaCotizacion);
    }

    @Override
    @Transactional
    public Cotizacion agregarItemACotizacion(Long cotizacionId, Long muebleId, Long varianteId, int cantidad) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
            .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        Mueble mueble = muebleService.buscarPorId(muebleId)
            .orElseThrow(() -> new RuntimeException("Mueble no encontrado"));

        if (mueble.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Solo quedan " + mueble.getStock());
        }

        Optional<ItemCotizacion> itemExistente = cotizacion.getItems().stream()
                .filter(i -> i.getMueble().getId().equals(muebleId) &&
                        ((varianteId == null && i.getVariante() == null) || 
                         (varianteId != null && i.getVariante() != null && i.getVariante().getId().equals(varianteId))))
                .findFirst();

        if (itemExistente.isPresent()) {
            return modificarCantidadItem(itemExistente.get().getId(), itemExistente.get().getCantidad() + cantidad);
        }

        muebleService.actualizarStock(mueble.getId(), mueble.getStock() - cantidad);
        Variante variante = (varianteId != null) ? varianteRepository.findById(varianteId).orElse(null) : null;
        
        ItemCotizacion nuevoItem = new ItemCotizacion();
        nuevoItem.setMueble(mueble);
        nuevoItem.setVariante(variante);
        nuevoItem.setCantidad(cantidad);
        
        cotizacion.addItem(nuevoItem);
        cotizacionRepository.save(cotizacion); // Guardar cascada
        
        return recalcularTotal(cotizacionId);
    }

    @Override
    @Transactional
    public void eliminarItem(Long itemId) {
        ItemCotizacion item = itemCotizacionRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        
        Long cotizacionId = item.getCotizacion().getId();
        Mueble mueble = item.getMueble();

        muebleService.actualizarStock(mueble.getId(), mueble.getStock() + item.getCantidad());
        itemCotizacionRepository.delete(item);
        itemCotizacionRepository.flush(); // Forzar borrado inmediato
        recalcularTotal(cotizacionId);
    }

    @Override
    @Transactional
    public Cotizacion modificarCantidadItem(Long itemId, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            eliminarItem(itemId); 
            return obtenerCotizacionActual(); 
        }

        ItemCotizacion item = itemCotizacionRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        
        Mueble mueble = item.getMueble();
        int cantidadAnterior = item.getCantidad();
        int diferencia = nuevaCantidad - cantidadAnterior;
        
        if (diferencia > 0) {
            // AUMENTAR cantidad -> Validar si hay stock suficiente para la diferencia
            if (mueble.getStock() < diferencia) {
                throw new RuntimeException("No hay suficiente stock para aumentar la cantidad.");
            }
            // Descontar del inventario
            muebleService.actualizarStock(mueble.getId(), mueble.getStock() - diferencia);
        } else if (diferencia < 0) {
            // DISMINUIR cantidad -> Devolver stock al inventario
            muebleService.actualizarStock(mueble.getId(), mueble.getStock() - diferencia);
        }

        // Actualizar el item
        item.setCantidad(nuevaCantidad);
        itemCotizacionRepository.save(item);

        return recalcularTotal(item.getCotizacion().getId());
    }

    @Override
    @Transactional
    public Cotizacion confirmarVenta(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
            .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        if (cotizacion.getEstado() == EstadoCotizacion.CONFIRMADA) {
            throw new RuntimeException("Esta cotización ya fue procesada.");
        }

        cotizacion.setEstado(EstadoCotizacion.CONFIRMADA);
        return cotizacionRepository.save(cotizacion);
    }

    // Método auxiliar privado
    public Cotizacion recalcularTotal(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId).orElseThrow();
        double total = 0.0;
        // Recargar items desde BD para asegurar consistencia
        for (ItemCotizacion item : cotizacion.getItems()) {
            double precioUnitario = item.getMueble().getPrecioBase();
            if (item.getVariante() != null) {
                precioUnitario += item.getVariante().getAumentoPrecio();
            }
            total += precioUnitario * item.getCantidad();
        }
        cotizacion.setTotalCalculado(total);
        return cotizacionRepository.save(cotizacion);
    }
    
    @Override
    public List<Cotizacion> listarTodas() {
        return cotizacionRepository.findAll();
    }

    public Optional<Cotizacion> buscarPorId(Long id) {
        return cotizacionRepository.findById(id);
    }
}