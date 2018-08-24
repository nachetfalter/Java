
/**
 * The heuristic interface used by ZeroHeuristic and Minimalout
 * @param <N>: node
 */
public interface Heuristic<N> {
    int execute(N curr, N goal);
}
