import java.util.*;

/**
 * Class: A star
 * Responsibility: calculate the optimal path
 * Collaborator: Comparator, graph, search, heuristic, planner
 * @inv: None of the graphs should be empty.
 */
public class AStar<N> implements Search<N> {
    private PriorityQueue<N> openSet = new PriorityQueue<>(new NComparator());
    private Map<N, Integer> fMap = new HashMap<>();
    private Map<N, Integer> gMap = new HashMap<>();

    /**
     * This comparator use the f value stored to put the items in order.
     */
    private final class NComparator implements Comparator<N> {
        @Override
        public int compare(N o1, N o2) {
            if (fMap.get(o1) < fMap.get(o2)) return -1;
            if (fMap.get(o1) > fMap.get(o2)) return 1;
            return 0;
        }
    }

    /**
     * A star implementation, take a start node, a finish node, a graph and the heuristic
     * @param start: the start node
     * @param goal: the goal node
     * @param graph: the graph composed of the nodes
     * @param heuristic: the heuristic algorithm used to alter h
     * @return the optimal path from the start node to the goal node
     */
    @Override
    public List<N> execute(N start, N goal, Graph<N> graph, Heuristic<N> heuristic) {
        openSet.add(start);
        fMap.put(start, 0);
        gMap.put(start, 0);
        List<N> result = new ArrayList<>();
        while (!openSet.isEmpty()) {
            N head = openSet.poll();
            int g, h, f;
            result.add(head);
            if (head.equals(goal)) {
                return result;
            }
            for(N neighbour : graph.getNeighbour(head)) {
                g = gMap.get(head) + graph.getEdgeCost(head, neighbour);
                h = heuristic.execute(neighbour, goal);
                f = g + h;
                if (fMap.containsKey(neighbour) && f < fMap.get(neighbour)) {
                    openSet.remove(neighbour);
                    gMap.put(neighbour, g);
                    fMap.put(neighbour, g);
                    openSet.add(neighbour);
                }
                else if (!fMap.containsKey(neighbour)){
                    gMap.put(neighbour, g);
                    fMap.put(neighbour, f);
                    openSet.add(neighbour);
                }
            }
        }
        return null;
    }
}
