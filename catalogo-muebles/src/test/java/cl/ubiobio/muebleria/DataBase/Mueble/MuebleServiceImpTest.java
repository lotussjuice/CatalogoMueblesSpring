package cl.ubiobio.muebleria.DataBase.Mueble;

import cl.ubiobio.muebleria.util.EstadoMueble;
import cl.ubiobio.muebleria.util.TamanoMueble;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/*
    Pruebas unitarias para MuebleServiceImp usando mocks
    Se mockea la dependencia MuebleRepository
    Pruebas de Create, Read y Update (desactivar)
 */
@SpringBootTest(classes = MuebleServiceImp.class)
public class MuebleServiceImpTest {


    @MockBean
    private MuebleRepository muebleRepository;

    // @Autowired inyecta interfaz del servicio con su implementación real
    @Autowired
    private MuebleService muebleService;

    private Mueble muebleDePrueba;

    @BeforeEach
    void setUp() {
        // Crear un objeto mueble de prueba para usar en los tests
        muebleDePrueba = new Mueble.MuebleBuilder("Silla de Oficina", 150.0, 20)
                .tipo("Silla")
                .tamano(TamanoMueble.MEDIANO)
                .material("Tela")
                .estado(EstadoMueble.ACTIVO)
                .build();
        muebleDePrueba.setId(1L); // Asignarle un ID
    }

    @Test
    @DisplayName("Test (Create): Crear un nuevo mueble")
    void testCrearMueble() {
        // Que cuando se llame al método save del repositorio mock, retorne el mueble 
        when(muebleRepository.save(any(Mueble.class))).thenReturn(muebleDePrueba);

        Mueble.MuebleBuilder builder = new Mueble.MuebleBuilder("Silla de Oficina", 150.0, 20)
                .tipo("Silla");

        Mueble resultado = muebleService.crearMueble(builder);

        assertNotNull(resultado);
        assertEquals("Silla de Oficina", resultado.getNombre());
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Test (Read): Buscar un mueble por ID")
    void testBuscarPorId() {
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleDePrueba));
        Optional<Mueble> resultado = muebleService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Silla de Oficina", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Test (Update): Desactivar un mueble")
    void testDesactivarMueble() {
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleDePrueba));
        ArgumentCaptor<Mueble> muebleCaptor = ArgumentCaptor.forClass(Mueble.class);

        muebleService.desactivarMueble(1L);

        verify(muebleRepository, times(1)).save(muebleCaptor.capture());
        
        // Verificar que efectivamente se desactivo el mueble
        assertEquals(EstadoMueble.INACTIVO, muebleCaptor.getValue().getEstado());
    }

    @Test
    @DisplayName("Test (Read): Listar todos los muebles")
    void testListarTodosLosMuebles() {
        // Crear una lista de muebles
        Mueble mueble1 = new Mueble.MuebleBuilder("Silla de Comedor", 80.0, 30).build();
        Mueble mueble2 = new Mueble.MuebleBuilder("Mesa de Noche", 120.0, 15).build();
        List<Mueble> listaDePrueba = Arrays.asList(mueble1, mueble2);

        // Retornar lista al llamar a findAll()
        when(muebleRepository.findAll()).thenReturn(listaDePrueba);

        // Emplear funcion de listado
        List<Mueble> resultado = muebleService.listarTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size()); // Verificamos que la lista tiene 2 elementos
        assertEquals("Silla de Comedor", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("Test (Update): Actualizar precio de un mueble")
    void testActualizarMueble() {
        Mueble muebleExistente = new Mueble.MuebleBuilder("Silla Vieja", 100.0, 10).build();
        muebleExistente.setId(1L);

        // Objeto con los datos de actualizacion
        Mueble datosNuevos = new Mueble.MuebleBuilder("Silla Nueva", 150.0, 20).build();
        
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleExistente));
        
        // thenAnswer permite definir comportamiento personalizado al llamar al mock
        when(muebleRepository.save(any(Mueble.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0); // Devuelve el mueble que fue guardado
        });

        Mueble resultado = muebleService.actualizarMueble(1L, datosNuevos);

        assertNotNull(resultado);
        // Verificar que los datos se actualizaron
        assertEquals(1L, resultado.getId()); 
        assertEquals("Silla Nueva", resultado.getNombre());
        assertEquals(150.0, resultado.getPrecioBase()); 
        assertEquals(20, resultado.getStock());
    }
}