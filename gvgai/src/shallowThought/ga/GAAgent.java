package shallowThought.ga;


import shallowThought.Heuristics.StateHeuristic;
import shallowThought.Heuristics.WinScoreHeuristic;
import core.game.StateObservation;
import shallowThought.AbstractSubAgent;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;

import java.awt.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 26/02/14
 * Time: 15:17
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class GAAgent extends AbstractSubAgent {

	// Dictionaries with the parameters of different types
	private static Hashtable<String, Integer> intParameters = new Hashtable<String, Integer>();  
	private static Hashtable<String, Double> doubleParameters = new Hashtable<String, Double>();
	private static Hashtable<String, Long> longParameters = new Hashtable<String, Long>();
	// List of all parameters
	private static String [] parameterList;
	
	// parameters:
    private static double GAMMA = 0.90;
    private static int SIMULATION_DEPTH = 7;
    private static int POPULATION_SIZE = 5;
    private static double RECPROB = 0.1;
    private double MUT = (1.0 / SIMULATION_DEPTH);
    private static long BREAK_MS = 35;
    
    private ElapsedCpuTimer timer;

    private int genome[][][];
    private final int N_ACTIONS;
    private final HashMap<Integer, Types.ACTIONS> action_mapping;
    private final HashMap<Types.ACTIONS, Integer> r_action_mapping;
    protected Random randomGenerator;

    private int numSimulations;
    

    /**
     * Public constructor with state observation and time due.
     *
     * @param stateObs     state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public GAAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        randomGenerator = new Random();

        action_mapping = new HashMap<Integer, Types.ACTIONS>();
        r_action_mapping = new HashMap<Types.ACTIONS, Integer>();
        int i = 0;
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {
            action_mapping.put(i, action);
            r_action_mapping.put(action, i);
            i++;
        }
        
        parameterList = new String[]{"GAMMA", "SIMULATION_DEPTH", "POPULATION_SIZE", "RECPROB", "MUT", "BREAK_MS"};
        addParameters();
        N_ACTIONS = stateObs.getAvailableActions().size();
        initGenome(stateObs);
        
    }
    
    
    /* Add parameters to the hashtable, hardcoded :( 
     * Fomrat: key - "<parameter name>", value - "<value> - <Datatype>" 
     * Datatype: Int, Double, Long
     */
    private void addParameters() {
    	intParameters.put("SIMULATION_DEPTH", SIMULATION_DEPTH);
    	intParameters.put("POPULATION_SIZE", POPULATION_SIZE);
    	    	
    	doubleParameters.put("GAMMA", GAMMA);
    	doubleParameters.put("RECPROB", RECPROB);
    	doubleParameters.put("MUT", MUT);
    	
    	longParameters.put("BREAK_MS", BREAK_MS);
    }


    double microbial_tournament(int[][] actionGenome, StateObservation stateObs, StateHeuristic heuristic) throws TimeoutException {
        int a, b, c, W, L;
        int i;


        a = (int) ((POPULATION_SIZE - 1) * randomGenerator.nextDouble());
        do {
            b = (int) ((POPULATION_SIZE - 1) * randomGenerator.nextDouble());
        } while (a == b);

        double score_a = simulate(stateObs, heuristic, actionGenome[a]);
        double score_b = simulate(stateObs, heuristic, actionGenome[b]);

        if (score_a > score_b) {
            W = a;
            L = b;
        } else {
            W = b;
            L = a;
        }

        int LEN = actionGenome[0].length;

        for (i = 0; i < LEN; i++) {
            if (randomGenerator.nextDouble() < RECPROB) {
                actionGenome[L][i] = actionGenome[W][i];
            }
        }


        for (i = 0; i < LEN; i++) {
            if (randomGenerator.nextDouble() < MUT) actionGenome[L][i] = randomGenerator.nextInt(N_ACTIONS);
        }

        return Math.max(score_a, score_b);

    }

    private void initGenome(StateObservation stateObs) {

        genome = new int[N_ACTIONS][POPULATION_SIZE][SIMULATION_DEPTH];


        // Randomize initial genome
        for (int i = 0; i < genome.length; i++) {
            for (int j = 0; j < genome[i].length; j++) {
                for (int k = 0; k < genome[i][j].length; k++) {
                    genome[i][j][k] = randomGenerator.nextInt(N_ACTIONS);
                }
            }
        }
    }


    private double simulate(StateObservation stateObs, StateHeuristic heuristic, int[] policy) throws TimeoutException {


        //System.out.println("depth" + depth);
        long remaining = timer.remainingTimeMillis();
        if (remaining < BREAK_MS) {
            //System.out.println(remaining);
            throw new TimeoutException("Timeout");
        }


        int depth = 0;
        stateObs = stateObs.copy();
        for (; depth < policy.length; depth++) {
            Types.ACTIONS action = action_mapping.get(policy[depth]);

            stateObs.advance(action);

            if (stateObs.isGameOver()) {
                break;
            }
        }

        numSimulations++;
        double score = Math.pow(GAMMA, depth) * heuristic.evaluateState(stateObs);
        return score;


    }

    private Types.ACTIONS microbial(StateObservation stateObs, int maxdepth, StateHeuristic heuristic, int iterations) {

        double[] maxScores = new double[stateObs.getAvailableActions().size()];

        for (int i = 0; i < maxScores.length; i++) {
            maxScores[i] = Double.NEGATIVE_INFINITY;
        }


        outerloop:
        for (int i = 0; i < iterations; i++) {
            for (Types.ACTIONS action : stateObs.getAvailableActions()) {


                StateObservation stCopy = stateObs.copy();
                stCopy.advance(action);

                double score = 0;
                try {
                    score = microbial_tournament(genome[r_action_mapping.get(action)], stCopy, heuristic) + randomGenerator.nextDouble()*0.00001;
                } catch (TimeoutException e) {
                    break outerloop;
                }
                int int_act = this.r_action_mapping.get(action);

                if (score > maxScores[int_act]) {
                    maxScores[int_act] = score;
                }


            }
        }

        Types.ACTIONS maxAction = this.action_mapping.get(Utils.argmax(maxScores));


        return maxAction;

    }

    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     *
     * @param stateObs     Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        this.timer = elapsedTimer;
        numSimulations = 0;

        Types.ACTIONS lastGoodAction = microbial(stateObs, SIMULATION_DEPTH, new WinScoreHeuristic(stateObs), 100);

        return lastGoodAction;
    }

    
    public String[] getParameterList() {
    	return parameterList;
    }
    
    /**
     * Setter for parameters, parses input values that are strings to the value types     * 
     * @param name: name of parameter to set
     * @param value: value of parameter as string 
     */
    public void setParameter(String name, String value) {
      	switch (name) {
       	case "SIMULATION_DEPTH":
       		SIMULATION_DEPTH = new Double(Double.parseDouble(value)).intValue();
       		break;
       	case "POPULATION_SIZE":
       		POPULATION_SIZE = new Double(Double.parseDouble(value)).intValue();
       		break;
       	case "GAMMA":
       		GAMMA = Double.parseDouble(value);
       		break;
       	case "RECPROB":
       		RECPROB = Double.parseDouble(value);
       		break;
       	case "MUT":
       		MUT = Double.parseDouble(value);
       		break;
       	case "BREAK_MS":
       		BREAK_MS = Long.parseLong(value);
       		break;
      	}
    }
    
    /**
     * Getter for parameters
     * @param
     * @return parameter value as number
     */
    public Number getParameter(String name) {
      	switch (name) {
       	case "SIMULATION_DEPTH":
       		return SIMULATION_DEPTH;
       	case "POPULATION_SIZE":
       		return POPULATION_SIZE;
       	case "GAMMA":
       		return GAMMA;
       	case "RECPROB":
       		return RECPROB;
       	case "MUT":
       		return MUT;
       	case "BREAK_MS":
       		return BREAK_MS;
    	}
      	return null;
    }
    
    public Integer getIntParameter(String name) {
    	return intParameters.get(name);
    }
    
    public Double getDoubleParameter(String name) {
    	return doubleParameters.get(name);
    }
    
    public Long getLongParameter(String name) {
    	return longParameters.get(name);
    }
    
    public void setIntParameter(String name, Integer value) {
    	intParameters.put(name, value);
    }
    
    public void setDoubleParameter(String name, Double value) {
    	doubleParameters.put(name, value);
    }
    
    public void setLongParameter(String name, Long value) {
    	longParameters.put(name, value);
    }

    @Override
    public void draw(Graphics2D g)
    {
        //g.drawString("Num Simulations: " + numSimulations, 10, 20);
    }
}
