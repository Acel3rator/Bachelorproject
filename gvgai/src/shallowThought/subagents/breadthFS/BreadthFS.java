package shallowThought.subagents.breadthFS;

import shallowThought.AbstractSubAgent;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class BreadthFS extends AbstractSubAgent {

	private static final boolean DEBUG = true;
	// Dictionaries with the parameters of different types
	private static Hashtable<String, Integer> intParameters = new Hashtable<String, Integer>();  
	private static Hashtable<String, Double> doubleParameters = new Hashtable<String, Double>();
	// List of all parameters
	private static String [] parameterList;
	
	// Parameters:
	int MAX_DEPTH = 1000;
	int BALANCE_TIME_MEMORY = 10;  // state is copied every ten depths -> we cannot copy every step because of memory, but we cannot build it anew all the time either because of time limitation
	
    // Actions:
    public static int NUM_ACTIONS;
    public static Types.ACTIONS[] actions;
    
    //
    boolean firstMove = true;
    boolean returnActions = false;
    Stack<Integer> actionList;
    SingleTreeNode root = null;
    SingleTreeNode current = null;
    Queue<SingleTreeNode> search = new LinkedList<SingleTreeNode>();
    Set<Long> seen = new HashSet<Long>();
    public Random m_rnd;
	public boolean[][] debug_visited;

    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public BreadthFS(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        //Get the actions in a static array.
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for(int i = 0; i < actions.length; ++i)
        {
            actions[i] = act.get(i);
        }
        NUM_ACTIONS = actions.length;

        parameterList = new String[]{"MAX_DEPTH", "BALANCE_TIME_MEMORY"};
        addParameters();
        
        // debug:
        debug_visited = new boolean[so.getObservationGrid()[0].length][so.getObservationGrid().length];
    }
    
    /* Add parameters to the hashtables, semi-hardcoded :| 
     * Fomrat: key - "<parameter name>", value - "<value>" 
     * Datatype: Int, Double
     */
    private void addParameters() {
    	// TODO this method is not safe
    	intParameters.put("MAX_DEPTH", MAX_DEPTH);
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        double avgTimeTaken = 0;
        double acumTimeTaken = 0;
    	long remaining = elapsedTimer.remainingTimeMillis();
        int numIters = 0;

        int remainingLimit = 5;

        if (returnActions) {
        	if (actionList.isEmpty()) {
        		returnActions = false;
        		return Types.ACTIONS.ACTION_NIL;  // ERROR ERROR ERROR (we should have won by now => nondeterministic or timedependet)
        	}
        	Types.ACTIONS action = actions[actionList.pop()];
        	return action;
        }
        
        // TODO ignoreChange
        if (firstMove) {
        	System.out.println("first move activated");
        	root = new SingleTreeNode(stateObs.copy(), m_rnd);
        	search.add(root);
        	current = root;
        	firstMove = false;
        }
        
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit){
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();

            // search starts here
            if (search.isEmpty()) {
            	System.out.println("breadthfirstsearch list is empty :(");
            	break;
            	
            	// TODO exception of some kind, no solution found
            }
            //System.out.println(search);
            current = search.remove();
            
            StateObservation s = current.state();
            
            if (DEBUG) {
            	debug_visited[new Double(s.getAvatarPosition().y).intValue()/s.getBlockSize()][new Double(s.getAvatarPosition().x).intValue()/s.getBlockSize()] = true;
            }

            if (gameOver(s)) {  // we lost
            	System.out.println("lost");
            	continue;
            }

            if (stop(s)) {  // we won
            	System.out.println("won");
            	returnActions = true;
            	createActionList(current);  // possible timeout?
            }

            if (seen(s)) {  // this state has been seen earlier
            	//System.out.println("seen");
            	continue;
            }
            
            // else: add all children
            for (SingleTreeNode child : current.expand()) {
        //    System.out.println("new child");
            	search.add(child);
            }
            
            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
            //System.out.println(elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + ")");
            avgTimeTaken  = acumTimeTaken/numIters;
            remaining = elapsedTimer.remainingTimeMillis();
        }
     //   if (DEBUG) System.out.println("Remaining: "+remaining);
        if (DEBUG && remaining == -22) System.out.println(current.m_depth);
        return Types.ACTIONS.ACTION_NIL;
    }
    
    /**
     * called, when we are done with the search -> creates the stack "actionList" (global variable) from which we then can draw the desired path within the search.
     * @param current
     */
    private void createActionList(SingleTreeNode current) {
		actionList = new Stack<Integer>();
    	while (current.childIdx != -1) {
    		actionList.push(current.childIdx);
    		current = current.parent;
    	}
	}

    /**
     * returns true if we have lost the game
     * @param s
     * @return
     */
	private boolean gameOver(StateObservation s) {
		if (s.isGameOver() && s.getGameWinner() != Types.WINNER.PLAYER_WINS) {
			return true;
		}
		return false;
	}

	/**
	 * returns true if we have won
	 * TODO maybe expand on true if we reach a higher score?
	 * @param s
	 * @return
	 */
	private boolean stop(StateObservation s) {
		if (s.isGameOver() && s.getGameWinner() == Types.WINNER.PLAYER_WINS) {
			return true;
		}
		return false;
	}

	/**
	 * returns true if we have seen this state before somewhere in the search. ignores npcs.
	 * also, it adds s to the seen states
	 * @param s
	 * @return
	 */
	private boolean seen(StateObservation s) {
		//  check for equality with any of the state before
		long hash = hash(s, true);
		if (seen.contains(hash)) {
			return true;
		}
		seen.add(hash);
		return false;
	}

	/**
	 * Maps state to long, pretty hashable
	 * Stolen from yolobot, cause yolo! :)
	 * @param so
	 * @param ignoreNPCs
	 * @return hash value for a StateObservation
	 */
	private long hash(StateObservation so, boolean ignoreNPCs) {
		long prime = 31;

		long result = 17;
		result = result * prime + Double.doubleToLongBits(so.getAvatarPosition().x);
		result = result * prime + Double.doubleToLongBits(so.getAvatarPosition().y);
		//result = result * prime + so.getAvatarType();
		result = result * prime + Double.doubleToLongBits(so.getAvatarOrientation().x);
		result = result * prime + Double.doubleToLongBits(so.getAvatarOrientation().y);
		result = result * prime + Double.doubleToLongBits(so.getAvatarSpeed());
		

		for (int i = 0; i < so.getObservationGrid().length; i++) {
			result = result * prime + i;
			for (int j = 0; j < so.getObservationGrid()[i].length; j++) {
				result = result * prime + j;
				for (Observation obs : so.getObservationGrid()[i][j]) {
					if (obs.category != Types.TYPE_AVATAR
							&& (obs.category != Types.TYPE_NPC || !ignoreNPCs)) {
						result = result * prime + obs.obsID;
						result = result * prime + obs.itype;
					}
				}
			}
		}

		HashMap<Integer, Integer> inventory = so.getAvatarResources();
		for (int itemId : inventory.keySet()) {
			result = result * prime + itemId;
			result = result * prime + inventory.get(itemId);
		}

		return result;
	}
	
	public String[] getParameterList() {
    	return parameterList;
    }

    public void setParameter(String name, String value) {
    	switch (name) {
    	case "MAX_DEPTH":
    		MAX_DEPTH = new Double(Double.parseDouble(value)).intValue();
    		break;
    	case "BALANCE_TIME_MEMORY":
    		BALANCE_TIME_MEMORY = new Double(Double.parseDouble(value)).intValue();
			break;
    	}
    }
    
    public Number getParameter(String name) {
    	switch (name) {
    	case "MAX_DEPTH":
    		return MAX_DEPTH;
    	case "BALANCE_TIME_MEMORY":
    		return BALANCE_TIME_MEMORY;
		}
    	return null;
    }   
}