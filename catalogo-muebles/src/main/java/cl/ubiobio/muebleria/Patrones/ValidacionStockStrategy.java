package cl.ubiobio.muebleria.Patrones;


import cl.ubiobio.muebleria.DataBase.Mueble.Mueble;

/*
 Implementacion de patron Strategy para validacion de stock
 Esto para planificar futuras extensiones en la logica de validacion de stock
 Placeholder como patron de diseño
 */
public interface ValidacionStockStrategy {
    
    // valida stock para un mueble y cantidad requerida
    boolean validar(Mueble mueble, int cantidadRequerida);
}