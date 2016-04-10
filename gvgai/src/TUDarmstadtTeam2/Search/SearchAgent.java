package TUDarmstadtTeam2.Search;
import TUDarmstadtTeam2.utils.AbstractTUTeams2Player;
import TUDarmstadtTeam2.utils.Config;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;

/**
 * Created by philipp on 27.04.15.
 * SearchOld Agent for testing the exhaustive search Algorithms
 * TODO: Find heuristic or some kind of "bound" on number of states to figure out if exhaustive search is suitable for the current game
 */

public class SearchAgent extends AbstractTUTeams2Player{
    private int iterationCount = 0;
    private ArrayList<Types.ACTIONS> actions;
    private ArrayList<Types.ACTIONS> newActions;
    private ArrayList<Types.ACTIONS> constructorActions;
    private BestFirstSearch search;
    boolean playing;

    public SearchAgent(StateObservation so, ElapsedCpuTimer elapsedTimer){
        if(so.getNPCPositions() != null){
            System.out.println("Found NPC - not playing");
            playing = false;
        }
        else {
          //  System.out.println("BFS Agent Started");
            search = new BestFirstSearch(so.getAvailableActions());
            constructorActions = search.startSearch(so, elapsedTimer,Config.SEARCH_DEPTH);
            playing =true;
        }
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if(iterationCount == 1000){
            playing = false;
        }
        //If constructor found solution: play it
        if(constructorActions != null){
            return constructorActions.remove(0);
        }
        //Debugging
        if (Config.PRINT_OUTPUTS && stateObs.getGameTick() == 1999) {
            System.out.println("Expanded: " + search.count + " nodes, reached depth: "+ search.maxReachedDepth);
        }
        if (!playing) {
            //TODO do something more useful (start MCTS or something)
            return Types.ACTIONS.ACTION_NIL;
        }
        //If current Iteration is finished, get best path and start next
        if(iterationCount % Config.ITERATION_SIZE == 0 && iterationCount != 0){
            actions = search.getCurrentBestPath();
            newActions = search.initNextIteration(elapsedTimer);
        }
        else{
            if(!search.isFinished()) {
                newActions = search.continueSearch(elapsedTimer);
            }
        }
        iterationCount ++;
        // Check if new actions available, and old actions are all played
        if(actions == null || actions.size() == 0){
            actions = newActions;
        }
        //If actions available, play them
        if(actions != null && actions.size() != 0){
            return actions.remove(0);
        }
        else{
            return Types.ACTIONS.ACTION_NIL;
        }
    }
    @Override
    public boolean isPlaying() {
        return !search.isAborted() && this.playing;
    }

}

