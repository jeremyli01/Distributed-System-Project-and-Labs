 /**
 * org.example.Block class for Project3Task1
 * @author Jeremy(Zihan) Li
 * @andrewEmail zihanli2@andrew.cmu.edu
 * */
 package org.example;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
/**
 * Implementation of the org.example.Block class from the org.example.Block JavaDoc.
 * */
public class Block {
    // the position of the block on the chain, starts at 0
    int index;
    // minimum number of left most hex digits needed by a proper hash
    int difficulty;
    // a String holding the block's single transaction details
    String data;
    // a Java Timestamp object holding the time of the block's creation
    Timestamp timestamp;
    // the SHA256 hash of a block's parent
    String previousHash;
    // a BigInteger value determined by a proof of work routine
    BigInteger nonce;

    /**
     * Constructor with parameters index, timestamp, data and difficulty
     * */
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.difficulty = difficulty;
        this.data = data;
        this.timestamp = timestamp;
        this.nonce = BigInteger.ZERO;
        this.previousHash = "";
    }
    /**
     * Getter method for variable index
     * */
    public int getIndex() {
        return index;
    }
    /**
     * Setter method for variable index
     * */
    public void setIndex(int index) {
        this.index = index;
    }
    /**
     * Getter method for variable difficulty
     * */
    public int getDifficulty() {
        return difficulty;
    }
    /**
     * Setter method for variable difficulty
     * */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    /**
     * Getter method for variable data
     * */
    public String getData() {
        return data;
    }
    /**
     * Setter method for variable data
     * */
    public void setData(String data) {
        this.data = data;
    }
    /**
     * Getter method for variable timestamp
     * */
    public Timestamp getTimestamp() {
        return timestamp;
    }
    /**
     * Setter method for variable timestamp
     * */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    /**
     * Computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty
     * */
    public String calculateHash() throws NoSuchAlgorithmException {
        String valueToHash = String.valueOf(index) + timestamp + data + previousHash + nonce + difficulty;
        return computeHash(valueToHash);
    }
    /**
     * Getter method for variable nonce
     * */
    public BigInteger getNonce() {
        return nonce;
    }
    /**
     * Getter method for variable previousHash
     * */
    public String getPreviousHash() {
        return previousHash;
    }
    /**
     * Setter method for variable nonce
     * */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    /**
     * Increments the nonce until it finds a hash that meets the lowest requirement of the specified difficulty
     * */
    public String proofOfWork() throws NoSuchAlgorithmException {
        String hash = calculateHash();
        while (!hash.startsWith("0".repeat(difficulty))) {
            nonce = nonce.add(BigInteger.ONE);
            hash = calculateHash();
        }
        return hash;
    }

    @Override
    public String toString() {
        return String.format("{\"index\" : %d, \"time stamp\" : \"%s\", \"Tx \" :\"%s\", \"PrevHash\" : \"%s\", " +
                        "\"nonce\" : %s, " + "\"difficulty\" : %d}",
                index, timestamp, data, previousHash, nonce, difficulty);
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
     * Determines if the block is valid
     * */
    public boolean isInvalid() throws NoSuchAlgorithmException {
        return !calculateHash().startsWith("0".repeat(difficulty));
    }
}
