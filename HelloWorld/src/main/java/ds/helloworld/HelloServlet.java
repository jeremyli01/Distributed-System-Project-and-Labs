package ds.helloworld;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "The SHA256 Hash of Hello World is ";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        try {
            // Access MessageDigest class for SHA-265
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Compte the digest
            int nonce = 0;
            boolean flag = true;
            while (flag) {
                String rest = ",4,19,Pink,Orange,002fdb16086d97e03613fa0caa87b280eca956216e61a35400408bdd3a449e45";
                String s = nonce + rest;
                md.update(s.getBytes());
                String hash = bytesToHex(md.digest());
                if (hash.startsWith("0000")) {
                    // echo to console
                    System.out.println(hash);
                    // get a print writer from the the response object
                    PrintWriter out = response.getWriter();
                    // send an html document to caller
                    out.println("<html><body>");
                    // compute digest, convert to hex, send back to caller
                    out.println("<h1>" + message + hash + "</h1>");
                    out.println("</body></html>");
                    flag = false;
                    System.out.println(nonce);
                } else {
                    nonce++;
                }
            }
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println("No SHA-256 available" + e);
        }
    }

    public void destroy() {
    }
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}