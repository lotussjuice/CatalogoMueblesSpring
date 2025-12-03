package cl.ubiobio.muebleria.DataBase.Cotizacion;

import cl.ubiobio.muebleria.util.EstadoCotizacion;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import cl.ubiobio.muebleria.DataBase.ItemCotizacion.ItemCotizacion;

// Cotizacion con multiples items asociados (como un listado de compra)
@Entity
@Table(name = "cotizaciones")
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotizacion")
    private Long id;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCotizacion estado;

    @Column(name = "total_calculado")
    private double totalCalculado;
    
    // Relación: Una cotización tiene muchos items
    @OneToMany(
        mappedBy = "cotizacion",
        cascade = CascadeType.ALL, // Si borro la cotización, borro sus items
        orphanRemoval = true,
        fetch = FetchType.EAGER // Cargar los items al cargar la cotización
    )
    private List<ItemCotizacion> items = new ArrayList<>();
    
    public Cotizacion() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoCotizacion.PENDIENTE;
        this.totalCalculado = 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public EstadoCotizacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoCotizacion estado) {
        this.estado = estado;
    }

    public double getTotalCalculado() {
        return totalCalculado;
    }

    public void setTotalCalculado(double totalCalculado) {
        this.totalCalculado = totalCalculado;
    }

    public List<ItemCotizacion> getItems() {
        return items;
    }

    public void setItems(List<ItemCotizacion> items) {
        this.items = items;
        for (ItemCotizacion item : items) {
            item.setCotizacion(this);
        }
    }

    public Double getTotal() {
        return totalCalculado;
    }
    
    // Metodo para agregar un item a la cotizacion
    public void addItem(ItemCotizacion item) {
        this.items.add(item);
        item.setCotizacion(this);
    }
}