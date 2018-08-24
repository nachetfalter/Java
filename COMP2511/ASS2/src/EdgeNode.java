import java.util.ArrayList;
import java.util.List;

/**
 * Class: EdgeNode
 * Responsibility: store specific target edges as a node for efficiency
 * Collaborator: state, planner
 *
 * This class may require slightly more explanation.
 * I realise we can't really change what a single target shipment would cost
 * No matter what we do, these costs cannot be reduced. Therefore the only
 * way we can optimise this is to arrange the shipments (specific edges) in a specific
 * order because this is a directed graph due to the existence of fueling cost.
 * Although I take a specific edge as node, non target edges are still normal edges.
 * The cost between two edge node is the edge length between the destination of trip A
 * and the start location of trip B. In this case the 'node cost' is not added again as
 * it is already added when the edges are added in the original graph (initGraph) in the
 * first time.
 * So the whole graph can be reduced into a directed weighted graph with distinct nodes.
 * I add a "Sydney, Sydney" at the beginning which can be ignored later, so I can choose
 * the best first step.
 * I believe by doing this I can massively reduce the numbers of expanded nodes.
 * */
public class EdgeNode{
    private List<String> edge;

    EdgeNode(String from, String to)
    {
        edge = new ArrayList<>();
        edge.add(from);
        edge.add(to);
    }

    /**
     * Get where the edge is from
     * @return the name of the node where the edge is from
     */
    public String getFrom() {
        return edge.get(0);
    }

    /**
     * Get where the edge is going to
     * @return the name of the node where the edge is going to
     */
    public String getTo() {
        return edge.get(1);
    }
}
