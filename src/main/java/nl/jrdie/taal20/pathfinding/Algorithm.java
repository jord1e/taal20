package nl.jrdie.taal20.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Algorithm {
    static String[][] grid;

    static Vector2 startPos;
    static Vector2 destination;
    static Vector2 currentPos;
    static final Vector2[] checks = { new Vector2(0, 1), new Vector2(1, 0), new Vector2(0, -1), new Vector2(-1, 0) };
    static final String[] lines = {"stapVooruit","draaiLinks","draaiRechts"};

    static int GetH(Vector2 pos)
    {
        if (grid[(int)pos.x][(int)pos.y].contains("O"))
            return Integer.MAX_VALUE;
        return (int)(Math.abs(destination.x - pos.x) + Math.abs(destination.y - pos.y));
    }
    static int GetF(Vector2 pos, int g)
    {
        if (grid[(int)pos.x][(int)pos.y].contains("O"))
            return Integer.MAX_VALUE;
        return g + GetH(pos);
    }

    static int GetLowestIndex(List<Float> values)
    {
        int a = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < values.size(); i++)
        {
            if (values.get(i) < a)
            {
                a = Integer.parseInt(values.get(i).toString());
                index = i;
            }
        }

        return index;
    }

    public static int GetLowestF(List<Node> nodes)
    {
        int f = Integer.MAX_VALUE;
        int index = -1;
        for(int i = 0; i < nodes.size(); i++)
        {
            if(nodes.get(i).fCost < f && !nodes.get(i).scanned)
            {
                f = nodes.get(i).fCost;
                index = i;
            } else if (nodes.get(i).fCost == f && !nodes.get(i).scanned)
            {
                index = -1;
            }
        }
        return index;
    }
    public static int GetLowestH(List<Node> nodes)
    {
        int h = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < nodes.size(); i++)
        {
            if (nodes.get(i).hCost < h && !nodes.get(i).scanned)
            {
                h = nodes.get(i).hCost;
                index = i;
            }
            System.out.println(nodes.get(i).hCost+" yo "+nodes.get(i).scanned);
        }
        return index;
    }

    public static boolean hasNode(List<Node> nodes, int x, int y)
    {
        for (Node node : nodes) {
            if (node.x == x && node.y == y) {
                return true;
            }
        }
        return false;
    }

    public static int GetG(List<Node> nodes, int x, int y)
    {
        for (Node node : nodes) {
            if (node.x == x && node.y == y) {
                return node.gCost;
            }
        }
        return 0;
    }

    public static String FindPath(int size, String maze)
    {
        String[] maze_ = maze.split(";");

        grid = new String[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = maze_[i*size + j];
            }
        }

        int startingdirection = Integer.parseInt(maze.split("S")[1].charAt(0)+"");
        int koe = Arrays.asList(maze_).indexOf("S"+startingdirection);
        startPos = new Vector2(koe % size, (int)Math.floor((float)koe / (float)size));

        System.out.println(startPos.x + " yo uowe "+ startPos.y);

        String[] goals_ = maze.split("D");
        String[] goals = new String[goals_.length-1];
        for (int i = 0; i < goals_.length-1; i++) {
            goals[i] = "D"+goals_[1+i];
        }
        Vector2[] destinations = new Vector2[goals.length];
        for (int i = 0; i < goals.length; i++) {
            int kip = Arrays.asList(maze_).indexOf(goals[i]);
            destinations[i] = new Vector2(kip % size, (int)Math.floor((float)kip / (float)size));
        }

        destination = destinations[0];

        List<String> path = new ArrayList<>();

        int aantal = 0;
        Node node = null;
        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node((int)startPos.x, (int)startPos.y, 0, GetH(startPos), GetH(startPos)));
        while (aantal < 1000)
        {
            int aap = GetLowestF(nodes);
            if(aap == -1)
                node = nodes.get(GetLowestH(nodes));
            else
                node = nodes.get(aap);
            node.scanned = true;
            if (node.x == (int)destination.x && node.y == (int)destination.y)
                break;
            for (Vector2 check : checks) {
                int a = node.x - (int) check.x;
                int b = node.y - (int) check.y;
                if (!(a > -1 && b > -1 && a < size && b < size))
                    continue;
                if (!hasNode(nodes, a, b) && !grid[a][b].contains("O")) {
                    nodes.add(new Node(a, b, node.gCost + 1, GetH(new Vector2(a, b)), GetF(new Vector2(a, b), node.gCost + 1)));
                }
            }
            aantal++;
        }
        currentPos = destination;
        int direction = -1;
        aantal = 0;
        while (aantal < 1000)
        {
            List<Vector2> tiles = new ArrayList<>();
            List<Float> waardes = new ArrayList<>();
            for (int i = 0; i < checks.length; i++) {
                int a = (int) (currentPos.x - checks[i].x);
                int b = (int) (currentPos.y - checks[i].y);
                if (!(a > -1 && b > -1 && a < size && b < size))
                    continue;
                tiles.add(new Vector2(a, b));
                int g = GetG(nodes, a, b);
                waardes.add(g == 0 ? Float.MAX_VALUE : g);
                if (direction == -1) direction = i;
                if (i < direction) {
                    if (direction == 3 && i == 0)
                        path.add(lines[1]);
                    else
                        path.add(lines[2]);
                } else if (i > direction) {
                    if (direction == 0 && i == 3)
                        path.add(lines[2]);
                    else
                        path.add(lines[1]);
                }
                path.add(lines[0]);
            }
            currentPos = new Vector2((int)tiles.get(GetLowestIndex(waardes)).x, (int)tiles.get(GetLowestIndex(waardes)).y);
            if (waardes.get(GetLowestIndex(waardes)) == 1) break;
            aantal++;
        }

        if (startingdirection < direction) {
            if (direction == 3 && startingdirection == 0)
                path.add(lines[1]);
            else
                path.add(lines[2]);
        } else if (startingdirection > direction) {
            if (direction == 0 && startingdirection == 3)
                path.add(lines[2]);
            else
                path.add(lines[1]);
        }

        String output = null;
        for (int i = path.size()-1; i >= 0; i--) {
            output += path.get(i)+"\n";
        }

        return output;
    }
}
