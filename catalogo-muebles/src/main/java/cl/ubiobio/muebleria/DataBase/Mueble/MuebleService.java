package  cl.ubiobio.muebleria.DataBase.Mueble;

import java.util.List;
import java.util.Optional;

public interface MuebleService {
    List<Mueble> listarTodos();
    Optional<Mueble> buscarPorId(Long id);
    Mueble crearMueble(Mueble.MuebleBuilder builder);
    Mueble actualizarMueble(Long id, Mueble muebleActualizado);
    void desactivarMueble(Long id);
    Mueble actualizarStock(Long id, int nuevoStock);
}