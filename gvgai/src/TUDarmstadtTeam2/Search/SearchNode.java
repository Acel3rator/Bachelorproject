package TUDarmstadtTeam2.Search;

import core.game.StateObservation;
import ontology.Types;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by philipp on 25.04.15.
 * Holds the information necessary for a tree search trough the state Space
 */
//TODO: Find way to minimize memory usage (delete observation after expanding?)
public class SearchNode {
   // private StateObservation observation;
    private ArrayList<Types.ACTIONS> path;
    private final int depth;
    private StateObservation state;

    private int stateDepth;


    public SearchNode(StateObservation state, int stateDepth, ArrayList<Types.ACTIONS> leadingActions, int depth){
        this.state = state;
        this.stateDepth = stateDepth;
        this.path = leadingActions;
        this.depth = depth;
    }

    //getter
    public int getDepth(){
        return this.depth;
    }
    public ArrayList<Types.ACTIONS> getPath(){
        return path;
    }
    public boolean hasOwnState(){
        return depth == stateDepth;
    }
    public StateObservation getState() {
        return state;
    }
    public int getStateDepth() {
        return stateDepth;
    }
    public double getGameScore(){
        return state.getGameScore();
    }
}
