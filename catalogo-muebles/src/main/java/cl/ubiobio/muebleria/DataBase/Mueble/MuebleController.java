    package cl.ubiobio.muebleria.DataBase.Mueble;

    import cl.ubiobio.muebleria.util.EstadoMueble;
    import cl.ubiobio.muebleria.Dto.CrearMuebleRequest;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Optional;

    /*
    Rest Controller para gestionar los endpoints relacionados 
    Esto indica al sistema que esta clase manejará solicitudes HTTP
    Retorna y recibe datos en formato JSON
    */
    @RestController
    @RequestMapping("/api/muebles") 
    public class MuebleController {

        private final MuebleService muebleService;

        @Autowired
        public MuebleController(MuebleService muebleService) {
            this.muebleService = muebleService;
        }

        @GetMapping
        public List<Mueble> listarMuebles() {
            return muebleService.listarTodos();
        }

        
        //  Endpoint para buscar un mueble por ID
        @GetMapping("/{id}")
        public ResponseEntity<Mueble> buscarMueblePorId(@PathVariable Long id) {
            Optional<Mueble> mueble = muebleService.buscarPorId(id);
            return mueble.map(m -> ResponseEntity.ok(m)) // 200 OK
                        .orElse(ResponseEntity.notFound().build()); // 404 Not Found
        }

        // Endpoint para Crear un nuevo mueble
        @PostMapping
        public ResponseEntity<Mueble> crearMueble(@RequestBody CrearMuebleRequest request) {
            
            // Mapeamos el DTO al Builder que espera nuestro servicio
            Mueble.MuebleBuilder builder = new Mueble.MuebleBuilder(
                request.getNombre(),
                request.getPrecioBase(),
                request.getStock()
            )
            .tipo(request.getTipo())
            .material(request.getMaterial())
            .tamano(request.getTamano())
            .estado(request.getEstado() != null ? request.getEstado() : EstadoMueble.ACTIVO);

            Mueble nuevoMueble = muebleService.crearMueble(builder);
            
            // Retorno de status 201 Created con el mueble creado para el testeo
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMueble);
        }

        @PutMapping("/{id}")
        public ResponseEntity<Mueble> actualizarMueble(@PathVariable Long id, @RequestBody Mueble muebleActualizado) {
            try {
                Mueble mueble = muebleService.actualizarMueble(id, muebleActualizado);
                return ResponseEntity.ok(mueble);
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            }
        }

        @PutMapping("/{id}/desactivar")
        public ResponseEntity<Void> desactivarMueble(@PathVariable Long id) {
            try {
                muebleService.desactivarMueble(id);
                return ResponseEntity.noContent().build(); // 204 No Content
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            }
        }
    }