import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Class: ShipmentPlanner
 * @author Victor Wang
 * Responsibility: maintains the original graph derived from input
 *                  build the search tree graph
 *                 import information from input
 *                 run search functions to calculate optimal path.
 * Collaborator: Everything
 * @inv: None of the graphs should be empty.
 *
 * Analysis of the Heuristic I use:
 * It's O(1) complexity. Java .size is n(1), the arithmetic calculations can be
 * done in O(1) as well.
 * The goal state has the total amount of shipment that needed to be done.
 * By reduce it by how many we already finished, we know how many are there to go.
 * Then the number of remaining edges is multiplied by the shortest edge length in the
 * treegraph for a app.
 * This is admissible since even in the closest case the actual remaining edges
 * have to all be the smallest to be equal to this approximation.
 * This heuristic generally reduce node expansion by 100-200, and run time by 10-15 ms
 */
public class ShipmentPlanner {
    private Graph<String> initGraph;
    private SearchTreeGraph<State> stateGraph;
    private Search<State> search;
    private Heuristic<State> strategy;
    private List<State> result;


    public ShipmentPlanner() {
        initGraph = new DirectedCompleteGraph<>();
        stateGraph = new SearchTreeGraph<>();
        search = new AStar<>();
        strategy = new MinimalOut();
        result = new LinkedList<>();
    }


    /**
     * This function add node to the original graph
     * @param n: the name of the node
     * @param c: the cost of the node
     */
    public void addNode(String n, int c) {
        initGraph.addNode(n, c);
    }


    /**
     * This function adds an edge to the original graph
     * @param n1: name of the first node
     * @param n2: name of the second node
     * @param c: cost of the edge, the refueling cost would be automatically added within Graph.
     */
    public void addEdge(String n1, String n2, int c) {
        initGraph.addEdge(n1, n2, c);
    }


    /**
     * This function set up the result for toString to use
     * @param result: the returned path from A star
     */
    public void setResult(List<State> result) {
        this.result = result;
    }


    /**
     * This function set up the stateTree graph by reading from the collection of goal Edges, then
     * build a new graph through reading the original graph.
     * @param l: This is the initial state, or the last state before the next recursion
     * @param r: this is the Route list, this list consists of what we already went through
     * @param g: this is the Goal list, this list consists of what we need to go through
     * @pre: In the case of this assignment, the initial l will always be EdgeNode Sydney, Sydney
     */
    public void constructStateGraph(State l, List<EdgeNode> r, List<EdgeNode> g) {
        if (g.isEmpty()) return;
        for (EdgeNode i : g) {
            List<EdgeNode> route = new ArrayList<>(r);
            route.add(i);
            State n = new State(route);
            n.setCost(initGraph.getEdgeCost(route.get(route.size() - 2).getTo(), i.getFrom()));
            stateGraph.addNode(n, initGraph.getEdgeCost(i.getFrom(), i.getTo()));
            stateGraph.addEdge(l, n, initGraph.getEdgeCost(route.get(route.size() - 2).getTo(), i.getFrom()));
            l.connect(n);
            n.connect(l);
            List<EdgeNode> goal = new ArrayList<>(g);
            goal.remove(i);
            constructStateGraph(n, route, goal);
        }
    }


    /**
     * This function invoke the search algorithm
     * @param start: the start state
     * @param goal: the goal state
     * @return: the optimal path between them
     */
    public List<State> search(State start, State goal) {
        return search.execute(start, goal, stateGraph, strategy);
    }


    /**
     * Derive the shortestEdge of all edges
     * @return shortest edge in the treegraph
     */
    public int getShortestEdge() {
        return stateGraph.getShortestEdge();
    }


    /**
     * Description:
     * The main function read the file and invoke various import function
     * @pre args[0] != null
     * @param args: the file where main will read from
     */
    public static void main(String args[]) {
        EdgeNode start = new EdgeNode("Sydney", "Sydney");
        ShipmentPlanner plan = new ShipmentPlanner();
        Scanner sc = null;
        List<EdgeNode> goal = new ArrayList<>();
        goal.add(start);
        try {
            sc = new Scanner(new File(args[0]));
            while (sc.hasNext()) {
                // This is the new line split to remove all space
                String[] buffer = sc.nextLine().split("\\s+");
                switch (buffer[0]) {
                    case "Refuelling": {
                        plan.addNode(buffer[2], Integer.parseInt(buffer[1]));
                        break;
                    }
                    case "Time": {
                        plan.addEdge(buffer[2], buffer[3], Integer.parseInt(buffer[1]));
                        plan.addEdge(buffer[3], buffer[2], Integer.parseInt(buffer[1]));
                        break;
                    }
                    case "Shipment": {
                        EdgeNode a = new EdgeNode(buffer[1], buffer[2]);
                        goal.add(a);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Invalid Command");
                    }
                }
            }
            // The tree graph is created after reading the file
            List<EdgeNode> route = new ArrayList<>();
            List<EdgeNode> init = new ArrayList<>();
            init.add(start);
            route.add(start);
            State from = new State(init);
            State to = new State(goal);
            goal.remove(start);
            plan.constructStateGraph(from, route, goal);
            to.setCost(plan.getShortestEdge());
            plan.setResult(plan.search(from, to));
            System.out.print(plan.toString());
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        finally {
            if (sc != null) sc.close();
        }
    }


    /**
     * This function return the output string derived from the search algorithm
     * @return string derived from search algorithm
     */
    @Override
    public String toString() {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        List<EdgeNode> answer = result.get(result.size() - 1).getRoute();
        answer.remove(0);
        String oldTo = "";
        int cost = 0;
        for (EdgeNode i : answer) {
            if(!oldTo.equals("") && !oldTo.equals(i.getFrom())) {
                sb1.append("Ship ").append(oldTo).append(" to ").append(i.getFrom()).append("\n");
                cost += initGraph.getEdgeCost(oldTo, i.getFrom());
            }
            sb1.append("Ship ").append(i.getFrom()).append(" to ").append(i.getTo()).append("\n");
            cost += initGraph.getEdgeCost(i.getFrom(), i.getTo());
            oldTo = i.getTo();
        }
        sb2.append(result.size()).append(" nodes expanded").append("\n").append("cost = ").append(cost).append("\n");
        return sb2.append(sb1).toString();
    }
}
