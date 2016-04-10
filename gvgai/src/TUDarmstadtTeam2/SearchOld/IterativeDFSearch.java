package TUDarmstadtTeam2.SearchOld;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;

/**
 * Created by philipp on 27.04.15.
 * Iterative Deepening SearchOld:
 * This implementation employs Bounded Depth First SearchOld.
 * This implementation allows to stop the search at any node (when time is up) and continue it from this node.
 * Start search by calling startSearch()
 * Continue search by calling continueSearch()
 */

public class IterativeDFSearch implements TreeSearch {

    private BoundedDFSearch searcher;
    private ArrayList<Types.ACTIONS> actions;
    private int iteration, maxDepth;
    private StateObservation rootObservation;
    private int abortTime = 3; // Todo init this in constructor?
    public IterativeDFSearch(ArrayList<Types.ACTIONS> availableActions){
        searcher = new BoundedDFSearch(availableActions);
    }

    /**
     * initialises the search
     * @param observation current state of the game
     * @param timer
     * @param
     * @return null if no winning path is found, else an optimal (if length of optimal path longer than "from")
     *                                          winning path (optimal in the sense of length, not of score!!)
     */
    public ArrayList<Types.ACTIONS> startSearch(StateObservation observation, ElapsedCpuTimer timer, int ... searchInterval){
        this.rootObservation = observation;
        if(searchInterval.length != 2){
            System.out.println("Wrong search interval configuration for IDS. ([0] = from, [1] = to");
        }
        this.maxDepth = searchInterval[1];
        iteration = searchInterval[0] - 1; // iteration init with -1 since its incremented right after init (first loop iteration)
        while (timer.remainingTimeMillis() > abortTime && iteration < this.maxDepth && actions == null){
            iteration ++;
            //System.out.println("IDS: Started iteration with maxDepth: " +iteration);
            actions = searcher.startSearch(observation,timer,iteration);
        }
        return actions;
    }

    /**
     * continues the search.
     * startSearch has to be called at least once before this method can be called.
     * @param timer
     * @return null if no winning path is found, else an optimal (if length of optimal path longer than "from")
     *                                          winning path (optimal in the sense of length, not of score!!)
     */

    public ArrayList<Types.ACTIONS> continueSearch(ElapsedCpuTimer timer){
        if(searcher.isFinished() && iteration < maxDepth){
            iteration ++;
            return searcher.startSearch(rootObservation,timer,iteration);
        }
        else{
            if(!searcher.isFinished())
                return searcher.continueSearch(timer);
        }
        return null;

    }

    @Override
    public int getCount() {
        System.out.println("get count currently not available for IDS");
        return 0;
    }
}
