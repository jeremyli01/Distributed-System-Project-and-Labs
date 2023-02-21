/**
 *
 * UDP Client for Project2Task1
 *
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;

public class EchoClientUDP{
    public static void main(String args[]) {
        System.out.println("The UDP client is running");
        // args give message contents and server hostname
        DatagramSocket aSocket = null;
        try {
            // define the host name
            InetAddress aHost = InetAddress.getByName("localhost");
            // ask user to enter a port number, for now it is 6789
            Scanner input = new Scanner(System.in);
            System.out.println("Enter a port number");
            int serverPort = Integer.parseInt(input.nextLine());
            // create the socket
            aSocket = new DatagramSocket();
            String nextLine;
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            // read the input data line by line in a while-loop
            while ((nextLine = typed.readLine()) != null) {
                // convert the text to a byte array
                byte[] m = nextLine.getBytes();
                // request pocket for sending text to the server
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
                // connect and write data to the socket
                aSocket.send(request);
                // receive reply from the server
                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);
                byte[] responseBytes = Arrays.copyOf(reply.getData(), reply.getLength());
                String responseString = new String(responseBytes);
                // convert reply to a string and print in the console
                System.out.println("Reply from server: " + new String(responseBytes));
                if (responseString.equalsIgnoreCase("halt!")) {
                    System.out.println("UDP client side quitting");
                    // jump to the final block
                    return;
                }
            }

        } catch (SocketException e) {
            // handle socket exception
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            // handle IO exception
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close(); // close}
        }
    }
}