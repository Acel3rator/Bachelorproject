package shallowThought;


import shallowThought.olmcts.*;
import shallowThought.ga.*;
import shallowThought.osla.*;

import ontology.Types;
import tools.ElapsedCpuTimer;

import java.io.BufferedWriter;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

import core.ArcadeMachine;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public class Agent extends AbstractPlayer {

    // Random generator for the agent.
    protected Random randomGenerator;

    // Observation grid.
    protected ArrayList<Observation> grid[][];

    // block size
    protected int block_size;

    // Secretary for logging and reading
    protected Secretary glados = new Secretary();
    
    // File for record
    protected File recordFile;
    protected File exercises = new File("./src/shallowThought/learning/exercises.txt");;  // exercises to do
    protected File solutions = new File("./src/shallowThought/learning/solutions.txt");;  // write solutions (= best configs) here
    protected File learning = new File("./src/shallowThought/learning/learning.txt");;  // this is the temporary learning-file, save all necessary information for optimization here
    
    // Writer for the actions file.
    private BufferedWriter writer;

     // Set this variable to FALSE to avoid logging the actions to a file.
    private static final boolean SHOULD_LOG = true;
    private static final boolean LEARNING = true;
    
    // Different agents
    protected AbstractSubAgent[] subAgents;
    protected AbstractSubAgent chosenAgent;
    protected OLMCTSAgent olmcts;
    protected GAAgent ga;
    protected OSLAAgent osla;
    
    
    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
        grid = so.getObservationGrid();
        block_size = so.getBlockSize();
        // Initialize agents:
        olmcts = new OLMCTSAgent(so, elapsedTimer);
        ga = new GAAgent(so, elapsedTimer);
        osla = new OSLAAgent(so, elapsedTimer);
        // list with choosable subagents
        subAgents = new AbstractSubAgent[] {
            olmcts, ga, osla
        };
        // Record-File-Writer:
        this.recordFile = new File("./src/shallowThought/records/test.txt");

        chosenAgent = olmcts;
        if (LEARNING)
        {
        	// Choose random agent, document choice and level
        	//chosenAgent = subAgents[randomGenerator.nextInt(subAgents.length)];
        	
        	// Configure agent according to exercise:
        	String[] ex = glados.readExercise(this.exercises);  // read file
        	
        	String controller = ex[0];
        	String game = ex[1];
        	String level = ex[2];
        	String[] runs = ex[3].split("/", 0);  // i.e. ["10", "15"] -> already executed "10/15" runs
        	String fixedUncut = ex[4];  // i.e. "MCTS_ITERATIONS=100&NUM_TURNS=20"
        	String[] fixedCut = fixedUncut.split("&", 0); // i.e. ["MCTS_ITERATIONS=100", "NUM_TURNS=20"]
        	String optiUncut = ex[5];  // i.e. "MCTS_ITERATIONS=1to100&NUM_TURNS=2to20"
        	String[] optiCut = ex[5].split("&", 0);  // i.e. ["MCTS_ITERATIONS=1to100", "NUM_TURNS=2to20"]
        	
        	// new exercise session -> clear learning file
        	if (Integer.parseInt(runs[0]) == Integer.parseInt(runs[1])) {
        		System.out.println("Test");
        		glados.clear(learning);
        	}
        	
        	// Create sub-controller
        	noPlayerSpecified: {
        		for(AbstractSubAgent player : subAgents)
        		{
        			if (player.getClass().getName() == controller) {
        				chosenAgent = player;
        				break noPlayerSpecified;
        			}
        		}
        		// TODO: Exception: player in exercises.txt does not exist
        	}
        	
        	// set fixed parameters
        	for(String config : fixedCut)
    		{
        		String[] para = config.split("=", 0);         	
        		chosenAgent.setParameter(para[0], para[1]);
    		}

        	// set unfixed parameters
        	String chosenPara = "";  // for ease of logging
        	String[] guess = guessX(optiCut, runs[0], runs[1]);  // determine next value to be tried for each parameter
    		for(int i = 0; i < optiCut.length; i++)
    		{
    			// this loop assigns each parameter to be optimized the corresponding value of the guess-array
        		String[] para = optiCut[i].split("=", 0);  // i.e. ["NUM_TURNS", "5to20"]         	
        		chosenAgent.setParameter(para[0], guess[i]);  // REMEMBER: guess[i] is a String(Double)
        		chosenPara = chosenPara + para[0] + "=" + guess[i]+ "&";  // for ease of logging  
    		}
    		// cut last "&" (also just ease of logging)
    	    if (chosenPara.length() > 0 && chosenPara.charAt(chosenPara.length()-1)=='&') {
    	        chosenPara = chosenPara.substring(0, chosenPara.length()-1);
    	    }
    		
    		// document choice of parameters and append it to "learning.txt"
    	    glados.writeToFileAppend(learning, controller+":"+game+":"+level+":"
					+(Integer.parseInt(runs[0])-1)+"/" + runs[1] + ":"  // counting executed exercises down one
					+fixedUncut+":"  // fixed parameters
					+chosenPara);  // new parameter choice
    	    glados.writeToFileAppend(learning, "Dummy");  // because score-writing always replaces last line
    	    // count down exercises
    	    glados.writeToFileReplaceFirstLine(exercises, controller+":"+game+":"+level+":"
					+(Integer.parseInt(runs[0])-1)+"/" + runs[1] + ":"  // counting executed exercises down one
					+fixedUncut+":"+optiUncut);
        	
        	System.out.println("chose: "+chosenAgent.getClass().getName());

            glados.writeLevelToFile(recordFile, so);
            glados.writeToFileAppend(recordFile, "Chose "+chosenAgent.getClass().getName());
            glados.writeToFileAppend(recordFile, "Dummy");  // because score-writing always replaces last line
        }
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions();
        ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
        ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
        ArrayList<Observation>[] resourcesPositions = stateObs.getResourcesPositions();
        ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions();
        grid = stateObs.getObservationGrid();

        // TODO: Check for a record that matches this category
        if (LEARNING) {
        	// glados.writeToFileReplaceLastLine(this.recordFile, Double.toString(stateObs.getGameScore()));
        	glados.writeToFileReplaceLastLine(this.learning, Double.toString(stateObs.getGameScore()));
        	
        }
        
        Types.ACTIONS action = null;
        StateObservation stCopy = stateObs.copy();

        ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();
        action = chosenAgent.act(stateObs, elapsedTimer);
                
        return action;
    }
    
    /**
     * This is where the optimizing-magic is supposed to happen. Right now it just splits the range in which to 
     * optimize on to the whole range of numbers we play. This is fair, considering every parameter is equally important
     * Returns a String[] exactly as long as there are elements
     * @param parameters parameters to be optimized
     * @param progress how many rounds of exercise are done yet
     * @param numbersToPlay how many rounds of exercise are there in total
     * @return Parameter settings for each parameter in parameters.
     */
    String[] guessX(String[] parameters, String progress, String numbersToPlay) {
    	int numOfPara = parameters.length;  // number of parameters to be optimized
    	int k = Integer.parseInt(progress);  // what step we're at with the exercise
    	String[] result = new String[numOfPara];
    	double divideRangeBy = Math.pow(Integer.parseInt(numbersToPlay), 1 / numOfPara);  // this is the different configs we can try for each para
    	for (int i = 0; i<numOfPara; i++) {
    		// assign each parameter a value on it's scale
    		String[] para = parameters[i].split("=", 0);
    		String[] range = para[1].split("to", 0);
    		// TODO: only works for doubles right now.
    		double a = Double.parseDouble(range[0]);
    		double b = Double.parseDouble(range[1]);
    		
    		int n = Integer.parseInt(numbersToPlay);
    		result[i] = Double.toString(
    				a + ( (b-a) / n) * k);
    		//result[i] = Double.toString(
    		//		a + ( (b-a) / divideRangeBy ) * 
    		//		Math.floor( (k %(n/ Math.pow(numOfPara, i))) / (n/ Math.pow(numOfPara, i+1))));
    	}
    	return result;
    }
	
    /**
     * Gets the player the control to draw something on the screen.
     * It can be used for debug purposes.
     * @param g Graphics device to draw to.
     */
    public void draw(Graphics2D g)
    {
    	g.drawString("chose: "+chosenAgent.getClass().getName(), 20,20);
    	String[] parameters = chosenAgent.getParameterList();
    	for(int i = 0; i < parameters.length; i++)
		{
    		String para = parameters[i];
    		g.drawString(para + "=" + chosenAgent.getParameter(para), 20, 20* (i+2));
		}
        /*int half_block = (int) (block_size*0.5);
        for(int j = 0; j < grid[0].length; ++j)
        {
            for(int i = 0; i < grid.length; ++i)
            {
                if(grid[i][j].size() > 0)
                {
                    Observation firstObs = grid[i][j].get(0); //grid[i][j].size()-1
                    //Three interesting options:
                    int print = firstObs.category; //firstObs.itype; //firstObs.obsID;
                    g.drawString(print + "", i*block_size+half_block,j*block_size+half_block);
                }
            }
        }*/
    }
}