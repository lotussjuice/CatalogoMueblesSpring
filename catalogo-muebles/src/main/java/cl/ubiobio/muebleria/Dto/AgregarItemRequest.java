package cl.ubiobio.muebleria.Dto;

/*
 Dto para agregar un item a una cotización
 Para ser usado en el endpoint correspondiente y obtener el json de la request
 */
public class AgregarItemRequest {

    private Long muebleId;
    private Long varianteId;
    private int cantidad;

    public Long getMuebleId() {
        return muebleId;
    }
    public void setMuebleId(Long muebleId) {
        this.muebleId = muebleId;
    }
    public Long getVarianteId() {
        return varianteId;
    }
    public void setVarianteId(Long varianteId) {
        this.varianteId = varianteId;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}