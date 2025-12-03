package cl.ubiobio.muebleria;

import cl.ubiobio.muebleria.DataBase.Mueble.Mueble;
import cl.ubiobio.muebleria.DataBase.Mueble.MuebleRepository;
import cl.ubiobio.muebleria.DataBase.Variante.Variante;
import cl.ubiobio.muebleria.DataBase.Variante.VarianteRepository;
import cl.ubiobio.muebleria.util.EstadoMueble;
import cl.ubiobio.muebleria.util.TamanoMueble;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component 
public class DataLoader implements CommandLineRunner { 

    private final MuebleRepository muebleRepository;
    private final VarianteRepository varianteRepository;

    // Inyeccion por constructor
    public DataLoader(MuebleRepository muebleRepository, VarianteRepository varianteRepository) {
        this.muebleRepository = muebleRepository;
        this.varianteRepository = varianteRepository;
    }

    @Override
    @Transactional // <--Mantiene la sesión abierta todo el tiempo
    public void run(String... args) throws Exception {
        
        if (varianteRepository.count() == 0) {
            Variante v1 = new Variante("Barniz Natural (Roble)", 5000.0);
            Variante v2 = new Variante("Barniz Oscuro (Nogal)", 6500.0);
            Variante v3 = new Variante("Lacado Blanco Mate", 8000.0);
            Variante v4 = new Variante("Negro Industrial", 9000.0);
            Variante v5 = new Variante("Tapiz Gris Premium", 15000.0);

            varianteRepository.saveAll(Arrays.asList(v1, v2, v3, v4, v5));
        }

        if (muebleRepository.count() == 0) {
            List<Variante> variantes = varianteRepository.findAll();
            
            Variante vNatural = variantes.get(0);
            Variante vOscuro = variantes.get(1);
            Variante vBlanco = variantes.get(2);
            Variante vNegro = variantes.get(3);
            Variante vTapiz = variantes.get(4);

            Mueble m1 = new Mueble.MuebleBuilder("Comedor Familiar 6 Personas", 180000.0, 5)
                    .tipo("Mesa").material("Madera Nativa").tamano(TamanoMueble.GRANDE)
                    .estado(EstadoMueble.ACTIVO).build();
            m1.addVarianteDisponible(vNatural);
            m1.addVarianteDisponible(vOscuro);

            Mueble m2 = new Mueble.MuebleBuilder("Silla Nórdica", 35000.0, 30)
                    .tipo("Silla").material("Pino y Tela").tamano(TamanoMueble.PEQUENO)
                    .estado(EstadoMueble.ACTIVO).build();
            m2.addVarianteDisponible(vBlanco);
            m2.addVarianteDisponible(vNegro);

            Mueble m3 = new Mueble.MuebleBuilder("Sofá Chesterfield 3 Cuerpos", 450000.0, 3)
                    .tipo("Sillón").material("Cuero Sintético").tamano(TamanoMueble.GRANDE)
                    .estado(EstadoMueble.ACTIVO).build();
            m3.addVarianteDisponible(vTapiz);

            Mueble m4 = new Mueble.MuebleBuilder("Escritorio Home Office", 85000.0, 10)
                    .tipo("Escritorio").material("Melamina").tamano(TamanoMueble.MEDIANO)
                    .estado(EstadoMueble.ACTIVO).build();
            m4.addVarianteDisponible(vNegro);
            m4.addVarianteDisponible(vNatural);


            Mueble m5 = new Mueble.MuebleBuilder("Velador Minimalista", 25000.0, 15)
                    .tipo("Velador").material("MDF").tamano(TamanoMueble.PEQUENO)
                    .estado(EstadoMueble.ACTIVO).build();
            m5.addVarianteDisponible(vBlanco);

            muebleRepository.saveAll(Arrays.asList(m1, m2, m3, m4, m5));
        }
    }
}