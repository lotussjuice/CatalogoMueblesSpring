package cl.ubiobio.muebleria.DataBase.ItemCotizacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCotizacionRepository extends JpaRepository<ItemCotizacion, Long> {
}