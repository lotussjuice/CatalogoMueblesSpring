package cl.ubiobio.muebleria.DataBase.Variante;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import cl.ubiobio.muebleria.DataBase.Mueble.Mueble;

// Entidad con variaciones, aplicables a muchos muebles y con costo adicional
@Entity
@Table(name = "variantes")
public class Variante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_variante")
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "aumento_precio", nullable = false)
    private double aumentoPrecio; // El costo adicional de esta variante

    // Relación: Una variante puede aplicar a muchos muebles
    @ManyToMany(mappedBy = "variantesDisponibles", fetch = FetchType.LAZY)
    private List<Mueble> mueblesAplicables = new ArrayList<>();

    // 
    public Variante() {
    }

    public Variante(String nombre, double aumentoPrecio) {
        this.nombre = nombre;
        this.aumentoPrecio = aumentoPrecio;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getAumentoPrecio() {
        return aumentoPrecio;
    }

    public void setAumentoPrecio(double aumentoPrecio) {
        this.aumentoPrecio = aumentoPrecio;
    }

    public List<Mueble> getMueblesAplicables() {
        return mueblesAplicables;
    }

    public void setMueblesAplicables(List<Mueble> mueblesAplicables) {
        this.mueblesAplicables = mueblesAplicables;
    }
}