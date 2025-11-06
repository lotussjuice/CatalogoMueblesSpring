package cl.ubiobio.muebleria.DataBase.Mueble;

import cl.ubiobio.muebleria.util.EstadoMueble;
import cl.ubiobio.muebleria.util.TamanoMueble;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import cl.ubiobio.muebleria.DataBase.Variante.Variante;

// Entidad que representa un Mueble en el sistema
@Entity
@Table(name = "muebles")
public class Mueble {

    // Estructuracion de la tabla "muebles" en la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mueble")
    private Long id;

    @Column(name = "nombre_mueble", nullable = false)
    private String nombre;

    @Column(name = "tipo")
    private String tipo; // Ej: "silla", "mesa" 

    @Column(name = "precio_base", nullable = false)
    private double precioBase;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoMueble estado; // ACTIVO, INACTIVO 

    @Enumerated(EnumType.STRING)
    @Column(name = "tamano")
    private TamanoMueble tamano; // GRANDE, MEDIANO, PEQUENO 

    @Column(name = "material")
    private String material;

    // Relación: Un mueble puede tener muchas variantes disponibles.
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "mueble_variantes_disponibles",
        joinColumns = @JoinColumn(name = "mueble_id"),
        inverseJoinColumns = @JoinColumn(name = "variante_id")
    )
    private List<Variante> variantesDisponibles = new ArrayList<>();

    
    // Implementacion de patron builder para facilitar la creacion de objetos Mueble
    // Contructor público para JPA
    public Mueble() {
    }

    // Contructor para builder
    private Mueble(MuebleBuilder builder) {
        this.nombre = builder.nombre;
        this.tipo = builder.tipo;
        this.precioBase = builder.precioBase;
        this.stock = builder.stock;
        this.estado = builder.estado;
        this.tamano = builder.tamano;
        this.material = builder.material;
    }

    public Long getId() { return id; }
    
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre;}

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo;}

    public double getPrecioBase() { return precioBase; }

    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }

    public int getStock() { return stock;}

    public void setStock(int stock) { this.stock = stock; }

    public EstadoMueble getEstado() { return estado;}

    public void setEstado(EstadoMueble estado) { this.estado = estado;}

    public TamanoMueble getTamano() { return tamano; }

    public void setTamano(TamanoMueble tamano) { this.tamano = tamano; }

    public String getMaterial() { return material; }

    public void setMaterial(String material) { this.material = material; }

    public List<Variante> getVariantesDisponibles() {
        return variantesDisponibles;
    }

    public void setVariantesDisponibles(List<Variante> variantesDisponibles) {
        this.variantesDisponibles = variantesDisponibles;
    }
    
    public void addVarianteDisponible(Variante variante) {
        this.variantesDisponibles.add(variante);
        variante.getMueblesAplicables().add(this);
    }

    // Clase Builder para la entidad
    public static class MuebleBuilder {
        private String nombre;
        private String tipo;
        private double precioBase;
        private int stock;
        private EstadoMueble estado = EstadoMueble.ACTIVO; // Valor por defecto
        private TamanoMueble tamano;
        private String material;

        public MuebleBuilder(String nombre, double precioBase, int stock) {
            this.nombre = nombre;
            this.precioBase = precioBase;
            this.stock = stock;
        }

        public MuebleBuilder tipo(String tipo) {
            this.tipo = tipo;
            return this;
        }

        public MuebleBuilder estado(EstadoMueble estado) {
            this.estado = estado;
            return this;
        }

        public MuebleBuilder tamano(TamanoMueble tamano) {
            this.tamano = tamano;
            return this;
        }

        public MuebleBuilder material(String material) {
            this.material = material;
            return this;
        }

        public Mueble build() {
            return new Mueble(this);
        }
    }
}