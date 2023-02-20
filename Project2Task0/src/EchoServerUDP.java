/**
 *
 * UDP Server for Project2Task0
 *
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class EchoServerUDP{
    public static void main(String args[]) {
        System.out.println("The UDP server is running");
        // declare the socket object
        DatagramSocket aSocket = null;
        // instantiate the buffer array
        byte[] buffer = new byte[1000];
        try {
            // ask user to enter a port number, for now it is 6789
            Scanner input = new Scanner(System.in);
            System.out.println("Enter a port number");
            int serverPort = Integer.parseInt(input.nextLine());
            // create, bind the socket and listen to port 6789
            aSocket = new DatagramSocket(serverPort);
            // create the request object
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            while (true) {
                // receive and read the request
                aSocket.receive(request);
                // create a new socket object for sending reply to the client
                DatagramPacket reply = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());
                // cut down the size of request bytes
                byte[] requestBytes = Arrays.copyOf(request.getData(), request.getLength());
                // convert the request data from byte array to string
                String requestString = new String(requestBytes);
                // quit the socket if received halt message from the client
                if (requestString.contains("halt!")) {
                    // print the received message to the console
                    System.out.println("Echoing: " + requestString);
                    // write data to the socket by sending reply to the clinet
                    aSocket.send(reply);
                    System.out.println("UDP server side quitting");
                    // jump to the final block
                    return;
                }
                // print the received message to the console
                System.out.println("Echoing: " + requestString);
                // write data to the socket by sending reply to the clinet
                aSocket.send(reply);
            }
        } catch (SocketException e) {
            // handle socket exception
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            // handle IO exception
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close(); // close}
        }
    }
}
