package shallowThought.subagents.olmcts;

import shallowThought.AbstractSubAgent;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Random;

public class OLMCTSAgent extends AbstractSubAgent {

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

    // Actions:
    public static int NUM_ACTIONS;
    public static Types.ACTIONS[] actions;
    
    protected SingleMCTSPlayer mctsPlayer;
    public int turnCount = NUM_TURNS;

    
    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public OLMCTSAgent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        //Get the actions in a static array.
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for(int i = 0; i < actions.length; ++i)
        {
            actions[i] = act.get(i);
        }
        NUM_ACTIONS = actions.length;

        //Create the player.
        mctsPlayer = getPlayer(so, elapsedTimer);

        parameterList = new String[]{"MCTS_ITERATIONS", "ROLLOUT_DEPTH", "K", "REWARD_DISCOUNT", "NUM_TURNS"};
        addParameters();
    }
    
    /* Add parameters to the hashtables, semi-hardcoded :| 
     * Fomrat: key - "<parameter name>", value - "<value>" 
     * Datatype: Int, Double
     */
    private void addParameters() {
    	intParameters.put("ROLLOUT_DEPTH", ROLLOUT_DEPTH);
    	intParameters.put("MCTS_ITERATIONS", MCTS_ITERATIONS);
    	intParameters.put("NUM_TURNS", NUM_TURNS);
    	
    	doubleParameters.put("K", K);
    	doubleParameters.put("REWARD_DISCOUNT", REWARD_DISCOUNT);
    }
    
    

    public SingleMCTSPlayer getPlayer(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        return new SingleMCTSPlayer(new Random());
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        //Set the state observation object as the new root of the tree.
    	if (turnCount == NUM_TURNS) {	
    		mctsPlayer.init(stateObs);
    	}
    		
        //Determine the action using MCTS...
        int action = mctsPlayer.run(elapsedTimer);
        
        if (turnCount != NUM_TURNS) {
        	turnCount++;
        	return Types.ACTIONS.ACTION_NIL;
        }

        //... and return it.
		turnCount = 0;

        return actions[action];
    }
    
    
    public String[] getParameterList() {
    	return parameterList;
    }

    public void setParameter(String name, String value) {
    	switch (name) {
    	case "MCTS_ITERATIONS":
    		MCTS_ITERATIONS = new Double(Double.parseDouble(value)).intValue();
    		break;
    	case "ROLLOUT_DEPTH":
    		ROLLOUT_DEPTH = new Double(Double.parseDouble(value)).intValue();
    		break;
    	case "K":
    		K = Double.parseDouble(value);
    		break;
    	case "REWARD_DISCOUNT":
    		REWARD_DISCOUNT = Double.parseDouble(value);
    		break;
    	case "NUM_TURNS":
    		NUM_TURNS = new Double(Double.parseDouble(value)).intValue();
    		turnCount = NUM_TURNS;
    		break;
    	}
    }

    public Number getParameter(String name) {
    	switch (name) {
    	case "MCTS_ITERATIONS":
    		return MCTS_ITERATIONS;
    	case "ROLLOUT_DEPTH":
    		return ROLLOUT_DEPTH;
    	case "K":
    		return K;
    	case "REWARD_DISCOUNT":
    		return REWARD_DISCOUNT;
    	case "NUM_TURNS":
    		return NUM_TURNS;
    	}
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
    	NUM_TURNS = value;
    	turnCount = NUM_TURNS;
    }
    
    public void setDoubleParameter(String name, Double value) {
    	doubleParameters.put(name, value);
    }
}
