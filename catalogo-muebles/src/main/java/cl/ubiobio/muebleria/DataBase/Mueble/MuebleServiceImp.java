    package cl.ubiobio.muebleria.DataBase.Mueble;

    import cl.ubiobio.muebleria.util.EstadoMueble;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;
    import java.util.Optional;

    @Service
    public class MuebleServiceImp implements MuebleService {

        private final MuebleRepository muebleRepository;

        @Autowired
        public MuebleServiceImp(MuebleRepository muebleRepository) {
            this.muebleRepository = muebleRepository;
        }

        @Override
        @Transactional(readOnly = true)
        public List<Mueble> listarTodos() {
            return muebleRepository.findAll();
        }

        @Override
        @Transactional(readOnly = true)
        public Optional<Mueble> buscarPorId(Long id) {
            return muebleRepository.findById(id);
        }

        @Override
        @Transactional
        public Mueble crearMueble(Mueble.MuebleBuilder builder) {
            // Implementadno builder
            Mueble nuevoMueble = builder.build();
            return muebleRepository.save(nuevoMueble);
        }

        @Override
        @Transactional
        public Mueble actualizarMueble(Long id, Mueble muebleActualizado) {
            return muebleRepository.findById(id)
                .map(muebleExistente -> {
                    muebleExistente.setNombre(muebleActualizado.getNombre());
                    muebleExistente.setTipo(muebleActualizado.getTipo());
                    muebleExistente.setPrecioBase(muebleActualizado.getPrecioBase());
                    muebleExistente.setStock(muebleActualizado.getStock());
                    muebleExistente.setEstado(muebleActualizado.getEstado());
                    muebleExistente.setTamano(muebleActualizado.getTamano());
                    muebleExistente.setMaterial(muebleActualizado.getMaterial());
                    return muebleRepository.save(muebleExistente);
                })
                .orElseThrow(() -> new RuntimeException("Mueble no encontrado con id: " + id));
        }

        @Override
        @Transactional
        public void desactivarMueble(Long id) {
            Mueble mueble = muebleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mueble no encontrado con id: " + id));
            
            mueble.setEstado(EstadoMueble.INACTIVO); // Requisito de desactivar 
            muebleRepository.save(mueble);
        }
        
        @Override
        @Transactional
        public Mueble actualizarStock(Long id, int nuevoStock) {
            Mueble mueble = muebleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mueble no encontrado con id: " + id));
            
            mueble.setStock(nuevoStock);
            return muebleRepository.save(mueble);
        }
    }