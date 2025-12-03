package cl.ubiobio.muebleria.Dto;

import cl.ubiobio.muebleria.util.EstadoMueble;
import cl.ubiobio.muebleria.util.TamanoMueble;
/*
DTO para la creación de un nuevo mueble
Retorno de datos desde el cliente al servidor
*/
public class CrearMuebleRequest {

    private String nombre;
    private String tipo;
    private double precioBase;
    private int stock;
    private EstadoMueble estado;
    private TamanoMueble tamano;
    private String material;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public double getPrecioBase() {
        return precioBase;
    }
    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public EstadoMueble getEstado() {
        return estado;
    }
    public void setEstado(EstadoMueble estado) {
        this.estado = estado;
    }
    public TamanoMueble getTamano() {
        return tamano;
    }
    public void setTamano(TamanoMueble tamano) {
        this.tamano = tamano;
    }
    public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
}