package cl.ubiobio.muebleria.DataBase.Cotizacion;


import cl.ubiobio.muebleria.Dto.AgregarItemRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    @Autowired
    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @PostMapping
    public ResponseEntity<Cotizacion> crearCotizacion() {
        Cotizacion nuevaCotizacion = cotizacionService.crearNuevaCotizacion();
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCotizacion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cotizacion> buscarCotizacionPorId(@PathVariable Long id) {
        return cotizacionService.buscarPorId(id)
            .map(ResponseEntity::ok) // 200 OK
            .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<Cotizacion> agregarItem(
            @PathVariable("id") Long cotizacionId,
            @RequestBody AgregarItemRequest request) {
        
        try {
            Cotizacion cotizacionActualizada = cotizacionService.agregarItemACotizacion(
                cotizacionId,
                request.getMuebleId(),
                request.getVarianteId(),
                request.getCantidad()
            );
            return ResponseEntity.ok(cotizacionActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarVenta(@PathVariable("id") Long cotizacionId) {
        try {
            Cotizacion cotizacionConfirmada = cotizacionService.confirmarVenta(cotizacionId);
            return ResponseEntity.ok(cotizacionConfirmada);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Stock insuficiente")) {
                // 400 seteado para verificar en postman
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            // 404 si la cotización no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}