package TUDarmstadtTeam2.Search;

import TUDarmstadtTeam2.stateIdentification.StateIdentification;
import TUDarmstadtTeam2.utils.BoundablePriorityQueue;
import TUDarmstadtTeam2.utils.Config;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Hashtable;


/**
 * Created by philipp on 14.05.15.
 */
public class BestFirstSearch{

    private Types.ACTIONS[] availableActions;
    private boolean aborted;
    private boolean finished;

    private SearchNode current;
    private SearchNode currentBestNode;
    private double currentBestScore;
    private BoundablePriorityQueue<SearchNode> queue;
    private int maxDepth;
    // visited list
    private Hashtable<Integer,Byte> visited;
    private StateIdentification hashFactory;
    private NodeComparator comparator;
    // memory handling
    private Runtime runtime;
    private long maxMemory;

    //stats, debugging only
    public int maxReachedDepth=0;
    public int count;
    // timing
    private long timeLeft;
    private long accTime = 0;
    private long avgTime = 0;
    private int iteration = 0;
    private int timingOffset;

    public BestFirstSearch(ArrayList<Types.ACTIONS> availableActions) {
        this.runtime = Runtime.getRuntime();
        this.maxMemory = runtime.maxMemory();
        this.availableActions = availableActions.toArray(new Types.ACTIONS[availableActions.size()]);
        this.hashFactory = new StateIdentification();
        this.visited = new Hashtable<Integer,Byte>();
        count = 0;
    }

    /**
     * * initialises the search
     *
     * @param observation current state of the game
     * @param timer
     * @param maxDepth   and searches until here
     * @return null if no winning path is found, else a winning path
     */
    public ArrayList<Types.ACTIONS> startSearch(StateObservation observation, ElapsedCpuTimer timer, int maxDepth) {
        currentBestNode =null;
        if(Config.PRINT_OUTPUTS) {
            System.out.println("Started Best First Search");
        }
        this.maxDepth = maxDepth;
        //init queue
        this.comparator = new NodeComparator(Config.TIE_BREAK);
        this.queue = new BoundablePriorityQueue<SearchNode>(comparator);
        aborted = false;
        finished = false;
        //init root
        this.current = new SearchNode(observation,0,new ArrayList<Types.ACTIONS>(),0);
        // add Root to queue and visited list
        visited.put(hashFactory.generateHashedState(observation), (byte) current.getDepth());
        queue.add(current);
        timingOffset = 50;
        return search(timer);
    }
    /**
     * continues the search.
     * startSearch has to be called at least once before this method can be called.
     * @param timer
     * @return null if no winning path is found, else a winning path
     */
    public ArrayList<Types.ACTIONS> continueSearch (ElapsedCpuTimer timer){
        timingOffset = 0;
      //  comparator.invertTieBreak();
        return search(timer);
    }

    private ArrayList<Types.ACTIONS> search (ElapsedCpuTimer timer) {
        StateObservation currentState;
        SearchNode newNode;
        double newScore;
        int currentHash;
        int currentDepth;
        StateObservation copy;
        timeLeft = timer.remainingTimeMillis();
        while (!queue.isEmpty()) {
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            // Do only if time for next iteration is left
            if (timeLeft < 2* avgTime  || timeLeft  < (Config.ABORT_TIME + timingOffset)) {
                return null;
            } else {
                // Check for memory use, bound queue if necessary
                if((!queue.isBounded()) && runtime.totalMemory() > Config.MAX_MEMORY_UTILIZATION_FACTOR * maxMemory){
                    queue.boundQueue();
                }
                //poll next node
                current = queue.poll();
                currentDepth = current.getDepth();
                //set maxReachedDepth
                maxReachedDepth = (currentDepth > maxReachedDepth) ? currentDepth : maxReachedDepth;
                if (current.getDepth() < maxDepth) {
                    currentState = current.getState();
                    // expand current Node
                    for (int k = 0; k < availableActions.length; k++) {
                        copy = currentState.copy();
                        copy.advance(availableActions[k]);
                        // if game over state found, abort and return
                        if (copy.isGameOver() && copy.getGameWinner() == Types.WINNER.PLAYER_WINS) {
                            if(Config.PRINT_OUTPUTS) {
                                System.out.println("Found Path, at depth " + (current.getDepth() + 1) + " expanded: " + count + " nodes. (Max SearchOld Depth reached: " + maxReachedDepth + ")");
                            }
                            ArrayList<Types.ACTIONS> path = current.getPath();
                            path.add(availableActions[k]);
                            finished = true;
                            return path;
                        } else {
                            currentHash = hashFactory.generateHashedState(copy);
                            // add only if state is not in visited list or found on smaller depth
                            if (!visited.containsKey(currentHash) || currentDepth<visited.get(currentHash)) {
                                ArrayList<Types.ACTIONS> newPath = new ArrayList<Types.ACTIONS>();
                                newPath.addAll(current.getPath());
                                newPath.add(availableActions[k]);
                                count++;
                                visited.put(currentHash, (byte) (currentDepth));
                                newNode = new SearchNode(copy, currentDepth + 1, newPath, currentDepth + 1);
                                newScore = newNode.getGameScore();
                                if(newNode.getGameScore() > currentBestScore){
                                    currentBestScore =  newScore;
                                    currentBestNode = newNode;
                                }
                                try {
                                    queue.add(newNode);
                                }
                                catch (OutOfMemoryError err){
                                    System.out.println("Out of memory - aborting!");
                                    aborted = true;
                                }

                            }
                        }
                    }
                }
            }
            //timing
            iteration++;
            accTime += elapsedTimerIteration.elapsedMillis();
            avgTime = accTime / iteration;
            timeLeft = timer.remainingTimeMillis();
        }
        if(Config.PRINT_OUTPUTS) {
            System.out.println("Aborting with nothing found (Depth): " + maxDepth);
            System.out.println("Visited: " + visited.size());
            System.out.println("Expanded: " + count + " nodes, reached max Depth: " + maxReachedDepth);
        }
        aborted = true;
        return null;
    }

    /**
     * Returns the path to the state with highest value found.
     * Attention, makes the agent act "greedy"
     * @return
     */
    public ArrayList<Types.ACTIONS> getCurrentBestPath(){
        if (currentBestNode == null){
            return null;
        }
        return currentBestNode.getPath();
    }

    /**
     * Starts next iteration of search
     * @param timer
     * @return
     */
    public ArrayList<Types.ACTIONS>  initNextIteration(ElapsedCpuTimer timer){
        if (currentBestNode == null){
            return null;
        }
        return startSearch(currentBestNode.getState(),timer,maxDepth);
    }


    /**
     * @return whether current search is finished
     */
    public boolean isFinished(){
        return finished;
    }
    public boolean isAborted(){
        return aborted;
    }
}
