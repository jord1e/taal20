package nl.jrdie.taal20.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Algorithm {
    static String[][] grid;

    static Vector2 startPos;
    static Vector2 destination;
    static Vector2 currentPos;
    static final Vector2[] checks = {new Vector2(0, 1), new Vector2(1, 0), new Vector2(0, -1), new Vector2(-1, 0)};
    static final String[] lines = {"stapVooruit", "draaiLinks", "draaiRechts"};


    static int GetH(Vector2 pos) {
        if (grid[(int) pos.x][(int) pos.y].contains("O") || grid[(int) pos.x][(int) pos.y].contains("R0")) {
            return Integer.MAX_VALUE;
        }
        return (int) (Math.abs(destination.x - pos.x) + Math.abs(destination.y - pos.y));
    }

    static int GetF(Vector2 pos, int g) {
        if (grid[(int) pos.x][(int) pos.y].contains("O") || grid[(int) pos.x][(int) pos.y].contains("R0")) {
            return Integer.MAX_VALUE;
        }
        int x = 1;
        if (grid[(int) pos.x][(int) pos.y].contains("R"))
            x = 2;
        return (g + GetH(pos)) * x;
    }

    static int GetLowestIndex(List<Float> values) {
        int a = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) < a) {
                a = Math.round(values.get(i));
                index = i;
            }
        }

        return index;
    }

    public static int GetLowestF(List<Node> nodes) {
        int f = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).fCost < f && !nodes.get(i).scanned) {
                f = nodes.get(i).fCost;
                index = i;
            } else if (nodes.get(i).fCost == f && !nodes.get(i).scanned) {
                index = -1;
            }
        }
        return index;
    }

    public static int GetLowestH(List<Node> nodes) {
        int h = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).hCost < h && !nodes.get(i).scanned) {
                h = nodes.get(i).hCost;
                index = i;
            }
        }
        return index;
    }

    public static boolean hasNode(List<Node> nodes, int x, int y) {
        for (Node node : nodes) {
            if (node.x == x && node.y == y) {
                return true;
            }
        }
        return false;
    }

    public static int GetG(List<Node> nodes, int x, int y) {
        for (Node node : nodes) {
            if (node.x == x && node.y == y) {
                return node.gCost;
            }
        }
        return 0;
    }

    public static int getDirection(Vector2 vec){
        for(int i = 0; i < checks.length; i++){
            if(vec.equals(checks[i]))
                return i;
        }
        return -1;
    }

    public static String FindPath(int size, String maze) {
        String[] maze_ = maze.split(";");
        grid = new String[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[j][i] = maze_[i * size + j];
            }
        }

        int startingdirection = Integer.parseInt(maze.split("S")[1].charAt(0) + "");
        int koe = Arrays.asList(maze_).indexOf("S" + startingdirection);
        startPos = new Vector2(koe % size, (int) Math.floor((float) koe / (float) size));
        System.out.println("StartPos: " + startPos);
        String[] goals_ = maze.split("D");
        String[] goals = new String[goals_.length - 1];
        for (int i = 0; i < goals_.length - 1; i++) {
            goals[i] = "D" + goals_[1 + i].charAt(0);
        }
        Vector2[] destinations = new Vector2[goals.length];
        for (int i = 0; i < goals.length; i++) {
            int kip = Arrays.asList(maze_).indexOf(goals[i]);
            destinations[i] = new Vector2(kip % size, (int) Math.floor((float) kip / (float) size));
        }
        //destination = destinations[0];
        destination = new Vector2(5,2);
        System.out.println("Destination: " + destination + "; " + goals[0]);

        List<String> path = new ArrayList<>();

        int aantal = 0;
        Node node = null;
        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node((int) startPos.x, (int) startPos.y, 0, GetH(startPos), GetH(startPos)));
        while (aantal < 1000) {
            int aap = GetLowestF(nodes);
            if (aap == -1)
                node = nodes.get(GetLowestH(nodes));
            else
                node = nodes.get(aap);
            node.scanned = true;
            if (node.x == (int) destination.x && node.y == (int) destination.y)
                break;
            for (int i = 0; i < checks.length; i++) {
                int a = node.x - (int) checks[i].x;
                int b = node.y - (int) checks[i].y;
                if (!(a > -1 && b > -1 && a < size && b < size))
                    continue;
                if (!hasNode(nodes, a, b) && !(grid[a][b].contains("O") || grid[a][b].contains("R0"))) {
                    nodes.add(new Node(a, b, node.gCost + 1, GetH(new Vector2(a, b)), GetF(new Vector2(a, b), node.gCost + 1)));
                }
            }
            aantal++;
        }
        currentPos = destination;
        int direction = -1;
        aantal = 0;
        while (aantal < 1000) {
            List<Vector2> tiles = new ArrayList<>();
            List<Float> waardes = new ArrayList<>();
            if (GetG(nodes, (int) currentPos.x, (int) currentPos.y) == 1) break;
            for (int i = 0; i < checks.length; i++) {
                int a = (int) (currentPos.x - checks[i].x);
                int b = (int) (currentPos.y - checks[i].y);
                if (!(a > -1 && b > -1 && a < size && b < size))
                    continue;
                tiles.add(new Vector2(a, b));
                int g = GetG(nodes, a, b);

                waardes.add(g == 0 ? Float.MAX_VALUE : g);
            }

            int richting = GetLowestIndex(waardes);
            if (direction == -1) direction = richting;
            if (grid[(int) currentPos.x][(int) currentPos.y].contains("R")) {
                int a = Integer.parseInt(grid[(int) currentPos.x][(int) currentPos.y].charAt(1) + "");
                switch (a) {
                    case 1:
                        direction++;
                        break;
                    case 2:
                        direction+=2;
                        break;
                    case 3:
                        direction+=3;
                        break;
                }
            }
            direction %= 4;
            if (Math.abs(direction - richting) == 2) {
                path.add(lines[2]);
                path.add(lines[2]);
            } else if (direction == 0) {
                if (richting == 1) {
                    path.add(lines[2]);
                } else if (richting == 3) {
                    path.add(lines[1]);
                }
                direction = richting;
            } else if (direction == 1 || direction == 2) {
                if (richting > direction) {
                    direction++;
                    path.add(lines[2]);
                } else if (richting < direction) {
                    direction--;
                    path.add(lines[1]);
                }
            } else if (direction == 3) {
                if (richting == 0) {
                    direction = 0;
                    path.add(lines[2]);
                } else if (richting == 2) {
                    direction = 2;
                    path.add(lines[1]);
                }
            }
            path.add(lines[0]);
            currentPos = new Vector2((int) tiles.get(GetLowestIndex(waardes)).x, (int) tiles.get(GetLowestIndex(waardes)).y);
            System.out.println(currentPos);
            aantal++;
        }
        path.add(lines[0]);
        int richting = startingdirection;
        if (Math.abs(direction - richting) == 2 && direction != -1) {
            path.add(lines[2]);
            path.add(lines[2]);
        } else if (direction == 0) {
            if (richting == 1) {
                path.add(lines[2]);
            } else if (richting == 3) {
                path.add(lines[1]);
            }
        } else if (direction == 1 || direction == 2) {
            if (richting > direction) {
                path.add(lines[2]);
            } else if (richting < direction) {
                path.add(lines[1]);
            }
        } else if (direction == 3) {
            if (richting == 0) {
                path.add(lines[2]);
            } else if (richting == 2) {
                path.add(lines[1]);
            }
        }
        String output = "";
        for (int i = path.size() - 1; i >= 0; i--) {
            output += path.get(i) + "\n";
        }

        return output;
    }
}
