import java.util.Set;

/**
 * The interface implemented by DirectedCompleteGraph and StateTreeGraph
 * @param <N>: node
 */
public interface Graph<N>{
    void addNode(N n, int cost);
    void addEdge(N from, N to, int cost);
    Set<N> getNeighbour(N from);
    int getEdgeCost(N from, N to);
}
