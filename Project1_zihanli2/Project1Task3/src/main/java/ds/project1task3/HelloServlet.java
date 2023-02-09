package ds.project1task3;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", urlPatterns ={"/submit", "/getResults"})
public class HelloServlet extends HttpServlet {

    /** Tracks what the current user submits*/
    public static Map<String, Integer> choiceMap = new HashMap<>();

    /** Stores the lastChoice the user submits*/
    public static String lastChoice = "";

    /** Initiates the map with four choices */
    public void init() {
        choiceMap.put("A", 0);
        choiceMap.put("B", 0);
        choiceMap.put("C", 0);
        choiceMap.put("D", 0);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        String ua = request.getHeader("User-Agent");
        // prepare the appropriate DOCTYPE for the view pages
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }
        // directs to the submit page
        if (!request.getServletPath().equals("/getResults")) {
            lastChoice = request.getParameter("choice");
            choiceMap.put(lastChoice, choiceMap.get(lastChoice) + 1);
            RequestDispatcher view = request.getRequestDispatcher("index.jsp");
            view.forward(request, response);
        } else { // directs to the result page
            RequestDispatcher view = request.getRequestDispatcher("result.jsp");
            view.forward(request, response);
            init(); // clear the collected results
        }
    }

    public void destroy() {
    }
}