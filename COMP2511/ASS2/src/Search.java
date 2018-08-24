import java.util.List;

/**
 * The search algorithm interface, implemented by AStar
 * @param <N> node
 */
public interface Search<N> {
    List<N> execute(N from, N to, Graph<N> graph, Heuristic<N> heuristic);
}
