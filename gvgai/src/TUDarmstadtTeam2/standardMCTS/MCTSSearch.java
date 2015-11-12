package TUDarmstadtTeam2.standardMCTS;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;


/**
 * MCTS SearchOld
 */
public class MCTSSearch {

    private TreeNode root;
    /*Holds the root of the search */
    private StateObservation rootState;
    /*Holds the state of the node currently considered */
    private StateObservation currentState;

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
        this.rootState = rootState;
        this.root = new TreeNode(null, 0);

        // Variables for timing
		long timeLeft = timer.remainingTimeMillis();
		long accTime = 0;
		long avgTime = 0;
		int iteration = 0;

        //currentNode corresponds to current State - currentState is global since it is needed elsewhere
        TreeNode currentNode;
        //TODO Make timing better? Average over previous runs? (just useful if one run takes more than 1 ms)
        while(timeLeft - Agent.ABORT_TIME > avgTime << 2) {
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
        /* Select best child of root and return corresponding action */
        TreeNode best = null;
        double bestValue = - Double.MAX_VALUE;
        for (TreeNode child: root.getChildren()){
        	//we add noise to choose a child randomly if their UctValue is equal.
        	double noise = random.nextDouble() * epsilon;
            if(child != null && child.getScore() + noise > bestValue){
                best = child;
                bestValue = child.getScore() + noise;
            }
        }
        return availableActions[best.getIndex()];
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
        double bestValue = -Double.MAX_VALUE;
        //Child not null since method is only entered if node fully expanded
        for (TreeNode child : current.getChildren()) {
        	//we add noise to choose a child randomly if their UctValue is equal.
        	double noise = random.nextDouble() * epsilon;
            if (bestValue < child.getUctValue() + noise) {
                bestValue = child.getUctValue() + noise;
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
        int childIndex = - 1;
        do {
            childIndex = random.nextInt(Agent.NUMBEROFACTIONS);
        }while (toExpand.childAt(childIndex));
        TreeNode newChild = new TreeNode(toExpand,childIndex);
        toExpand.addChild(childIndex, newChild);
        currentState.advance(availableActions[childIndex]);
        return newChild;
    }
    /*
    Simulates a game until it is finished or MAX Simulation Depth is reached
     */
    private double simulate(TreeNode node, StateObservation state) {
        /* TODO: Add clever heuristic to bias simulation */
        for(int i = 0; i < Agent.MAX_SIMULATION_DEPTH; i++ ) {
            state.advance(availableActions[random.nextInt(availableActions.length)]);
            if(state.isGameOver()) return state.getGameScore();
        }
        return state.getGameScore();
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
 }