/**
 * UDP Server for Project2Task2
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

public class AddingServerUDP {
    static int sum;
    public static void main(String args[]) {
        System.out.println("Server started");
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
                // cut down the size of request bytes
                byte[] requestBytes = Arrays.copyOf(request.getData(), request.getLength());
                // convert the request data from byte array to string
                String requestString = new String(requestBytes);
                // quit the socket if received halt message from the client
                if (requestString.contains("halt!")) {
                    // print the received message to the console
                    System.out.println("Echoing: " + requestString);
                    // create a new socket object for sending reply to the client
                    DatagramPacket reply = new DatagramPacket(request.getData(),
                            request.getLength(), request.getAddress(), request.getPort());
                    // write data to the socket by sending reply to the client
                    aSocket.send(reply);
                    System.out.println("UDP server side quitting");
                    // jump to the final block
                    return;
                }
                // convert the bytes to int
                int requestNum = convertByteArrayToInt(requestBytes);
                addAction(requestNum);
                // create a new socket object for sending reply to the client
                DatagramPacket reply = new DatagramPacket(intToBytes(sum),
                        intToBytes(sum).length, request.getAddress(), request.getPort());
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
    /**
     * Add methods which separates add number from packets communication.
     * */
    public static void addAction(int num) {
        // print the received message to the console
        System.out.printf("Adding %d to %d\n", num, sum);
        sum += num;
        System.out.printf("Returning sum of %d to client\n\n", sum);
        // write data to the socket by sending reply to the client
    }
    /**
     * Method for converting int number into byte array.
     * source:https://javadeveloperzone.com/java-basic/java-convert-int-to-byte-array/
     * */
    public static byte[] intToBytes(int data) {
        return new byte[] {
                (byte)((data >> 24) & 0xff),
                (byte)((data >> 16) & 0xff),
                (byte)((data >> 8) & 0xff),
                (byte)((data >> 0) & 0xff),
        };
    }
    /**
     * Method for converting byte array into int.
     * source:https://javadeveloperzone.com/java-basic/java-convert-int-to-byte-array/
     * */
    private static int convertByteArrayToInt(byte[] intBytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(intBytes);
        return byteBuffer.getInt();
    }
}
