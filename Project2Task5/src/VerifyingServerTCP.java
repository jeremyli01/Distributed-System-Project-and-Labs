/**
 * Verifying server for Project2Task5
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class VerifyingServerTCP {
    static Map<String, Integer> sumMap = new TreeMap<>();
    public static void main(String args[]) {
        System.out.println("Server started");
        Socket aSocket = null;
        ServerSocket serverSocket = null;
        try {
            // ask user to enter a port number, for now it is 6789
            Scanner input = new Scanner(System.in);
            System.out.println("Enter a port number");
            int serverPort = Integer.parseInt(input.nextLine());
            // create, bind the socket and listen to port 6789
            serverSocket = new ServerSocket(serverPort);
            while (true) {
                // receive and read the request
                aSocket = serverSocket.accept();
                Scanner in = new Scanner(aSocket.getInputStream());
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(aSocket.getOutputStream())));
                while (in.hasNextLine()) {
                    // get the received parameters and jump to different blocks depending on the choice
                    String responseString = in.nextLine();
                    String[] responseArray = responseString.split(" ");
                    // verify that the response comes from the same client
                    if (!verify(responseArray)) {
                        System.out.println("Error in request");
                    } else {
                        int choice = Integer.parseInt(responseArray[0]);
                        System.out.println("choice is " + choice);
                        String id = responseArray[2];
                        if (choice == 4) {
                            // send the quit request back
                            out.println(choice);
                            System.out.println("quit request received.");
                            out.flush();
                        }
                        if (choice == 3) {
                            // get the sum of the specific id
                            int responseNum = sumMap.getOrDefault(id, 0);
                            out.println(responseNum);
                            System.out.println("visitor's ID: " + id);
                            System.out.println("operation requested: get");
                            System.out.println("Returned value: " + responseNum);
                            out.flush();
                        }
                        if (choice == 1 || choice == 2) {
                            // convert the request data from byte array to integer value, in this case the operation, id and value
                            int value = Integer.parseInt(responseArray[1]);
                            if (choice == 1) {
                                sumMap.put(id, sumMap.getOrDefault(id, 0) + value); // add value
                            } else {
                                sumMap.put(id, sumMap.getOrDefault(id, 0) - value); // subtract value
                            }
                            int responseNum = sumMap.getOrDefault(id, 0);
                            out.println(responseNum);
                            String operand = choice == 1 ? "add" : "subtract";
                            System.out.println("visitor's ID: " + id);
                            System.out.println("operation requested: " + operand);
                            System.out.println("Returned value: " + responseNum);
                            out.flush();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            // handle socket exception
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            // handle IO exception
            System.out.println("IO: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (aSocket != null) aSocket.close(); // close
                if (serverSocket != null) serverSocket.close(); // close
            } catch (IOException e) {
                throw  new RuntimeException(e);
            }
        }
    }
    /**
     * Computes the hash value using SHA-256 for given message string.
     * */
    public static String computeHash(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] sourceBytes = message.getBytes();
        md.update(sourceBytes);
        String hash = bytesToHex(md.digest());
        return hash;
    }
    /**
     * Hexadecimal array.
     * */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    /**
     * Converts bytes into hexadecimal code.
     * */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    /**
     * Verifies that the ID server receives matches with the ID computes from the publicKey, and the encrypted hash value is sent by the
     * same ID and private key pair (d, n).
     * */
    public static boolean verify(String[] responseArray) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String id = responseArray[2];
        BigInteger e =  new BigInteger(responseArray[3]);
        BigInteger n = new BigInteger(responseArray[4]);
        String publicKey = e.toString() + n;
        String hash = computeHash(publicKey);
        boolean isIDMatch = hash.substring(hash.length() - 20).equals(id);
        // fetch the signed hash value
        BigInteger encryptedHash = new BigInteger(responseArray[5]);
        // decrypt
        BigInteger decryptedHash = encryptedHash.modPow(e, n);
        String messageToCheck;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < responseArray.length - 1; i++) {
            sb.append(responseArray[i]);
            if (i != responseArray.length - 2) {
                sb.append(" ");
            }
        }
        messageToCheck = computeHash(sb.toString());
        // Get the bytes from messageToCheck
        byte[] bytesOfMessageToCheck = messageToCheck.getBytes("UTF-8");
        // compute the digest of the message with SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageToCheckDigest = md.digest(bytesOfMessageToCheck);
        byte[] extraByte = new byte[messageToCheckDigest.length + 1];
        // most significant set to 0
        extraByte[0] = 0;
        System.arraycopy(messageToCheckDigest, 0, extraByte, 1, messageToCheckDigest.length);
        // from the digest, create a new BigInteger
        BigInteger bigIntegerToCheck = new BigInteger(extraByte);
        System.out.println(decryptedHash);
        System.out.println(bigIntegerToCheck);
        boolean isSignatureMatch = (decryptedHash.compareTo(bigIntegerToCheck) == 0);
        System.out.println(isIDMatch);
        System.out.println(isSignatureMatch);
        return isIDMatch && isSignatureMatch;
    }

}
