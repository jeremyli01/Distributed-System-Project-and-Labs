/**
 * Signing client for Project2Task5
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class SigningClientTCP {
    static Socket aSocket;
    static int serverPort;

    static BufferedReader in;

    static PrintWriter out;
    static BigInteger publicKey;
    static BigInteger privateKey;
    static String id;
    static BigInteger n; // n is the modulus for both the private and public keys
    static BigInteger e; // e is the exponent of the public key
    static BigInteger d; // d is the exponent of the private key

    static int DUMMY = 0; // dummy number if the choice is not add or subtract

    public static void main(String args[]) {
        System.out.println("The TCP client is running");
        // args give message contents and server hostname
        try {
            // ask user to enter a port number, for now it is 6789
            Scanner input = new Scanner(System.in);
            System.out.println("Enter a port number");
            serverPort = Integer.parseInt(input.nextLine());
            // create the socket
            aSocket = new Socket("localhost", serverPort);
            in = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(aSocket.getOutputStream())));
            // read the input data line by line in a while-loop
            generateRSAKeys();
            generateID();
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
                        operateAction(numToAdd, choice);
                        continue;
                    }
                    case 2: {
                        System.out.println("Enter value to subtract:");
                        int numToSubtract = Integer.parseInt(input.nextLine());
                        operateAction(numToSubtract, choice);
                        continue;
                    }
                    case 3: {
                        operateAction(DUMMY, choice);
                        continue;
                    }
                    case 4: {
                        operateAction(DUMMY, choice);
                        return;
                    }
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (aSocket != null) aSocket.close(); // close
            } catch (IOException e) {
                throw  new RuntimeException(e);
            }
        }
    }
    /**
     * Add and subtract methods which encapsulates packets communication, and operate addition or subtraction
     * based on specific id.
     * */
    public static int operateAction(int i, int choice) {
        try {
            StringBuilder sb = new StringBuilder();
            // append all parameters to be sent
            sb.append(choice).append(" ").append(i).append(" ").append(id).append(" ").append(e).append(" ").append(n);
            // string value to be hashed
            String messageToSign = sb.toString();
            // compute the hash value to be signed
            String hash = computeHash(messageToSign);
            sb.append(" ").append(sign(hash));
            // append the hash value and send to the server
            String sendString = sb.toString();
            out.println(sendString);
            out.flush();
            int responseNum = Integer.parseInt(in.readLine());
            if (responseNum == 4) {
                System.out.println("Client side quitting. The remote variable server is still running.");
            } else {
                System.out.println("The result is " + responseNum);
            }
        } catch (SocketException e) {
            // handle socket exception
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            // handle IO exception
            System.out.println("IO Exception: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    /**
     * Generates the private and public RSA keys. Part of the code is from RSAExample.java
     * */
    public static void generateRSAKeys() {
        Random rnd = new Random();
        // Generate two large random primes.
        BigInteger p = new BigInteger(2048, 100, rnd);
        BigInteger q = new BigInteger(2048, 100, rnd);
        n = p.multiply(q); // Modulus for both keys
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        // By convention the prime 65537 is used as the public exponent.
        e = new BigInteger("65537");
        d = e.modInverse(phi);
        publicKey = new BigInteger(e.toString() + n.toString()); // (e,n) is the RSA public key
        privateKey = new BigInteger(d.toString() + n.toString()); // (d,n) is the RSA private key
        System.out.println("public key is: " + publicKey);
        System.out.println("private key is: " + privateKey);
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
     * Generates the client ID from the public key.
     * */
    public static void generateID() throws NoSuchAlgorithmException {
        String hash = computeHash(publicKey.toString());
        // get the last 20 bytes
        id = hash.substring(hash.length() - 20);
    }
    /**
     * Signs the message to be sent to the server, part of the code is from ShortMessageSign.java.
     * */
    public static String sign(String message) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // compute the digest with SHA-256
        byte[] bytesOfMessage = message.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);
        byte[] messageDigest = new byte[bigDigest.length + 1];
        // most significant set to 0
        messageDigest[0] = 0;
        System.arraycopy(bigDigest, 0, messageDigest, 1, bigDigest.length);
        // from the digest, create a new BigInteger
        BigInteger m = new BigInteger(messageDigest);
        // encrypt the digest with the private key
        BigInteger c = m.modPow(d, n);
        // return this as a big integer string
        return c.toString();
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
}