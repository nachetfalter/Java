import java.util.*;

/**
 * Class: State
 * Responsibility: maintaining individual state within the state tree
 * Collaborator: the heuristics, edgeNode, node
 * route: the path we already finished
 * connected: the next/previous state
 * cost: cost of moving from the last state
 */
public class State implements Node{
    private List<EdgeNode> route;
    private Set<State> connected;
    private int cost;

    State(List<EdgeNode> route) {
        this.route = new ArrayList<>(route);
        this.connected = new HashSet<>();
        cost = 0;
    }

    /**
     * connected Getter
     * @return connected states
     */
    @Override
    public Set<State> getConnected() {
        return connected;
    }

    public int getCost() {
        return cost;
    }

    /**
     * Get the path stored in the state
     * @return List of edgeNode in order
     */
    public List<EdgeNode> getRoute() {
        return route;
    }

    /**
     * This is used by the A start to check terminating condition. The A star doesn't rely on this
     * to run.
     * @param obj: the supposed goal State
     * @return true if we reached the terminating condition, false if not.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !State.class.isAssignableFrom(obj.getClass())) return false;
        State o = (State) obj;
        return route.size() == o.route.size();
    }

    /**
     * Connect two states
     * @param s: the state which the current try to connect to
     */
    public void connect(State s) {
        connected.add(s);
    }

    /**
     * Set the cost of the state
     * @param cost: cost of moving from the last state to this state.
     *  !Not accumulated
     */
    public void setCost(int cost) {
        this.cost = cost;
    }
}
