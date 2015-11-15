package TUDarmstadtTeam2.standardMCTSv2_StateIdentification;

import TUDarmstadtTeam2.stateIdentification.StateIdentification;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


/**
 * MCTS SearchOld
 */
public class MCTSSearch {

    private static final double LOSE_SCORE = -10000000.0;
    private static final double WIN_SCORE =  10000000.0;
    private TreeNode root;
    /*Holds the root of the search */
    private StateObservation rootState;
    /*Holds the state of the node currently considered */
    private StateObservation currentState;

    private StateIdentification hashFactory = new StateIdentification();

    private HashSet<Integer> visited = new HashSet<Integer>();

    private Random random;

    private Types.ACTIONS[] availableActions;

    private double epsilon = 1e-6;

    public MCTSSearch(ArrayList<Types.ACTIONS> availableActions) {
        this.availableActions = availableActions.toArray(new Types.ACTIONS[Agent.NUMBEROFACTIONS]);
        this.random = new Random();
    }

    /**
     * Additional constructor to reproduce results (random is seeded);
     * TODO copy original constructor as soon as implementation is finished
     */
    public MCTSSearch(ArrayList<Types.ACTIONS> availableActions, long seed) {
        this.availableActions = availableActions.toArray(new Types.ACTIONS[Agent.NUMBEROFACTIONS]);
        this.random = new Random(seed);
    }

    /** the search Algorithm - called at the beginning of every move
     * @param rootState the current state of the actual game
     * @param timer
     */
    public Types.ACTIONS search(StateObservation rootState, ElapsedCpuTimer timer){
        visited.clear();
        this.rootState = rootState;
        visited.add(hashFactory.generateHashedState(rootState));
        this.root = new TreeNode(null, 0,0);

        // Variables for timing
        long timeLeft = timer.remainingTimeMillis();
        long accTime = 0;
        long avgTime = 0;
        int iteration = 0;

        //currentNode corresponds to current State - currentState is global since it is needed elsewhere
        TreeNode currentNode;
        //TODO Make timing better? Average over previous runs? (just useful if one run takes more than 1 ms)
        while(timeLeft > 2*avgTime && timeLeft > Agent.ABORT_TIME) {
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            // Select next node to expand and expand it
            currentNode = treePolicy();
            // run simulation
            double value = simulate(currentNode,currentState);
            // back propagate result of simulation
            propagate(currentNode, value);
            //timing
            iteration++;
            accTime += elapsedTimerIteration.elapsedMillis();
            avgTime = accTime / iteration;
            timeLeft = timer.remainingTimeMillis();
        }
        //System.out.println("iterations: "+iteration);
        /* Select best child of root and return corresponding action */
        return availableActions[getChildIndex()];

    }

    /* navigates trough tree until leave is reached */
    private TreeNode treePolicy() {
        TreeNode selected = root;
        currentState = rootState.copy();
        int rolloutCount = 0;
        while (! currentState.isGameOver() && rolloutCount <= Agent.MAX_ROLLOUT_DEPTH) {
            rolloutCount++;
            // if current node is not fully expanded, expand it
            /*TODO Currently ALL children need to be expanded, including those who are obviously bad
              (redo last move? instant loss?...)
             */
            if(! selected.fullyExpanded()){
                return expand(selected);
            }
            // else select next node in tree
            else{
                selected = selectNext(selected);
                currentState.advance(availableActions[selected.getIndex()]);
            }
        }
        return selected;
    }
    /* the next node is the child with the highest uct value */
    private TreeNode selectNext(TreeNode current){
        double bestValue = - Double.MAX_VALUE;
        double childValue;
        //Child not null since method is only entered if node fully expanded
        for (TreeNode child : current.getChildren()) {
            //we add noise to choose a child randomly if their UctValue is equal.
            childValue = child.getUctValue();
            childValue = noise(childValue);
            if (bestValue < childValue) {
                bestValue = childValue;
                current = child;
            }
        }
        return current;
    }
    /*
    Expands a node
    */
    private TreeNode expand(TreeNode toExpand) {
        /*TODO: Add clever heuristic to chose Child */
        int childIndex=-1;
        int currentBest = Integer.MIN_VALUE;
        for(int i = 0; i < Agent.NUMBEROFACTIONS; i++){
            int x = random.nextInt();
            if (x > currentBest && !toExpand.childAt(i)) {
                childIndex = i;
                currentBest = x;
            }
        }
        TreeNode newChild = new TreeNode(toExpand,childIndex,toExpand.getDepth() + 1);
        toExpand.addChild(childIndex, newChild);
        currentState.advance(availableActions[childIndex]);
        return newChild;
    }
    /*
    Simulates a game until it is finished or MAX Simulation Depth is reached
     */
    private double simulate(TreeNode node, StateObservation state) {
        /* TODO: Add clever heuristic to bias simulation */
        StateObservation temp;
        for(int i = node.getDepth(); i < Agent.MAX_ROLLOUT_DEPTH + 1; i++ ) {
            do {
                temp = state.copy();
                temp.advance(availableActions[random.nextInt(Agent.NUMBEROFACTIONS)]);
            } while (visited.contains(hashFactory.generateHashedState(temp)));
            state = temp;
            if(state.isGameOver()) break;
        }
        double score = state.getGameScore();
        if(state.isGameOver()){
            if(state.getGameWinner() == Types.WINNER.PLAYER_WINS){
                score += WIN_SCORE;
            }
            if(state.getGameWinner() == Types.WINNER.PLAYER_LOSES){
                score += LOSE_SCORE;
            }
        }
        return score;
    }
    /*
    Navigates to tree (from bottom to top) until root is reached.
    Updates value in each node.
     */
    private void propagate(TreeNode node, double value) {
        while(node != null) {
            node.updateScore(value);
            node = node.getParent();
        }
    }
    private int getChildIndex(){
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;
        TreeNode[] rootChildren = root.getChildren();
        for (int i=0; i<rootChildren.length; i++) {
            if(rootChildren[i] != null) {
                double childValue = rootChildren[i].getScore() / (rootChildren[i].getNrVisited() + epsilon);
                childValue = noise(childValue);
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }
        return selected;
    }
    private double noise(double input)
    {
            return (input + epsilon) * (1.0 + epsilon * (random.nextDouble() - 0.5));
    }

}