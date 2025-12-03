package cl.ubiobio.muebleria.DataBase.Mueble;


import cl.ubiobio.muebleria.Dto.CrearMuebleRequest;
import cl.ubiobio.muebleria.CatalogoMueblesApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MuebleController.class) 
@Import(CatalogoMueblesApplication.class)
public class MuebleControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular peticiones HTTP

    @MockBean
    private MuebleService muebleService; // Mockeamos el servicio

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @Test
    void testGetListarMuebles() throws Exception {
        // 1. Arrange
        Mueble mueble1 = new Mueble.MuebleBuilder("Silla", 100.0, 10).build();
        Mueble mueble2 = new Mueble.MuebleBuilder("Mesa", 200.0, 5).build();
        List<Mueble> lista = Arrays.asList(mueble1, mueble2);

        when(muebleService.listarTodos()).thenReturn(lista);

        // 2. Act & 3. Assert
        mockMvc.perform(get("/api/muebles"))
                .andExpect(status().isOk()) // Esperamos un 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2)) // JSONPath para verificar el JSON
                .andExpect(jsonPath("$[0].nombre").value("Silla"));
    }

    @Test
    void testPostCrearMueble() throws Exception {
        // 1. Arrange
        CrearMuebleRequest requestDto = new CrearMuebleRequest();
        requestDto.setNombre("Silla Nueva");
        requestDto.setPrecioBase(150.0);
        requestDto.setStock(10);

        Mueble muebleCreado = new Mueble.MuebleBuilder("Silla Nueva", 150.0, 10).build();
        muebleCreado.setId(1L); // El servicio devolvería un mueble con ID

        when(muebleService.crearMueble(any())).thenReturn(muebleCreado);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/muebles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))) // Convertimos el DTO a JSON
                .andExpect(status().isCreated()) // Esperamos un 201 Created
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Silla Nueva"));
    }
}