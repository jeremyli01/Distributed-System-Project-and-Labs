/**
 * Blockchain client for Project3Task1
 * @author Jeremy(Zihan) Li
 * @andrewEmail zihanli2@andrew.cmu.edu
 * */
package org.example;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class BlockChainClientTCP {
    private BufferedReader in;
    private PrintWriter out;
    private Socket clientSocket;
    private Gson gson;

    public static void main(String[] args) {
        BlockChainClientTCP client = new BlockChainClientTCP();
        client.init();
        client.start();
    }
    /**
     * Initializes the client socket to listen user request and send to the server
     * */
    private void init() {
        System.out.println("Blockchain client is running");
        gson = new Gson();
        int serverPort = 7777;
        try {
            clientSocket = new Socket("localhost", serverPort);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Starts the client server, which asks for user input for different choices and send to server in JSON file
     * */
    private void start(){
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                displayMenu();
                int choice = Integer.parseInt(input.readLine()); // stores the choice
                RequestMessage requestMessage = new RequestMessage();
                requestMessage.setChoice(choice);
                switch (choice) {
                    case 1:
                        System.out.println("Enter difficulty > 0");
                        // stores difficulty
                        int difficulty = Integer.parseInt(input.readLine());
                        System.out.println("Enter transaction");
                        // stores transaction information
                        String data = input.readLine();
                        requestMessage.setNumberRequest(difficulty);
                        requestMessage.setStringRequest(data);
                        break;
                    case 4:
                        System.out.println("Corrupt the Blockchain");
                        System.out.println("Enter block ID of block to corrupt");
                        // stores block index
                        int index = Integer.parseInt(input.readLine());
                        System.out.printf("Enter new data for block %d%n", index);
                        // stores data to override
                        data = input.readLine();
                        requestMessage.setNumberRequest(index);
                        requestMessage.setStringRequest(data);
                        break;
                    case 6:
                        return;
                }
                out.println(gson.toJson(requestMessage));
                out.flush();
                ResponseMessage responseMessage = gson.fromJson(in.readLine(), ResponseMessage.class);
                System.out.println(responseMessage.getResponse());
            }
        } catch (NumberFormatException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private void displayMenu() {
        System.out.println("""
                Block Chain Menu
                0. View basic blockchain status.
                1. Add a transaction to the blockchain.
                2. Verify the blockchain.
                3. View the blockchain.
                4. Corrupt the chain.
                5. Hide the corruption by repairing the chain.
                6. Exit""");
    }
}
