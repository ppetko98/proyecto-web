/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc.controlador;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mvc.controlador.acciones.DominiosAction;
import mvc.controlador.acciones.FilosAction;
import mvc.modelo.dao.impl.DominioControllerImpl;
import mvc.modelo.entidades.Dominio;


/**
 *
 * @author Tamara
 */
@WebServlet(name = "ActionServlet", urlPatterns = {"/Biologia"},
        loadOnStartup = 1)
public class ActionServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        DominioControllerImpl controller= new DominioControllerImpl();
        List<Dominio> dominios = controller.findDominioEntities();
        
        ServletContext app = config.getServletContext();
        
         app.setAttribute("dominio", dominios);
        
        
    }

     
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Recuperar el parametro de peticion link
        String operacion = req.getParameter("link");
        
        switch (operacion) {
            case "add":
                System.out.println("\nOpcion ALTA seleccionada\n");
                break;
            case "del":
                System.out.println("\nOpcion BAJA seleccionada\n");
                break;
            case "edi":
                System.out.println("\nOpcion MODIFICAR seleccionada\n");
                break;
            case "det":
                System.out.println("\nOpcion DETALLES seleccionada\n");
                break;
            case "lis":
                System.out.println("\nOpcion LISTADO seleccionada\n");
                break;
        }
        
        //Recuperar el parametro de peticion opc
        
         String opcion = req.getParameter("opc");

        String vista = "";

        if (opcion == null) {
            // No existe parametro opc
            // Carga inicial de la aplicacion
            vista = "/inicial.jsp";
        } else {
            switch(opcion) {
                case "act":
                    DominiosAction action = new DominiosAction();
                    vista = action.execute(req);
                    break;
                case "pel":
                    FilosAction action2 = new FilosAction();
                    vista = action2.execute(req);
                    break;
            }
    }
    
    RequestDispatcher dispatcher = req.getRequestDispatcher(vista);

        dispatcher.forward(req, resp);
        
    }
}

    
   
 /* protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
           
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ActionServlet</title>");            
            out.println("</head>");
            out.println("<nav>");
            out.println("</nav>");
            out.println("<body>");
            out.println("<h1>Servlet ActionServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}*/
