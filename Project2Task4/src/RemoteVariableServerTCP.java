/**
 * Remote variable server for Project2Task4
 * @author Zihan Li
 * @AndrewID zihanli2
 *
 * */

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class RemoteVariableServerTCP {
    static Map<Integer, Integer> sumMap = new TreeMap<>();
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
                int choice;
                while (in.hasNextLine()) {
                    // get the received parameters and jump to different blocks depending on the choice
                    String responseString = in.nextLine();
                    String[] responseArray = responseString.split(" ");
                    choice = Integer.parseInt(responseArray[0]);
                    System.out.println("choice is" + choice);
                    if (choice == 4) {
                        // send the quit request back
                        out.println(choice);
                        System.out.println("quit request received.");
                        out.flush();
                    }
                    if (choice == 3) {
                        // get the sum of the specific id
                        int id = Integer.parseInt(responseArray[1]);
                        int responseNum = sumMap.getOrDefault(id, 0);
                        out.println(responseNum);
                        System.out.println("visitor's ID: " + id);
                        System.out.println("operation requested: get");
                        System.out.println("Returned value: " + responseNum);
                        out.flush();
                    }
                    if (choice == 1 || choice == 2) {
                        // convert the request data from byte array to integer value, in this case the operation, id and value
                        int id = Integer.parseInt(responseArray[1]);
                        int value = Integer.parseInt(responseArray[2]);
                        if (choice == 1) {
                            // add value
                            sumMap.put(id, sumMap.getOrDefault(id, 0) + value);
                        } else {
                            // subtract value
                            sumMap.put(id, sumMap.getOrDefault(id, 0) - value);
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
        } catch (SocketException e) {
            // handle socket exception
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            // handle IO exception
            System.out.println("IO: " + e.getMessage());
        } finally {
            try {
                if (aSocket != null) aSocket.close(); // close
                if (serverSocket != null) serverSocket.close(); // close
            } catch (IOException e) {
                throw  new RuntimeException(e);
            }
        }
    }
}
