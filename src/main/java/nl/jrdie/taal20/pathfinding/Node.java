package nl.jrdie.taal20.pathfinding;

public class Node {
    public int x;
    public int y;
    public int gCost;
    public int hCost;
    public int fCost;
    public boolean scanned = false;

    public Node(int x, int y, int gCost, int hCost, int fCost)
    {
        this.x = x;
        this.y = y;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = fCost;
    }
}
