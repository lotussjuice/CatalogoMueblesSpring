package cl.ubiobio.muebleria.DataBase.Mueble;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuebleRepository extends JpaRepository<Mueble, Long> {
}