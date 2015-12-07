package shallowThought.olmcts;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

public class OLMCTSAgent extends AbstractPlayer {

	// Parameters:
	public static int MCTS_ITERATIONS = 100;
    public static int ROLLOUT_DEPTH = 10;
    public static double K = Math.sqrt(2);
    public static double REWARD_DISCOUNT = 1.00;
    public static int NUM_TURNS = 3; // how many turns the mcts should run
    
    // Actions:
    public static int NUM_ACTIONS;
    public static Types.ACTIONS[] actions;
    
    int turnCount = NUM_TURNS;

    
    protected SingleMCTSPlayer mctsPlayer;

    
    
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

}
