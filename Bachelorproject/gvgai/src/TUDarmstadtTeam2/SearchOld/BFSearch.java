package TUDarmstadtTeam2.SearchOld;

import TUDarmstadtTeam2.Search.SearchNode;
import TUDarmstadtTeam2.stateIdentification.StateIdentification;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by philipp on 30.04.15.
 * Breadth First SearchOld
*/
public class BFSearch implements TreeSearch {

    private int ABORT_TIME = 3; //ms
    private Types.ACTIONS[] availableActions;

    private SearchNode current;
    private ArrayList<SearchNode> queue;

    private StateObservation rootState;

    private HashSet<Integer> visited;
    private StateIdentification hashFactory;

    private boolean solvable = true;
    private boolean memoryAvailableMode = true;

    private Runtime runtime;
    private long maxMemory;
    long timeLeft;
    long accTime = 0;
    long avgTime = 0;
    int iteration = 0;

    private int count;

    public BFSearch(ArrayList<Types.ACTIONS> availableActions) {
        runtime= Runtime.getRuntime();
        maxMemory = runtime.maxMemory();
        System.out.println("Max Meomory: "+ maxMemory/(1024*1024));
        this.availableActions = availableActions.toArray(new Types.ACTIONS[availableActions.size()]);
        this.hashFactory = new StateIdentification();
    }

    /**
     * * initialises the search
     *
     * @param observation current state of the game
     * @param timer
     * @return null if no winning path is found, else a winning path
     */
    public ArrayList<Types.ACTIONS> startSearch(StateObservation observation, ElapsedCpuTimer timer, int ... searchInterval) {
        count = 0;
        //System.out.println("Started BFS");
        /* Init Queue and visited list */
        this.visited = new HashSet<Integer>();
        this.queue = new ArrayList<SearchNode>();
        /*Init Root */
        this.current = new SearchNode(observation,0,new ArrayList<Types.ACTIONS>(),0);
        rootState=observation;
        visited.add(hashFactory.generateHashedState(observation));
        /*Add Root to queue */
        queue.add(current);
        return search(timer);

    }

    /**
     * continues the search.
     * startSearch has to be called at least once before this method can be called.
     *
     * @param timer
     * @return null if no winning path is found, else a winning path
     */
    public ArrayList<Types.ACTIONS> continueSearch(ElapsedCpuTimer timer) {
        return search(timer);
    }
    // TODO Add reasonable behavior to handle memory overuse (or clever way to avoid it in the first place)

    private ArrayList<Types.ACTIONS> search(ElapsedCpuTimer timer) {
        StateObservation currentState, parentState = rootState;
       int currentHash;
        StateObservation copy;
        timeLeft = timer.remainingTimeMillis();
        while (!queue.isEmpty()) {
            // Do only if time for next iteration is left */
            if(!(timeLeft > 2*avgTime && timeLeft > ABORT_TIME)) {
                //System.out.println("Not finished!");
                return null;
            } else {
                ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
                /* pop next node */
                current = queue.remove(0);
                /*get State*/
                currentState = current.getState();
                if ( ! current.hasOwnState()){
                    parentState = currentState;
                    currentState = advanceVia(currentState, current.getPath(), current.getStateDepth());
                }
                /* expand all */
                for (int k = 0; k < availableActions.length; k++) {
                    copy = currentState.copy();
                    copy.advance(availableActions[k]);
                    /* if game over state found, abort and return */
                    if (copy.isGameOver() && copy.getGameWinner() == Types.WINNER.PLAYER_WINS) {
                        System.out.println("Found Path at Depth: " + (current.getDepth() + 1) + ", expanded: " + count + " nodes");
                        Runtime rt = Runtime.getRuntime();
                       // System.out.println("Memory usage: " + (rt.totalMemory() - rt.freeMemory()) + " byte");
                        ArrayList<Types.ACTIONS> path = current.getPath();
                        path.add(availableActions[k]);
                        return path;
                    } else {
                        // Checks whether state has been seen before
                        currentHash = hashFactory.generateHashedState(copy);
                        if (!visited.contains(currentHash)) {
                            //check for memory Overuse
                            if(count % 100 == 0) {
                                long currentMem = runtime.totalMemory();
                                if (currentMem > 0.9 * maxMemory) {
                                    if(runtime.freeMemory() < 500*1024*1024 && memoryAvailableMode) {
                                        System.out.println("switching mode to false");
                                        memoryAvailableMode = false;
                                    }
                                    else {
                                        if(! memoryAvailableMode) {
                                            System.out.println("switching mode to true");
                                            memoryAvailableMode = true;
                                        }
                                    }
                                }
                            }
                            //Adds only if state has not been seen before
                            count++;
                            visited.add(currentHash);
                            ArrayList<Types.ACTIONS> newPath = new ArrayList<Types.ACTIONS>();
                            newPath.addAll(current.getPath());
                            newPath.add(availableActions[k]);
                            SearchNode node;
                            if (memoryAvailableMode) {
                                node = new SearchNode(copy, current.getDepth()+1, newPath, current.getDepth() + 1);
                            }
                            else{
                                node = new SearchNode(current.getState(), current.getStateDepth(), newPath,current.getDepth() +1);
                            }
                            queue.add(node);
                        }
                    }
                }
                iteration++;
                accTime += elapsedTimerIteration.elapsedMillis();
                avgTime = accTime / iteration;
                timeLeft = timer.remainingTimeMillis();
            }
        }
        System.out.println("Aborting: Nothing Found  expanded:" + count + " nodes)");
        solvable = false;
        return null;
    }
    private StateObservation advanceVia(StateObservation state,ArrayList<Types.ACTIONS> path, int from){
        StateObservation retState = state.copy();
        int length = path.size();
       // System.out.print("Advanced via: ");
        for(int i = from; i < length; i++){
            Types.ACTIONS a = path.get(i);
            retState.advance(a);
        //   System.out.print(a+" | ");
        }
      // System.out.println();
        return retState;
    }

    public boolean isSolvable() {
        return solvable;
    }

    public int getCount() {
        return count;
    }
}


