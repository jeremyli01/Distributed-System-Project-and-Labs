/**
 * Remote variable client for Project2Task4
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

public class RemoteVariableClientTCP {
    static Socket aSocket;
    static int serverPort;

    static BufferedReader in;

    static PrintWriter out;

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
//            outServer = new DataOutputStream(aSocket.getOutputStream());
//            inServer = new DataInputStream(aSocket.getInputStream());
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
    public static int addOrSubstract(int i, int id, int choice) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(choice).append(" ").append(id).append(" ").append(i);
            String sendString = sb.toString();
            out.println(sendString);
            out.flush();
            System.out.println("Success writing in");
            int responseNum = Integer.parseInt(in.readLine());
            System.out.println("Success receive back");
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
            StringBuilder sb = new StringBuilder();
            sb.append(choice).append(" ").append(id);
            String sendString = sb.toString();
            out.println(sendString);
            out.flush();
            int responseNum = Integer.parseInt(in.readLine());
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
        out.println(i);
        out.flush();
        int responseNum = Integer.parseInt(in.readLine());
        if (responseNum == 4) {
            System.out.println("Client side quitting. The remote variable server is still running.");
        }
    }
}