import java.net.*;
import java.io.*;
import java.util.Scanner;

public class EchoServerTCP {

    public static void main(String args[]) {
        Socket clientSocket = null;
        try {
            int serverPort = 7777; // the server port we are using
            
            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);

            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            clientSocket = listenSocket.accept();
            // If we get here, then we are now connected to a client.

            // Set up "inFromSocket" to read from the client socket
            Scanner inFromSocket;
            inFromSocket = new Scanner(clientSocket.getInputStream());

            // Set up "outToSocket" to write to the client socket
            PrintWriter outToSocket;
            outToSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            /*
             * Forever,
             *   read a line from the socket
             *   print it to the console
             *   echo it (i.e. write it) back to the client
             */
            while (true) {
                String data = inFromSocket.nextLine();
                if (data.contains("GET")) {
                    outToSocket.println("HTTP/1.1 200 OK\n");
                    String[] dataArr = data.split(" ");
                    String fileName = dataArr[1];
                    System.out.println(fileName);
                    try {
                        Scanner scanner = new Scanner(new File("/Users/jeremyli/Documents/95-702 Distributed System/Lab4/src" + fileName));
                        while (scanner.hasNextLine()) {
                            outToSocket.println(scanner.nextLine());
//                            System.out.println(scanner.nextLine());
                        }
                        scanner.close();
                        outToSocket.flush();

                    } catch (Exception e) {
                        Scanner scanner = new Scanner(new File("/Users/jeremyli/Documents/95-702 Distributed System/Lab4/src/404.html"));
                        while (scanner.hasNextLine()) {
                            outToSocket.println(scanner.nextLine());
                        }
                        scanner.close();
                        outToSocket.flush();

                    }
                    break;
                }
                outToSocket.flush();
            }
//            outToSocket.println("404 Not Found");
//            outToSocket.flush();


        // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());

        // If quitting (typically by you sending quit signal) clean up sockets
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }
}
