/**
 * Blockchain server for Project3Task1
 * @author Jeremy(Zihan) Li
 * @andrewEmail zihanli2@andrew.cmu.edu
 * */
package org.example;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class BlockChainServerTCP {
    ServerSocket serverSocket;
    Socket clientSocket;
    BlockChain blockChain;
    Gson gson;

    public static void main(String[] args) {
        BlockChainServerTCP server = new BlockChainServerTCP();
        server.init();
        server.start();
    }
    /**
     * Initializes the Socket, Gson, and blockChain object
     * */
    private void init() {
        System.out.print("Blockchain server running. \n");
        try {
            int serverPort = 7777; // listen on port 7777 by default
            serverSocket = new ServerSocket(serverPort);
            gson = new Gson();
            initializeBlockChain();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Initializes the blockList, and add one genesis block
     * */
    private void initializeBlockChain() throws NoSuchAlgorithmException {
        blockChain = new BlockChain();
        blockChain.computeHashesPerSecond();
        Block genesis = new Block(0, blockChain.getTime(), "Genesis", 2);
        blockChain.addBlock(genesis);
        blockChain.repairChain();
    }
    /**
     * Starts the server on port to handle request from client and send response
     * */
    private void start() {
        try {
            while (true) {
                clientSocket = serverSocket.accept(); // get request from server socket
                System.out.println("We have a visitor");
                Scanner in = new Scanner(clientSocket.getInputStream());
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                while (in.hasNextLine()) {
                    RequestMessage requestMessage = gson.fromJson(in.nextLine(), RequestMessage.class); // parse request into JSON
                    ResponseMessage responseMessage = handleRequest(requestMessage);
                    out.println(gson.toJson(responseMessage));
                    out.flush();
                }
            }
        } catch (SocketException | NoSuchAlgorithmException e) {
            // handle socket exception
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            // handle IO exception
            System.out.println("IO: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) clientSocket.close(); // close
                if (serverSocket != null) serverSocket.close(); // close
            } catch (IOException e) {
                throw  new RuntimeException(e);
            }
        }
    }
    /**
     * Handles client request based on the choice same as task0
     * */
    private ResponseMessage handleRequest(RequestMessage requestMessage) throws NoSuchAlgorithmException {
        int choice = requestMessage.getChoice();
        String response = switch (choice) {
            case 0 ->
                    displayStatus();
            case 1 ->
                    addTransaction(requestMessage);
            case 2 ->
                verify();
            case 3 ->
                view();
            case 4 ->
                corrupt(requestMessage);
            case 5 ->
                repair();
            default ->
                throw new IllegalStateException("Unexpected value: " + choice);
        };
        ResponseMessage responseMessage = new ResponseMessage();
        if (choice != 0) {
            // print the current response to match the output in sample
            System.out.printf("Setting response to: %s\n", response);
        }
        if (choice == 1) {
            // for add block, print the specific line to match the output in sample
            System.out.printf("...{\"selection\":%d,\"response\":\"%s\"}%n", choice, response);
        }
        responseMessage.setResponse(response);
        return responseMessage;
    }

    /**
     * Displays the current status variable in the blockChain
     * */
    private String displayStatus() {
        System.out.printf("Response : {\"selection\":0,\"size\":%d,\"chainHash\":\"%s\",\"totalHashes\":%f," +
                        "\"totalDiff\":%d,\"recentNonce\":%d,\"diff\":%d,\"hps\":%d}\n", blockChain.getChainSize(),
                blockChain.getChainHash(), blockChain.getTotalExpectedHashes(), blockChain.getDifficulty(),
                blockChain.getLatestBlock().getNonce(), blockChain.getLatestBlock().getDifficulty(),
                blockChain.getHashPerSecond());

        return String.format("""
                Current size of chain: %d
                Difficulty of most recent block: %d
                Total difficulty for all blocks: %d
                Approximate hashes per second on this machine: %d
                Expected total hashes required for the whole chain: %f
                Nonce for most recent block: %s
                Chain hash: %s
                """,
                blockChain.getChainSize(), blockChain.getLatestBlock().getDifficulty(),blockChain.getDifficulty(),
                blockChain.getHashPerSecond(), blockChain.getTotalExpectedHashes(), blockChain.getLatestBlock().getNonce(),
                blockChain.getChainHash());
    }
    /**
     * Adds the block created by the user to the current blockChain
     * */
    private String addTransaction(RequestMessage requestMessage) throws NoSuchAlgorithmException {
        System.out.println("Adding a block");
        int difficulty = requestMessage.getNumberRequest();
        String data = requestMessage.getStringRequest();
        long startTime = System.currentTimeMillis();
        Block newBlock = new Block(blockChain.getChainSize(), blockChain.getTime(), data, difficulty);
        newBlock.setPreviousHash(blockChain.getChainHash());
        blockChain.addBlock(newBlock);
        blockChain.chainHash = newBlock.proofOfWork();
        long endTime = System.currentTimeMillis();
        return String.format("Total execution time to add this block was %d milliseconds", endTime - startTime);
    }
    /**
     * Verifies if the blockChain is valid and keeps track of the time spent
     * */
    private String verify() throws NoSuchAlgorithmException {
        System.out.println("Verifying entire chain");
        long startTime = System.currentTimeMillis();
        System.out.printf("Chain verification: %s %n", blockChain.isChainValid());
        long endTime = System.currentTimeMillis();
        System.out.printf("Total execution time to verify the chain was %d milliseconds\n", endTime - startTime);
        return String.format("Total execution time to verify the chain was %d milliseconds",
                endTime - startTime);
    }
    /**
     * Prints out the JSON format of the blockChain
     * */
    private String view() {
        System.out.println("View the Blockchain");
        return blockChain.toString();
    }
    /**
     * Corrupts the blockChain by editing the block at position i determined by the user
     * */
    private String corrupt(RequestMessage requestMessage) {
        System.out.println("Corrupt the Blockchain");
        int index = requestMessage.getNumberRequest();
        blockChain.getBlock(index).setData(requestMessage.getStringRequest());
        String result = String.format("org.example.Block %d now holds %s", index, blockChain.getBlock(index).getData());
        System.out.println(result);
        return result;
    }
    /**
     * Repairs the blockChain and keeps track of time
     * */
    private String repair() throws NoSuchAlgorithmException {
        System.out.println("Repairing the entire chain");
        long startTime = System.currentTimeMillis();
        blockChain.repairChain();
        long endTime = System.currentTimeMillis();
        return  String.format("Total execution time required to repair the chain was %d milliseconds", endTime - startTime);
    }
}
