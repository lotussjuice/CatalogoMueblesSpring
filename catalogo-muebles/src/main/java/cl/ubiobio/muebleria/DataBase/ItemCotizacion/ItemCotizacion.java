package cl.ubiobio.muebleria.DataBase.ItemCotizacion;

import jakarta.persistence.*;

import cl.ubiobio.muebleria.DataBase.Mueble.Mueble;
import cl.ubiobio.muebleria.DataBase.Variante.Variante;
import cl.ubiobio.muebleria.DataBase.Cotizacion.Cotizacion;

// Entidad para poner items por separado en una cotización
@Entity
@Table(name = "items_cotizacion")
public class ItemCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long id;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    // Muchos items pertenecen a un mueble (limitado por lógica a stock disponible)
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "mueble_id", nullable = false)
    private Mueble mueble;

    // Muchos items pueden tener una variante (cargo adicional)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "variante_id", nullable = true) // nullable=true permite mueble normal
    private Variante variante;

    // Muchos items pertenecen a una cotización
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;

    public ItemCotizacion() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Mueble getMueble() {
        return mueble;
    }

    public void setMueble(Mueble mueble) {
        this.mueble = mueble;
    }

    public Variante getVariante() {
        return variante;
    }

    public void setVariante(Variante variante) {
        this.variante = variante;
    }

    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }

    // Método para calcular el subtotal de este item en la cotización
    public double calcularSubtotal() {
        double precioUnitario = mueble.getPrecioBase();
        
        if (variante != null) {
            precioUnitario += variante.getAumentoPrecio();
        }
        return precioUnitario * cantidad;
    }
}