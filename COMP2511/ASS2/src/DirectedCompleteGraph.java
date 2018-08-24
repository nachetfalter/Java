import java.util.*;

/**
 * Class: DirectedCompeteGraph
 * Responsibility: maintain the vertex and edges  of a directed complete graph
 * Collaborator: graph, planner
 */
public class DirectedCompleteGraph<N> implements Graph<N> {
    private Map <N, Integer> node;
    private Map <List<N>, Integer> edge;

    DirectedCompleteGraph() {
        node = new HashMap<>();
        edge = new HashMap<>();
    }

    /**
     * Add a node to the graph
     * @param n: the info about the node
     * @param cost: the inherit cost of this node.
     * In this case, it would be the fueling cost
     */
    @Override
    public void addNode(N n, int cost) {
        if (node.containsKey(n)) return;
        node.put(n, cost);
    }

    /**
     * Add an edge to the graph
     * @param from: the node where the edge is from
     * @param to: the node where this edge links to
     * @param cost: the cost of this edge, it will be combined
     * with the cost of the departure city's refueling cost
     */
    @Override
    public void addEdge(N from, N to, int cost) {
        List<N> a = new ArrayList<>();
        Collections.addAll(a, from, to);
        if (!edge.containsKey(a)) edge.put(a, cost + node.get(from));
    }

    /**
     * Get the neighbour nodes by reading the keyset
     * This can be justified because the graph is complete
     * @param from: the node we are checking
     * @return: return a set of all neighbouring nodes
     */
    @Override
    public Set<N> getNeighbour(N from) {
        if (from == null) return node.keySet();
        Set<N> a = new HashSet<>(node.keySet());
        a.remove(from);
        return a;
    }

    /**
     * Get the cost of an edge
     * @param from: one side of the edge
     * @param to: the other side of the edge
     * @return: return the cost of the edge
     */
    @Override
    public int getEdgeCost(N from, N to) {
        List<N> a = new ArrayList<>();
        Collections.addAll(a, from, to);
        if (edge.get(a) == null) return 0;
        return edge.get(a);
    }
}
