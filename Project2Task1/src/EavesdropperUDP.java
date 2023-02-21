/**
 *
 * UDP Eavesdropper for Project2Task1
 *
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;

public class EavesdropperUDP {
    public static void main(String[] args) {
        DatagramSocket aSocket = null;
        DatagramSocket bSocket = null;
        byte[] buffer = new byte[1000];
        try {
            // enter the listen port and masquerade port
            Scanner input = new Scanner(System.in);
            System.out.println("Enter the port number you will listen to");
            int listenPort = Integer.parseInt(input.nextLine());
            System.out.println("Enter the port number you will masquerade");
            int serverPort = Integer.parseInt(input.nextLine());
            aSocket = new DatagramSocket(listenPort);
            bSocket = new DatagramSocket();
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            while(true) {
                // receive and read request from the client
                aSocket.receive(request);
                // cut down the size of request bytes
                byte[] requestBytes = Arrays.copyOf(request.getData(), request.getLength());
                // convert the request data from byte array to string
                String requestString = new String(requestBytes);
                // print in the console if received halt message from the client
                if (requestString.equals("halt!")) {
                    System.out.println("Received halt message from the client");
                }
                System.out.println("Captured from the client: " + requestString);
                // edit the message if the string is not halt or halt!
                if (!requestString.equals("halt") && !requestString.equals("halt!")) {
                    requestString += "!";
                }
                byte[] modifiedData = requestString.getBytes();
                // instead of sending back to the client, send the modified data to the server port
                DatagramPacket reply = new DatagramPacket(modifiedData,
                        modifiedData.length, InetAddress.getLocalHost(), serverPort);
                bSocket.send(reply);
                // receive the data back from the server port
                DatagramPacket serverResponse = new DatagramPacket(buffer, buffer.length);
                bSocket.receive(serverResponse);
                byte[] responseBytes = Arrays.copyOf(serverResponse.getData(), serverResponse.getLength());
                String responseString = new String(responseBytes);
                // remove the exclamation mark if the message is not halt!
                if (responseString.endsWith("!") && !responseString.equals("halt!")) {
                    responseString = responseString.substring(0, responseString.length() - 1);
                }
                System.out.println("Received back from the server: " + responseString);
                // send the data back to the client server
                byte[] clientResponseBytes = responseString.getBytes();
                DatagramPacket clientReply = new DatagramPacket(clientResponseBytes,
                        clientResponseBytes.length, request.getAddress(), request.getPort());
                aSocket.send(clientReply);
            }
        } catch (SocketException e) {
            // handle socket exception
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            // handle IO exception
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close(); // close
        }
    }
}
