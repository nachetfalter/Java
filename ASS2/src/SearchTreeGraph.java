import java.util.*;

/**
 * Class: SearchTreeGraph
 * Responsibility: maintain a graph of states
 * Collaborator: graph, node, planner
 * @param <T> node
 */
public class SearchTreeGraph<T extends Node> implements Graph<T> {
    private Map<T, Integer> node;
    private Map <Set<T>, Integer> edge;
    private int shortestEdge;

    SearchTreeGraph() {
        node = new HashMap<>();
        edge = new HashMap<>();
        shortestEdge = Integer.MAX_VALUE;
    }

    /**
     * Add a node
     * @param n: node
     * @param cost: cost of the node
     */
    @Override
    public void addNode(T n, int cost) {
        if (node.containsKey(n)) return;
        node.put(n, cost);
    }

    /**
     * Add an edge
     * @param from: where the edge is from
     * @param to: where the edge is going to
     * @param cost: the cost of the edge
     * The smallest edge is also calculated here.
     * If the smallest edge turns out to be 0 (mostly
     * because of my way of building the graph...
     * e.g. (Shanghai Sydney) (Sydney Shanghai). The cost of
     * the second Sydney would be included in the cost of the
     * second edge, thus leaving the distance between them 0.
     * In order to avoid 0 messing up later calculation, I only reduce
     * the cost to 1.
     */
    @Override
    public void addEdge(T from, T to, int cost) {
        Set<T> a = new HashSet<>();
        Collections.addAll(a, from, to);
        if (!edge.containsKey(a)) {
            edge.put(a, cost);
            if (cost < shortestEdge) {
                if (cost == 0) {
                    shortestEdge = 1;
                }
                else {
                    shortestEdge = cost;
                }
            }
        }
    }

    /**
     * Get the neighbours by invoke the interface method
     * @param from: our chosen node
     * @return the list of neighbouring nodes
     */
    @Override
    public Set<T> getNeighbour(T from) {
        return from.getConnected();
    }

    /**
     * Get the cost of an Edge
     * @param from: the node where the edge is from
     * @param to: the node where the edge leads to
     * @return the cost of the edge
     */
    @Override
    public int getEdgeCost(T from, T to) {
        Set<T> a = new HashSet<>();
        Collections.addAll(a, from, to);
        if (edge.get(a) == null) return 0;
        return edge.get(a);
    }

    /**
     * Retrieve the shortest edge for calculation
     * @return the shortest edge length
     */
    public int getShortestEdge() {
       return shortestEdge;
    }
}
