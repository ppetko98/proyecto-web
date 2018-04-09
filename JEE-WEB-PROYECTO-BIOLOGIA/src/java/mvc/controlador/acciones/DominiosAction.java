/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc.controlador.acciones;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import mvc.modelo.dao.impl.DominioControllerImpl;
import mvc.modelo.dao.impl.FiloControllerImpl;
import mvc.modelo.entidades.Dominio;
import mvc.modelo.entidades.Filo;
import mvc.modelo.entidades.Nomenclatura;

/**
 *
 * @author Tamara
 */
public class DominiosAction {
    
    public String execute(HttpServletRequest request) {
        
        // Recuperar parametro de peticion pelicula
        int id_filo = Integer.parseInt(request.getParameter("filo"));
        
        // Recuperar los filos con este id
        FiloControllerImpl controller = new FiloControllerImpl();
        
        Filo f  = controller.findFilo(id_filo);
        
        Collection<Nomenclatura> dominiosFilo = f.getNomenclaturaCollection();
        
        List<Dominio> dominiosdelFilo = new ArrayList<>();
        
        for(Nomenclatura n : dominiosFilo) {
            dominiosdelFilo.add(n.getDominio());
        }
        
        request.setAttribute("filo", f.getFiloName());
        request.setAttribute("dominios", dominiosdelFilo);
        
        return "/nomenclatura.jsp";
    }
    
}
