package TUDarmstadtTeam2.SearchOld;

/**
 * Created by philipp on 14.05.15.
 */
        import core.game.StateObservation;
        import core.player.AbstractPlayer;
        import ontology.Types;
        import tools.ElapsedCpuTimer;

        import java.util.ArrayList;

/**
 * Created by philipp on 27.04.15.
 * Search Agent for testing the exhaustive search Algorithms
 * TODO: Find heuristic or some kind of "bound" on number of states to figure out if exhaustive search is suitable for the current game
 */

public class SearchAgentOld extends AbstractPlayer{
    //Currently employs BFS
    ArrayList<Types.ACTIONS> actions;
    BoundedDFSearch search;
    boolean playing;

    public SearchAgentOld(StateObservation so, ElapsedCpuTimer elapsedTimer){
        if(so.getNPCPositions() != null){
            System.out.println("Found NPC - not playing");
            playing = false;
        }
        else {
            //  System.out.println("BFS Agent Started");
            search = new BoundedDFSearch(so.getAvailableActions());
            actions = search.startSearch(so, elapsedTimer,150);
            playing =true;
        }
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (playing){
            if (actions == null) {
                actions = search.continueSearch(elapsedTimer);
                return Types.ACTIONS.ACTION_NIL;
            }
            // If path found, play this path
            else {
                return actions.remove(0);
            }
        }
        if(stateObs.getGameTick() == 1999){
            System.out.println("Expanded: " +search.getCount() + " Nodes");
        }
        return Types.ACTIONS.ACTION_NIL;
    }
}

