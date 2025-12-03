package cl.ubiobio.muebleria.System;

import cl.ubiobio.muebleria.DataBase.Cotizacion.Cotizacion;
import cl.ubiobio.muebleria.DataBase.Cotizacion.CotizacionRepository;
import cl.ubiobio.muebleria.DataBase.Cotizacion.CotizacionService;
import cl.ubiobio.muebleria.DataBase.Mueble.Mueble;
import cl.ubiobio.muebleria.DataBase.Mueble.MuebleRepository;
import cl.ubiobio.muebleria.DataBase.Mueble.MuebleService;
import cl.ubiobio.muebleria.DataBase.Variante.Variante;
import cl.ubiobio.muebleria.DataBase.Variante.VarianteRepository;
import cl.ubiobio.muebleria.util.EstadoMueble;
import cl.ubiobio.muebleria.util.TamanoMueble;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web")
public class WebController {

    @Autowired private MuebleService muebleService;
    @Autowired private MuebleRepository muebleRepository;
    @Autowired private CotizacionService cotizacionService;
    @Autowired private CotizacionRepository cotizacionRepository;
    @Autowired private VarianteRepository varianteRepository;

    @GetMapping("")
    public String index() { return "index"; }

    @GetMapping("/cliente/cotizar")
    public String vistaCotizar(Model model, @RequestParam(required = false) String error) {
        model.addAttribute("perfil", "cliente");
        model.addAttribute("active", "cotizar");
        model.addAttribute("muebles", muebleService.listarTodos()); // Mostrará stock en tiempo real
        model.addAttribute("variantes", varianteRepository.findAll());
        model.addAttribute("carrito", cotizacionService.obtenerCotizacionActual());
        
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "cotizar";
    }

    @PostMapping("/cliente/cotizar/agregar")
    public String agregarAlCarrito(@RequestParam Long muebleId, 
                                   @RequestParam(required = false) Long varianteId, 
                                   @RequestParam int cantidad) {
        try {
            Cotizacion carrito = cotizacionService.obtenerCotizacionActual();
            cotizacionService.agregarItemACotizacion(carrito.getId(), muebleId, varianteId, cantidad);
            return "redirect:/web/cliente/cotizar";
        } catch (Exception e) {
            return "redirect:/web/cliente/cotizar?error=" + e.getMessage();
        }
    }

    @PostMapping("/cliente/cotizar/modificar")
    public String modificarCantidad(@RequestParam Long itemId, @RequestParam int nuevaCantidad) {
        try {
            cotizacionService.modificarCantidadItem(itemId, nuevaCantidad);
            return "redirect:/web/cliente/cotizar";
        } catch (Exception e) {
            return "redirect:/web/cliente/cotizar?error=" + e.getMessage();
        }
    }

    @PostMapping("/cliente/cotizar/eliminar")
    public String eliminarDelCarrito(@RequestParam Long itemId) {
        cotizacionService.eliminarItem(itemId);
        return "redirect:/web/cliente/cotizar";
    }

    @PostMapping("/cliente/cotizar/comprar")
    public String finalizarCompra() {
        Cotizacion carrito = cotizacionService.obtenerCotizacionActual();
        cotizacionService.confirmarVenta(carrito.getId());
        cotizacionService.crearNuevaCotizacion(); // Carrito nuevo limpio
        return "redirect:/web/cliente/historial?exito=true";
    }

    @GetMapping("/cliente/historial")
    public String vistaHistorial(@RequestParam(required = false) Boolean exito, Model model) {
        model.addAttribute("perfil", "cliente");
        model.addAttribute("active", "historial");
        if(exito != null && exito) {
            model.addAttribute("mensaje", "¡Compra confirmada! (El stock ya había sido reservado)");
            model.addAttribute("tipoMensaje", "success");
        }
        model.addAttribute("cotizaciones", cotizacionRepository.findAll()); 
        return "historial";
    }

    // --- GUSTAVO (ADMIN) ---

    @GetMapping("/gustavo/inventario")
    public String vistaInventario(Model model) {
        model.addAttribute("perfil", "gustavo");
        model.addAttribute("active", "inventario");
        model.addAttribute("muebles", muebleService.listarTodos());
        return "inventario";
    }

    @PostMapping("/gustavo/inventario/stock")
    public String actualizarStock(@RequestParam Long muebleId, @RequestParam int nuevoStock) {
        muebleService.actualizarStock(muebleId, nuevoStock);
        return "redirect:/web/gustavo/inventario";
    }
    
    @GetMapping("/gustavo/crear")
    public String vistaCrear(Model model) {
        model.addAttribute("perfil", "gustavo");
        model.addAttribute("active", "crear");
        model.addAttribute("tamanos", TamanoMueble.values());
        model.addAttribute("variantes", varianteRepository.findAll());
        return "crear"; 
    }

    @PostMapping("/gustavo/crear")
    public String crearMueble(@RequestParam String nombre, @RequestParam String tipo, @RequestParam double precioBase,
                              @RequestParam int stock, @RequestParam String material, @RequestParam TamanoMueble tamano,
                              @RequestParam(required = false) List<Long> variantesIds) {
        Mueble.MuebleBuilder builder = new Mueble.MuebleBuilder(nombre, precioBase, stock)
                .tipo(tipo).material(material).tamano(tamano).estado(EstadoMueble.ACTIVO);
        Mueble mueble = muebleService.crearMueble(builder);
        if (variantesIds != null) {
            List<Variante> vars = varianteRepository.findAllById(variantesIds);
            vars.forEach(mueble::addVarianteDisponible);
            muebleRepository.save(mueble);
        }
        return "redirect:/web/gustavo/inventario";
    }
    
    @GetMapping("/gustavo/variantes")
    public String vistaVariantes(Model model) {
        model.addAttribute("perfil", "gustavo");
        model.addAttribute("active", "variantes");
        model.addAttribute("variantes", varianteRepository.findAll());
        return "variante"; 
    }

    @PostMapping("/gustavo/variantes")
    public String crearVariante(@RequestParam String nombre, @RequestParam double aumentoPrecio) {
        varianteRepository.save(new Variante(nombre, aumentoPrecio));
        return "redirect:/web/gustavo/variantes";
    }

    @GetMapping("/cliente/catalogo")
    public String vistaCatalogo(Model model) {
        model.addAttribute("perfil", "cliente");
        model.addAttribute("active", "catalogo");
        model.addAttribute("muebles", muebleService.listarTodos());
        return "catalogo"; 
    }
}