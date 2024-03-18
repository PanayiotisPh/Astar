public class Node {
    private char[] table;
    private int g; // Cost from start to this node
    private int h; // Heuristic cost to goal
    private int f; // Total cost (g + h)
    private Node parent;
    private String moveDescription;
    private static int nodesCreated = 0;
    private int cost;

    public Node(char[] table, int g, Node parent, String moveDescription, int cost) {
        this.table = table;
        this.g = g;
        this.parent = parent;
        this.moveDescription = moveDescription;
        this.h = calculateHeuristic(table);
        this.f = g + h;
        this.cost = cost;
        nodesCreated++;
    }

    public int calculateHeuristic(char[] table) {
        int heuristic = 0;
        // Count how far each 'W' is from its target position on the left
        // and each 'B' from its target position on the right.
        for (int i = 0; i < table.length; i++) {
            char c = table[i];
            if (c == 'W') {
                // For 'W', the cost is its index, as we want it to move to the leftmost side.
                heuristic += i;
            } else if (c == 'B') {
                // For 'B', the cost is the distance from the rightmost side.
                heuristic += table.length - 1 - i;
            }
        }
        return heuristic;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public char[] getTable() {
        return table;
    }

    public void setTable(char[] table) {
        this.table = table;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getMoveDescription() {
        return moveDescription;
    }

    public void setMoveDescription(String moveDescription) {
        this.moveDescription = moveDescription;
    }

    public static int getNodesCreated() {
        return nodesCreated;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
        this.f = g + this.h;
    }
    
}
