import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class Astar {

     // Generate all possible next states from the current state, considering swaps and jumps.
     public static List<Node> generateNextStates(Node currentNode) {
        List<Node> nextStates = new ArrayList<>();
        char[] currentTable = currentNode.getTable();
        Node parent = currentNode; // Current node will be the parent for all next states.

        for (int i = 0; i < currentTable.length; i++) {
            // Check for possible swaps with 'E'.
            if (currentTable[i] == 'E') {
                // Check adjacent positions for a swap.
                attemptSwapAndAddState(nextStates, currentTable, i, i - 1, parent, 1); // Left swap
                attemptSwapAndAddState(nextStates, currentTable, i, i + 1, parent, 1); // Right swap

                // Check positions for a jump.
                attemptJumpAndAddState(nextStates, currentTable, i, i - 2, parent, 2); // Left jump
                attemptJumpAndAddState(nextStates, currentTable, i, i + 2, parent, 2); // Right jump
            }
        }

        // Check for possible direct swaps between 'B' and 'W'.
        for (int i = 0; i < currentTable.length - 1; i++) {
            // Swap if adjacent characters are 'B' and 'W'.
            if ((currentTable[i] == 'B' && currentTable[i + 1] == 'W') || (currentTable[i] == 'W' && currentTable[i + 1] == 'B')) {
                char[] newTable = currentTable.clone();
                // Perform the swap.
                char temp = newTable[i];
                newTable[i] = newTable[i + 1];
                newTable[i + 1] = temp;

                String moveDescription = "Swap B and W at positions " + i + " and " + (i + 1);

                // Add new state with a swap cost of 1.
                nextStates.add(new Node(newTable, 1, parent , moveDescription, 1));
            }
        }

        return nextStates;
    }

    private static void attemptSwapAndAddState(List<Node> nextStates, char[] table, int emptyIndex, int targetIndex, Node parent, int cost) {
        if (targetIndex >= 0 && targetIndex < table.length) {
            char[] newTable = table.clone();
            // Perform the swap.
            newTable[emptyIndex] = newTable[targetIndex];
            newTable[targetIndex] = 'E';

            String moveDescription = "Block at " + targetIndex + " moves to " + emptyIndex;

            // Add new state with the specified cost.
            nextStates.add(new Node(newTable, cost, parent, moveDescription, 1));
        }
    }

    private static void attemptJumpAndAddState(List<Node> nextStates, char[] table, int emptyIndex, int targetIndex, Node parent, int cost) {
        if (targetIndex >= 0 && targetIndex < table.length && (emptyIndex - targetIndex == 2 || targetIndex - emptyIndex == 2)) {
            char[] newTable = table.clone();
            // Perform the jump.
            newTable[emptyIndex] = newTable[targetIndex];
            newTable[targetIndex] = 'E';

            String moveDescription = "Block at " + targetIndex + " jumps to " + emptyIndex;
            // Add new state with the specified cost.
            nextStates.add(new Node(newTable, cost, parent, moveDescription, 2));
        }
    }

    static class NodeComparator implements Comparator<Node> {
        public int compare(Node node1, Node node2) {
            return Integer.compare(node1.getF(), node2.getF());
        }
    }

    public static List<Node> findPath(Node startNode) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        Set<String> closedList = new HashSet<>();
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll(); // Get the node with the lowest f value
            if (isGoal(currentNode)) {
                return buildPath(currentNode); // Return the path from start to goal
            }

            closedList.add(Arrays.toString(currentNode.getTable())); // Add the current state to the closed list

            for (Node successor : generateNextStates(currentNode)) {
                String stateKey = Arrays.toString(successor.getTable()); // Convert the table to a string to use as a key
                if (closedList.contains(stateKey)) continue; // Skip if the successor is already in the closed list

                int tentativeG = currentNode.getCost() + successor.getG();  // The cost to move to the successor
                // Check if the successor is already in the open list
                boolean isInOpenList = openList.stream().anyMatch(n -> Arrays.equals(n.getTable(), successor.getTable()));

                if (!isInOpenList || tentativeG < successor.getG()) { // If the successor is not in the open list or has a lower G
                    successor.setG(tentativeG); // Set the G value of the successor
                    if (!isInOpenList) {
                        openList.add(successor);
                    } else {
                        // If the node is already in the open list with a higher G, update it
                        openList.removeIf(n -> Arrays.equals(n.getTable(), successor.getTable()));
                        openList.add(successor);
                    }
                }
            }
        }

        return new ArrayList<>(); // Return empty path if no solution found
    }

    private static boolean isGoal(Node node) {
        char[] table = node.getTable();
        // Create a new String from the table, removing 'E's.
        String arrangementWithoutE = new String(table).replaceAll("E", "");
        // Check if all 'W's are on the left and all 'B's on the right, ignoring 'E'.
        return arrangementWithoutE.matches("W*B*");
    }

    private static List<Node> buildPath(Node goalNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = goalNode;
        while (currentNode != null) {
            path.add(0, currentNode); // Add to the beginning
            currentNode = currentNode.getParent();
        }
        return path;
    }

    public static void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
    
        // Run the garbage collector
        runtime.gc();
    
        // Calculate the used memory
        float memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory in bytes: " + memory);
        System.out.println("Used memory in megabytes: " + bytesToMegabytes(memory));
    }
    
    private static float bytesToMegabytes(float bytes) {
        final long MEGABYTE = 1024L * 1024L;
        return bytes / MEGABYTE;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Give puzzle B = black, W = white and E = empty");
        String input = sc.nextLine().trim().toUpperCase();
        long startTime = System.nanoTime();
        // Convert input string to char array
        char[] table = input.toCharArray();
        System.out.println("Inital state size: " + table.length);
        sc.close();
        // Create the start node with a heuristic value as its initial f value since g=0 at the start
        Node startNode = new Node(table, 0, null, "Initial State", 0 );

        // Execute A* algorithm to find the path
        List<Node> path = findPath(startNode);
        System.out.println();
        if (path.isEmpty()) {
            System.out.println("No solution found.");
        } else {
            System.out.println("Solution path (from start to goal):");
            for (Node node : path) {
                System.out.println(new String(node.getTable())+ " Description: " +node.getMoveDescription() + " F: " + node.getF());
            }
        }
        System.out.println();
        System.out.println("Nodes created: " + Node.getNodesCreated());
        long endTime = System.nanoTime();
        long durationMillis = (endTime - startTime) / 1_000_000;
        System.out.println();
        System.out.println("Execution time: " + durationMillis + " ms");
        System.out.println();
        printMemoryUsage();
    }
}