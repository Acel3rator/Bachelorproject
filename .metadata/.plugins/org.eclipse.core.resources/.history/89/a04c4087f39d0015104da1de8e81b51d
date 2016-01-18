package shallowThought.osla;


import shallowThought.AbstractSubAgent;
import shallowThought.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.Hashtable;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class OSLAAgent extends AbstractSubAgent {

	// Dictionaries with the parameters of different types
	private static Hashtable<String, Integer> intParameters = new Hashtable<String, Integer>();  
	private static Hashtable<String, Double> doubleParameters = new Hashtable<String, Double>();
	// List of all parameters
	private static String [] parameterList;
	
	// Parameters:
	public static int MCTS_ITERATIONS = 100;
    public static int ROLLOUT_DEPTH = 10;
    public static double K = Math.sqrt(2);
    public static double REWARD_DISCOUNT = 1.00;
    public static int NUM_TURNS = 3; // how many turns the mcts should run

	
    public OSLAAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

    	
    }

    /**
     *
     * Very simple one step lookahead agent.
     *
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        Types.ACTIONS bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        SimpleStateHeuristic heuristic =  new SimpleStateHeuristic(stateObs);
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {

            StateObservation stCopy = stateObs.copy();
            stCopy.advance(action);
            double Q = heuristic.evaluateState(stCopy);


            //System.out.println("Action:" + action + " score:" + Q);
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }
        }

       // System.out.println("====================");
        return bestAction;
    }
    
    public String[] getParameterList() {
    	return parameterList;
    }
    
    public void setParameter(String name, String value) {
    		
    }

    public Number getParameter(String name) {
    	return null;
    }
    
    public Integer getIntParameter(String name) {
    	return intParameters.get(name);
    }
    
    public Double getDoubleParameter(String name) {
    	return doubleParameters.get(name);
    }
    
    public void setIntParameter(String name, Integer value) {
    	intParameters.put(name, value);
    }
    
    public void setDoubleParameter(String name, Double value) {
    	doubleParameters.put(name, value);
    }
}
