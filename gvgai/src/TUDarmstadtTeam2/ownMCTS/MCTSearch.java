package TUDarmstadtTeam2.ownMCTS;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 07/11/13
 * Time: 17:13
 */
public class MCTSearch
{
    private static final double HUGE_NEGATIVE = -10000000.0;
    private static final double HUGE_POSITIVE =  10000000.0;
    private static double epsilon = 1e-6;
    private static double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};


    /**
     * Root of the tree.
     */
    private TreeNode root;
    private StateObservation rootState;

    /**
     * Random generator.
     */
    private Random m_rnd;



    public MCTSearch(Random a_rnd)
    {
        m_rnd = a_rnd;
    }

    /**
     * Inits the tree with the new observation state in the root.
     * @param a_gameState current state of the game.
     */
    public void init(StateObservation a_gameState)
    {
        //Set the game observation to a newly root node.
        //System.out.println("learning_style = " + learning_style);
        root = new TreeNode(null,0,0);
        rootState = a_gameState;
    }

    /**
     * Runs MCTS to decide the action to take. It does not reset the tree.
     * @param elapsedTimer Timer when the action returned is due.
     * @return the action to execute in the game.
     */
    public int run(ElapsedCpuTimer elapsedTimer)
    {
        //Do the search within the available time.
        mctsSearch(elapsedTimer);

        //Determine the best action to take and return it.
        //int action = mostVisitedAction();
        int action = bestAction();
        return action;
    }
    private void mctsSearch(ElapsedCpuTimer elapsedTimer) {

        double avgTimeTaken = 0;
        double acumTimeTaken = 0;
        long remaining = elapsedTimer.remainingTimeMillis();
        int numIters = 0;

        int remainingLimit = 5;
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit){
            //while(numIters < Agent.MCTS_ITERATIONS){

            StateObservation state = rootState.copy();

            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            TreeNode selected = treePolicy(state);
            double delta = rollOut(selected,state);
            backUp(selected, delta);

            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
            //System.out.println(elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + ")");
            avgTimeTaken  = acumTimeTaken/numIters;
            remaining = elapsedTimer.remainingTimeMillis();
        }
        // System.out.println("iterations:" +numIters);
    }

    public TreeNode treePolicy(StateObservation state) {

        TreeNode currentNode = root;

        while (!state.isGameOver() && currentNode.getDepth() < controllers.sampleOLMCTS.Agent.ROLLOUT_DEPTH)
        {
            if ( ! currentNode.fullyExpanded()) {
                return expand(currentNode, state);
            } else {
                TreeNode next = uct(currentNode,state);
                currentNode = next;
            }
        }
        return currentNode;
    }
    public TreeNode expand(TreeNode currentNode, StateObservation state) {
        int bestAction = 0;
        double bestValue = -1;

        for (int i = 0; i < Agent.NUMBEROFACTIONS; i++) {
            double x = m_rnd.nextDouble();
            if (x > bestValue &&  ! currentNode.childAt(i)) {
                bestAction = i;
                bestValue = x;
            }
        }
        //Roll the state
        state.advance(Agent.actions[bestAction]);
        //System.out.println("Expanded Node with action: " + Agent.actions[bestAction] + "in: "+state.getGameTick());
        TreeNode newChild = new TreeNode(currentNode,bestAction,currentNode.getDepth()+1);
        currentNode.addChild(bestAction,newChild);
        return newChild;
    }
    public TreeNode uct(TreeNode currentNode, StateObservation state) {

        TreeNode selected = null;
        double bestValue = -Double.MAX_VALUE;
        for (TreeNode child : currentNode.getChildren())
        {
            double hvVal = child.getScore();
            double childValue =  hvVal / (child.getNrVisited() + epsilon);

            childValue = Utils.normalise(childValue, bounds[0], bounds[1]);
            //System.out.println("norm child value: " + childValue);

            double uctValue = childValue +
                    Agent.C * Math.sqrt(Math.log(currentNode.getNrVisited() + 1) / (child.getNrVisited() + epsilon));

            uctValue = Utils.noise(uctValue, epsilon, this.m_rnd.nextDouble());     //break ties randomly

            // small sampleRandom numbers: break ties in unexpanded nodes
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }
        if (selected == null)
        {
            throw new RuntimeException("Warning! returning null: " + bestValue + " : " + currentNode.getChildren().length + " " +
                    + bounds[0] + " " +bounds[1]);
        }

        //Roll the state:
        state.advance(Agent.actions[selected.getIndex()]);

        return selected;
    }
    public double rollOut(TreeNode currentNode, StateObservation state)
    {
        int thisDepth = currentNode.getDepth();

        while (!finishRollout(state, thisDepth)) {

            int action = m_rnd.nextInt(Agent.NUMBEROFACTIONS);
            state.advance(Agent.actions[action]);
            thisDepth++;
        }


        double delta = value(state);

        if(delta < bounds[0])
            bounds[0] = delta;
        if(delta > bounds[1])
            bounds[1] = delta;

        //double normDelta = Utils.normalise(delta ,lastBounds[0], lastBounds[1]);

        return delta;
    }
    public double value(StateObservation a_gameState) {

        boolean gameOver = a_gameState.isGameOver();
        Types.WINNER win = a_gameState.getGameWinner();
        double rawScore = a_gameState.getGameScore();

        if(gameOver && win == Types.WINNER.PLAYER_LOSES)
            rawScore += HUGE_NEGATIVE;

        if(gameOver && win == Types.WINNER.PLAYER_WINS)
            rawScore += HUGE_POSITIVE;

        return rawScore;
    }
    public boolean finishRollout(StateObservation state, int depth)
    {
        if(depth >= Agent.ROLLOUT_DEPTH)      //rollout end condition.
            return true;

        if(state.isGameOver())               //end of game
            return true;

        return false;
    }
    public void backUp(TreeNode currentNode, double result)
    {
        TreeNode n = currentNode;
        while(n != null)
        {
            n.updateScore(result);
            n = n.getParent();
        }
    }


    public int mostVisitedAction() {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;
        boolean allEqual = true;
        double first = -1;
        TreeNode[] rootChildren = root.getChildren();

        for (int i=0; i<rootChildren.length; i++) {

            if(rootChildren[i] != null)
            {
                if(first == -1)
                    first = rootChildren[i].getNrVisited();
                else if(first != rootChildren[i].getNrVisited())
                {
                    allEqual = false;
                }

                double childValue = rootChildren[i].getNrVisited();
                childValue = Utils.noise(childValue, epsilon, this.m_rnd.nextDouble());     //break ties randomly
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }

        if (selected == -1)
        {
            System.out.println("Unexpected selection!");
            selected = 0;
        }else if(allEqual)
        {
            //If all are equal, we opt to choose for the one with the best Q.
            selected = bestAction();
        }
        return selected;
    }

    public int bestAction()
    {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;
        TreeNode[] rootChildren = root.getChildren();
        //System.out.println(rootChildren.length);

        for (int i=0; i<rootChildren.length; i++) {

            if(rootChildren[i] != null) {
                //double tieBreaker = m_rnd.nextDouble() * epsilon;
                double childValue = rootChildren[i].getScore() / (rootChildren[i].getNrVisited() + epsilon);
                childValue = Utils.noise(childValue, epsilon, this.m_rnd.nextDouble());     //break ties randomly
          //      System.out.print(childValue+" | ");
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }
       // System.out.println();

        if (selected == -1)
        {
            System.out.println("Unexpected selection!");
            selected = 0;
        }

        return selected;
    }
}
