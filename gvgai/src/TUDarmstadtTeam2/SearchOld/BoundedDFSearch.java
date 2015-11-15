package TUDarmstadtTeam2.SearchOld;

import TUDarmstadtTeam2.Search.SearchNode;
import TUDarmstadtTeam2.stateIdentification.StateIdentification;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.*;

/**
 * Created by philipp on 27.04.15.
 * Bounded depth first search
 * This implementation allows to stop the search at any node (when time is up) and continue it from this node.
 * Start search by calling startSearch()
 * Continue search by calling continueSearch()
 */
public class BoundedDFSearch implements TreeSearch {
    private int ABORT_TIME = 1; //ms
    private Types.ACTIONS[] availableActions;
    private boolean finished;

    private SearchNode current;
    private Stack<SearchNode> queue;
    private int maxDepth;

    private Hashtable<Integer,Byte> visited;
    private StateIdentification hashFactory;

    private int count;
    private int maxReachedDepth=0;
    private long timeLeft;
    private long accTime = 0;
    private long avgTime = 0;
    private int iteration = 0;

    public BoundedDFSearch(ArrayList<Types.ACTIONS> availableActions) {
        this.availableActions = availableActions.toArray(new Types.ACTIONS[availableActions.size()]);
        this.hashFactory = new StateIdentification();
    }

    /**
     * * initialises the search
     *
     * @param observation current state of the game
     * @param timer
     * @param searchInterval   and searches until here
     * @return null if no winning path is found, else a winning path
     */
    public ArrayList<Types.ACTIONS> startSearch(StateObservation observation, ElapsedCpuTimer timer, int ... searchInterval) {
        count = 0;
        System.out.println("Started Bounded SearchOld with max Depth: " + maxDepth);
        if(searchInterval.length != 1){
            System.out.println("Wrong Configuration of search interval. Length must be 1 (max Depth)");
        }
        this.maxDepth = searchInterval[0];
        this.visited = new Hashtable<Integer,Byte>();
        this.queue = new Stack<SearchNode>();
        finished = false;
        /*Init Root */
        this.current = new SearchNode(observation,0,new ArrayList<Types.ACTIONS>(),0);
        visited.put(hashFactory.generateHashedState(observation),(byte) current.getDepth());
        /*Add Root to queue */
        queue.add(current);
        return search(timer);
    }
    /**
     * continues the search.
     * startSearch has to be called at least once before this method can be called.
     * @param timer
     * @return null if no winning path is found, else a winning path
     */
        public ArrayList<Types.ACTIONS> continueSearch (ElapsedCpuTimer timer){
            return search(timer);
        }

        private ArrayList<Types.ACTIONS> search (ElapsedCpuTimer timer){
            StateObservation currentState;
            int currentHash;
            int currentDepth;
            StateObservation copy;
            timeLeft = timer.remainingTimeMillis();
            while (!queue.isEmpty()) {
                ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
                // Do only if time for next iteration is left *//*
                if (! (timeLeft > 2*avgTime && timeLeft > ABORT_TIME)) {
                    //System.out.println("Not finished!");
                    return null;
                } else {
                /* pop next node */
                    current = queue.pop();
                    currentDepth = current.getDepth();
                    maxReachedDepth = (currentDepth > maxReachedDepth) ? currentDepth : maxReachedDepth;
                    if (current.getDepth() < maxDepth) {
                        currentState = current.getState();
                    /* expand all */
                        for (int k = 0; k < availableActions.length; k++) {
                            copy = currentState.copy();
                            copy.advance(availableActions[k]);
                        /* if game over state found, abort and return */
                            if (copy.isGameOver() && copy.getGameWinner() == Types.WINNER.PLAYER_WINS) {
                                System.out.println("Found Path, at depth " +(current.getDepth()+1)+" expanded: " + count + " nodes. (Max SearchOld Depth reached: "+ maxReachedDepth + ")");
                                ArrayList<Types.ACTIONS> path = current.getPath();
                                path.add(availableActions[k]);
                                finished = true;
                                return path;
                            } else {
                            /* Depth has to be added to hash or one might miss a solution. This increases number of expanded
                             nodes significantly */
                                currentHash = hashFactory.generateHashedState(copy);
                                ArrayList<Types.ACTIONS> newPath = new ArrayList<Types.ACTIONS>();
                                newPath.addAll(current.getPath());
                                newPath.add(availableActions[k]);
                                if (!visited.containsKey(currentHash) || currentDepth<visited.get(currentHash)) {
                                //    System.out.println(currentDepth);
                                    count++;
                                    visited.put(currentHash,(byte) (currentDepth));
                                    queue.push(new SearchNode(copy, currentDepth + 1, newPath, currentDepth + 1));
                                }
                            }
                        }
                    }
                }
                iteration++;
                accTime += elapsedTimerIteration.elapsedMillis();
                avgTime = accTime / iteration;
                timeLeft = timer.remainingTimeMillis();
            }
            System.out.println("Aborting with nothing found (Depth): " + maxDepth);
            System.out.println("Visited: " + visited.size());
            System.out.println("Expanded: " + count + " nodes");
            finished = true;
            return null;
        }

        /**
         * @return whether current search is finished
         */

    public boolean isFinished() {
        return finished;
    }
    public int getCount() {
        return count;
    }
}

