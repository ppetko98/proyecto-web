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
import mvc.modelo.entidades.Dominio;
import mvc.modelo.entidades.Filo;
import mvc.modelo.entidades.Nomenclatura;

/**
 *
 * @author Tamara
 */
public class FilosAction {
    
    public String execute(HttpServletRequest request) {
        
        // Recuperar parametro de peticion pelicula
        int idDominio = Integer.parseInt(request.getParameter("dominio"));
        
        // Recuperar la pelicula de ese codigo
        DominioControllerImpl controller = new DominioControllerImpl();
        
        Dominio d = controller.findDominio(idDominio);
        
        Collection<Nomenclatura> dominioFilo= d.getNomenclaturaCollection();
        
        List<Filo> filosDelDominio= new ArrayList<>();
        
        for(Nomenclatura n : dominioFilo) {
            filosDelDominio.add(n.getFilo());
        }
        
        request.setAttribute("filos", filosDelDominio);
        request.setAttribute("descripcion", d.getDominioName());
        
        return "/filos.jsp";
    }
    
}
