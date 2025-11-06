package cl.ubiobio.muebleria.Patrones;

import cl.ubiobio.muebleria.DataBase.Mueble.Mueble;
import org.springframework.stereotype.Component;

/*
    Implementación concreta de la estrategia de validación de stock.
    Esta clase verifica si el stock disponible del mueble es suficiente
    para cubrir la cantidad requerida.
    Existe ya un método simple de validación de stock en el servicio de cotizaciones,
    pero se implementa este patrón para futuras extensiones y modificaciones.
    !! Recoradar utilizar este !!
*/
@Component("validacionSimple") // Nombrarla para inyección de dependencias
public class ValidacionStockSimple implements ValidacionStockStrategy {

    @Override
    public boolean validar(Mueble mueble, int cantidadRequerida) {
        if (mueble == null || cantidadRequerida <= 0) {
            return false;
        }
        return mueble.getStock() >= cantidadRequerida;
    }
}