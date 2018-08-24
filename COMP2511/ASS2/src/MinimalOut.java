/**
 * For explanation please read the beginning entry of ShipmentPlanner.java
 */
public class MinimalOut implements Heuristic<State> {

    @Override
    public int execute(State curr, State goal) {
        return (goal.getRoute().size() - curr.getRoute().size()) * goal.getCost();
    }
}

