/**
 * Remote variable client for Project2Task3
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

public class RemoteVariableClientUDP {
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
            // read the input data line by line in a while-loop
            while (true) {
                System.out.println("1. Add a value to your sum.");
                System.out.println("2. Subtract a value from your sum.");
                System.out.println("3. Get your sum.");
                System.out.println("4. Exit client");
                int choice = Integer.parseInt(input.nextLine());
                switch (choice){
                    case 1: {
                        System.out.println("Enter value to add:");
                        int numToAdd = Integer.parseInt(input.nextLine());
                        System.out.println("Enter your ID:");
                        int id = Integer.parseInt(input.nextLine());
                        addOrSubstract(numToAdd, id, choice);
                        continue;
                    }
                    case 2: {
                        System.out.println("Enter value to subtract:");
                        int numToSubtract = Integer.parseInt(input.nextLine());
                        System.out.println("Enter your ID:");
                        int id = Integer.parseInt(input.nextLine());
                        addOrSubstract(numToSubtract, id, choice);
                        continue;
                    }
                    case 3: {
                        System.out.println("Enter your ID:");
                        int id = Integer.parseInt(input.nextLine());
                        getSum(id, choice);
                        continue;
                    }
                    case 4: {
                        handleHalt(choice);
                        return;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (aSocket != null) aSocket.close(); // close}
        }
    }
    /**
     * Add and subtract methods which encapsulates packets communication, and operate addition or subtraction
     * based on specific id.
     * */
    public static int addOrSubstract(int i, int id, int choice) {
        try {
            // convert the text to a byte array
            byte[] m = new byte[12];
            ByteBuffer.wrap(m).putInt(i).putInt(id).putInt(choice);
            // request pocket for sending text to the server
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
            // connect and write data to the socket
            aSocket.send(request);
            // receive reply from the server
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            // cut down the size of return bytes
            byte[] responseBytes = Arrays.copyOf(reply.getData(), reply.getLength());
            // convert the returned bytes to int
            int responseNum = convertByteArrayToInt(responseBytes);
            System.out.println("The result is " + responseNum);
        } catch (SocketException e) {
            // handle socket exception
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            // handle IO exception
            System.out.println("IO Exception: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get method which sends get request to the server and gets back the sum of specific id.
     * */
    public static int getSum(int id, int choice) {
        try {
            // convert the text to a byte array
            byte[] m = new byte[8];
            ByteBuffer.wrap(m).putInt(id).putInt(choice);
            // request pocket for sending text to the server
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
            // connect and write data to the socket
            aSocket.send(request);
            // receive reply from the server
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            // cut down the size of return bytes
            byte[] responseBytes = Arrays.copyOf(reply.getData(), reply.getLength());
            // convert the returned bytes to int
            int responseNum = convertByteArrayToInt(responseBytes);
            System.out.println("The result is " + responseNum);
        } catch (SocketException e) {
            // handle socket exception
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            // handle IO exception
            System.out.println("IO Exception: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Halt method which only sends halt request to the server.
     * */
    public static void handleHalt(int i) throws IOException {
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
        // convert the returned bytes to string
        int responseNum = convertByteArrayToInt(responseBytes);
        if (responseNum == 4) {
            System.out.println("Client side quitting. The remote variable server is still running.");
        }
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