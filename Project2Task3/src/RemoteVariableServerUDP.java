/**
 * Remote variable server for Project2Task3
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;

public class RemoteVariableServerUDP {
    static Map<Integer, Integer> sumMap = new TreeMap<>();
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
                // convert the request data from byte array to integer value, in this case the quit option
                if (requestBytes.length == 4) {
                    int requestNum = convertByteArrayToInt(requestBytes);
                    DatagramPacket reply = new DatagramPacket(intToBytes(requestNum),
                            intToBytes(requestNum).length, request.getAddress(), request.getPort());
                    aSocket.send(reply);
                }
                // convert the request data from byte array to integer value, in this case the get option and id
                if (requestBytes.length == 8) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(requestBytes);
                    int id = byteBuffer.getInt();
                    int responseNum = sumMap.getOrDefault(id, 0);
                    DatagramPacket reply = new DatagramPacket(intToBytes(responseNum),
                            intToBytes(responseNum).length, request.getAddress(), request.getPort());
                    aSocket.send(reply);
                }
                // convert the request data from byte array to integer value, in this case the operation, id and value
                if (requestBytes.length == 12) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(requestBytes);
                    int value = byteBuffer.getInt();
                    int id = byteBuffer.getInt();
                    int choice = byteBuffer.getInt();
                    if (choice == 1) {
                        // add value
                        sumMap.put(id, sumMap.getOrDefault(id, 0) + value);
                    } else {
                        // subtract value
                        sumMap.put(id, sumMap.getOrDefault(id, 0) - value);
                    }
                    int responseNum = sumMap.getOrDefault(id, 0);
                    DatagramPacket reply = new DatagramPacket(intToBytes(responseNum),
                            intToBytes(responseNum).length, request.getAddress(), request.getPort());
                    aSocket.send(reply);
                }
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
