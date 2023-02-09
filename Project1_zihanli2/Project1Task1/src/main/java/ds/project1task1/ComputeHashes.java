package ds.project1task1;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

@WebServlet(name = "ComputeHashes", urlPatterns = {"/computeHashes"})
public class ComputeHashes extends HttpServlet {
    private String message;

    public void init() {
        message = "Please enter your text input";
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        message = request.getParameter("input"); // retrieve the input from user
        String choice = request.getParameter("choice"); // retrieve the choice
        try {
            MessageDigest md = MessageDigest.getInstance(choice); // select compute method, either MD5 or SHA-256
            md.update(message.getBytes());
            String hexHash = printHexBinary(md.digest()); // compute the hashed value in as hex code
            String base64Hash = printBase64Binary(md.digest()); // compute the hashed value in as Base 64 binary code
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>" + "The encrypted value of your message" + message + " is: " +"</h1>");
            out.println("<h1>" + "Hash method: " + choice + "</h1>");
            out.println("Hexadecimal hash value: " + hexHash + "</br>");
            out.println("Base64 hash value: " + base64Hash);
            out.println("</body></html>");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such algorithm" + e);
        }

    }
    public void destroy() {
    }

}