/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc.controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mvc.modelo.dao.NomenclaturaJpaController;
import mvc.modelo.entidades.Nomenclatura;

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
        
        NomenclaturaJpaController controller= new NomenclaturaJpaController();
        List<Nomenclatura> clasificaciones = controller.findNomenclaturaEntities();
        
        ServletContext app = config.getServletContext();
        
         app.setAttribute("taxonomy", clasificaciones);
        
        
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
                    ActoresAction action = new ActoresAction();
                    vista = action.execute(req);
                    break;
                case "pel":
                    PeliculasAction action2 = new PeliculasAction();
                    vista = action2.execute(req);
                    break;
            }
    }
    
    

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
