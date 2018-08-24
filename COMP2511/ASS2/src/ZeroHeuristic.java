
public class ZeroHeuristic implements Heuristic<State> {
    @Override
    public int execute(State curr, State goal) {
        return 0;
    }
}
