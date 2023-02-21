/**
 *
 * UDP Client for Project2Task2
 *
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

public class AddingClientUDP {
    static DatagramSocket aSocket;
    static InetAddress aHost;
    static int serverPort;

    public static void main(String args[]) {
        System.out.println("The UDP client is running");
        // args give message contents and server hostname
        try {
            // define the host name
            aHost = InetAddress.getByName("localhost");
            // ask user to enter a port number, for now it is 6789
            Scanner input = new Scanner(System.in);
            System.out.println("Enter a port number");
            serverPort = Integer.parseInt(input.nextLine());
            // create the socket
            aSocket = new DatagramSocket();
            String nextLine;
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            // read the input data line by line in a while-loop
            while ((nextLine = typed.readLine()) != null) {
                if (!nextLine.equalsIgnoreCase("halt!")) {
                    int numToAdd = Integer.parseInt(nextLine);
                    add(numToAdd);
                } else {
                    handleHalt(nextLine);
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (aSocket != null) aSocket.close(); // close}
        }
    }

    public static int add(int i) {
        try {
            // convert the text to a byte array
            byte[] m = intToBytes(i);
            // request pocket for sending text to the server
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
            // connect and write data to the socket
            aSocket.send(request);
            // receive reply from the server
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            byte[] responseBytes = Arrays.copyOf(reply.getData(), reply.getLength());
            int responseNum = convertByteArrayToInt(responseBytes);
            System.out.println("The server returned " + responseNum);
        } catch (SocketException e) {
            // handle socket exception
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            // handle IO exception
            System.out.println("IO Exception: " + e.getMessage());
        }
        return 0;
    }
    public static void handleHalt(String line) throws IOException {
        byte[] m = line.getBytes();
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
        if (responseString.equalsIgnoreCase("halt!")) {
            // convert reply to a string and print in the console
            System.out.println("The server returned " + responseString);
            System.out.println("UDP client side quitting");
        }
    }
    // source:https://javadeveloperzone.com/java-basic/java-convert-int-to-byte-array/
    public static byte[] intToBytes(int data) {
        return new byte[] {
                (byte)((data >> 24) & 0xff),
                (byte)((data >> 16) & 0xff),
                (byte)((data >> 8) & 0xff),
                (byte)((data >> 0) & 0xff),
        };
    }

    private static int convertByteArrayToInt(byte[] intBytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(intBytes);
        return byteBuffer.getInt();
    }
}