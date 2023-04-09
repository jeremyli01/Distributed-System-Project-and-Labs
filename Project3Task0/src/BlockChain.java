/**
 * BlockChain class for Project3Task0
 * @author Jeremy(Zihan) Li
 * @andrewEmail zihanli2@andrew.cmu.edu
 * */
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlockChain {
    // ArrayList that stores the blocks
    List<Block> blockList;
    // hash value of the last block
    String chainHash;
    // instance variable approximating the number of hashes per second
    int hashesPerSecond;
    // assigned hash time
    static int HASH_TIME = 2000000;
    // assigned initial string to hash
    static String INITIAL_STRING = "00000000";
    /**
     * Default constructor
     * */
    public BlockChain() {
        blockList = new ArrayList<>();
        chainHash = "";
        hashesPerSecond = 0;
    }
    /**
     * Getter method for variable chainHash
     * */
    public String getChainHash() {
        return chainHash;
    }
    /**
     * Returns the current time
     * */
    public Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }
    /**
     * Returns the latest block in the arrayList
     * */
    public Block getLatestBlock() {
        return blockList.get(blockList.size() - 1);
    }
    /**
     * Returns the size of the arrayList
     * */
    public int getChainSize() {
        return blockList.size();
    }
    /**
     * Computes exactly 2 million hashes and times how long that process takes.
     * So, hashes per second is approximated as (2 million / number of seconds).
     * It is run on start up and sets the instance variable hashesPerSecond.
     * It uses a simple string - "00000000" to hash.
     * */
    public void computeHashesPerSecond() throws NoSuchAlgorithmException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < HASH_TIME; i++) {
            Block.computeHash(INITIAL_STRING);
        }
        long endTime = System.currentTimeMillis();
        hashesPerSecond = (int) (HASH_TIME * 1000 / (endTime - startTime));
    }
    /**
     * Getter method fot the variable hashesPerSecond
     * */
    public int getHashPerSecond() {
        return hashesPerSecond;
    }
    /**
     * Adds a block to the blockList
     * */
    public void addBlock(Block newBlock) {
        blockList.add(newBlock);
    }

    @Override
    public String toString() {
        String blockOutput = "";
        for (Block block : blockList) {
            blockOutput += block.toString();
            blockOutput += ",\n";
        }
        return String.format("{\"ds_chain\" : [%s %n], \"chainHash\" : \"%s\"}",
                blockOutput, chainHash);
    }
    /**
     * Returns the block at position i
     * */
    public Block getBlock(int i) {
        return blockList.get(i);
    }
    /**
     * Returns the sum difficulty of all blocks
     * */
    public int getDifficulty() {
        int sum = 0;
        for (Block block : blockList) {
            sum += block.getDifficulty();
        }
        return sum;
    }
    /**
     * Returns the total expected hashes
     * */
    public double getTotalExpectedHashes() {
        double sum = 0;
        for (Block block : blockList) {
            sum += Math.pow(16, block.getDifficulty());;
        }
        return sum;
    }
    /**
     * Checks if the chain is valid, for each block, examines the hash value of the block, if the previous hash value matches,
     * and if the chainHash matches
     * */
    public String isChainValid() throws NoSuchAlgorithmException {
        int n = getChainSize();
        for (int i = 0; i < n; i++) {
            Block currBlock = getBlock(i);
            if (currBlock.isInvalid()) {
                return String.format("FALSE\nImproper hash on node %d does not begin with %s",
                        i, "0".repeat(getBlock(i).getDifficulty()));
            }
            if (i < n - 1 && !currBlock.calculateHash().equals(getBlock(i + 1).getPreviousHash())) {
                return String.format("FALSE\nPrevious hash of block %d is incorrect", i + 1);
            }
        }
        if (!getLatestBlock().calculateHash().equals(chainHash)) {
            return "FALSE\nThe chain hash does not equal to the hash of the latest block";
        }
        return "TRUE";
    }
    /**
     * Repairs the chain to ensure it meets the requirement defined in isChainValid method
     * */
    public void repairChain() throws NoSuchAlgorithmException {
        int n = getChainSize();
        String currHash = "";
        for (int i = 0; i < n; i++) {
            if (getBlock(i).isInvalid()) {
                currHash = getBlock(i).proofOfWork();
            } else {
                currHash = getBlock(i).calculateHash();
            }
            if (i < n - 1) {
                getBlock(i + 1).setPreviousHash(currHash);
            }
        }
        chainHash = currHash;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        BlockChain blockChain = new BlockChain();
        blockChain.computeHashesPerSecond();
        Block genesis = new Block(0, blockChain.getTime(), "Genesis", 2);
        blockChain.addBlock(genesis);
        blockChain.repairChain();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 0:
                    displayStatus(blockChain);
                    break;
                case 1:
                    addTransaction(blockChain, scanner);
                    break;
                case 2:
                    verify(blockChain);
                    break;
                case 3:
                    view(blockChain);
                    break;
                case 4:
                    corrupt(blockChain, scanner);
                    break;
                case 5:
                    repair(blockChain);
                    break;
                case 6:
                    scanner.close();
                    return;
            }
        }
    }
    /**
     * Prints the menu in the format assigned in the task requirement
     * */
    private static void printMenu() {
        System.out.println("""
                0. View basic blockchain status.
                1. Add a transaction to the blockchain.
                2. Verify the blockchain.
                3. View the blockchain.
                4. Corrupt the chain.
                5. Hide the corruption by repairing the chain.
                6. Exit""");
    }
    /**
     * Displays the current status variable in the blockChain
     * */
    private static void displayStatus(BlockChain blockChain) {
        System.out.printf("""
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
    private static void addTransaction(BlockChain blockChain, Scanner scanner) throws NoSuchAlgorithmException {
        System.out.println("Enter difficulty > 0");
        int difficulty = scanner.nextInt();
        System.out.println("Enter transaction");
        scanner.nextLine(); // one more break line so the scanner can read nextLine
        String data = scanner.nextLine();
        long startTime = System.currentTimeMillis();
        Block newBlock = new Block(blockChain.getChainSize(), blockChain.getTime(), data, difficulty);
        newBlock.setPreviousHash(blockChain.getChainHash());
        blockChain.addBlock(newBlock);
        blockChain.chainHash = newBlock.proofOfWork();
        long endTime = System.currentTimeMillis();
        System.out.printf("Total execution time to add this block was %d milliseconds%n", endTime - startTime);
    }
    /**
     * Verifies if the blockChain is valid and keeps track of the time spent
     * */
    private static void verify(BlockChain blockChain) throws NoSuchAlgorithmException {
        long startTime = System.currentTimeMillis();
        System.out.printf("Chain verification: %s %n", blockChain.isChainValid());
        long endTime = System.currentTimeMillis();
        System.out.printf("Total execution time to verify the chain was %d milliseconds\n", endTime - startTime);
    }
    /**
     * Prints out the JSON format of the blockChain
     * */
    private static void view(BlockChain blockChain) {
        System.out.println("View the Blockchain");
        System.out.println(blockChain.toString());
    }
    /**
     * Corrupts the blockChain by editing the block at position i determined by the user
     * */
    private static void corrupt(BlockChain blockChain, Scanner scanner) {
        System.out.println("Corrupt the Blockchain");
        System.out.println("Enter block ID of block to corrupt");
        int index = scanner.nextInt();
        System.out.printf("Enter new data for block %d%n", index);
        scanner.nextLine(); // one more break line so the scanner can read nextLine
        blockChain.getBlock(index).setData(scanner.nextLine());
        System.out.printf("Block %d now holds %s%n", index, blockChain.getBlock(index).getData());
    }
    /**
     * Repairs the blockChain and keeps track of time
     * */
    private static void repair(BlockChain blockChain) throws NoSuchAlgorithmException {
        long startTime = System.currentTimeMillis();
        blockChain.repairChain();
        long endTime = System.currentTimeMillis();
        System.out.printf("Total execution time required to repair the chain was %d milliseconds%n", endTime - startTime);
    }
}
