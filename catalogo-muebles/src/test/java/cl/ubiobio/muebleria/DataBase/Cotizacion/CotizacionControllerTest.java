package cl.ubiobio.muebleria.DataBase.Cotizacion;

import cl.ubiobio.muebleria.CatalogoMueblesApplication;
import cl.ubiobio.muebleria.Dto.AgregarItemRequest;
import cl.ubiobio.muebleria.util.EstadoCotizacion;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CotizacionController.class) 
@Import(CatalogoMueblesApplication.class)
public class CotizacionControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular peticiones HTTP

    @MockBean // Mockeo del servicio
    private CotizacionService cotizacionService;

    @Autowired
    private ObjectMapper objectMapper; // Para parsear objetos a JSON y revisar respuestas

    @Test
    @DisplayName("Test: Crear una nueva cotización vacía")
    void testPostCrearCotizacion() throws Exception {
        Cotizacion cotizacionNueva = new Cotizacion();
        cotizacionNueva.setId(1L);
        cotizacionNueva.setEstado(EstadoCotizacion.PENDIENTE);

        when(cotizacionService.crearNuevaCotizacion()).thenReturn(cotizacionNueva);

        mockMvc.perform(post("/api/cotizaciones"))
                .andExpect(status().isCreated()) // Se espera que se devuelva un 201 Created
                // Revisar el contenido JSON de la respuesta
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("Test: Agregar un ítem a una cotización")
    void testPostAgregarItem() throws Exception {
        AgregarItemRequest requestDto = new AgregarItemRequest();
        requestDto.setMuebleId(10L);
        requestDto.setVarianteId(20L);
        requestDto.setCantidad(2);

        // Simular una cotización actualizada después de agregar el ítem
        Cotizacion cotizacionActualizada = new Cotizacion();
        cotizacionActualizada.setId(1L);
        cotizacionActualizada.setTotalCalculado(300.0); 

        // Configurar el mock para que devuelva la cotización actualizada
        when(cotizacionService.agregarItemACotizacion(1L, 10L, 20L, 2))
                .thenReturn(cotizacionActualizada);

        mockMvc.perform(post("/api/cotizaciones/1/items") // URL con el ID de la cotización
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))) // Enviar dto como JSON 
                .andExpect(status().isOk()) // Se espera un 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalCalculado").value(300.0));
    }

    @Test
    @DisplayName("Test Venta: Confirmar venta con stock exitosa")
    void testPostConfirmarVenta_Exito() throws Exception {
        Cotizacion cotizacionConfirmada = new Cotizacion();
        cotizacionConfirmada.setId(1L);
        cotizacionConfirmada.setEstado(EstadoCotizacion.CONFIRMADA); 

        when(cotizacionService.confirmarVenta(1L)).thenReturn(cotizacionConfirmada);

        // Revisar el retorno del endpoint
        mockMvc.perform(post("/api/cotizaciones/1/confirmar"))
                .andExpect(status().isOk()) // Esperamos 200 OK
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    @DisplayName("Test Venta: Confirmar venta con stock insuficiente")
    void testPostConfirmarVenta_StockInsuficiente() throws Exception {
        String mensajeError = "Stock insuficiente para el producto: Silla";
        
        // Configurar mock para lanzar error
        when(cotizacionService.confirmarVenta(1L))
                .thenThrow(new RuntimeException(mensajeError));

        mockMvc.perform(post("/api/cotizaciones/1/confirmar"))
                .andExpect(status().isBadRequest()) // Error 400 como se definio en controller
                .andExpect(content().string(mensajeError)); // Verificar que llego el mensaje de error
    }
}