package shallowThought;


import shallowThought.subagents.olmcts.*;
import shallowThought.subagents.breadthFS.BreadthFS;
import shallowThought.subagents.ga.*;
import shallowThought.subagents.osla.*;

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
    protected File learningRecordFile;
    protected File exercises = new File("./src/shallowThought/learning/exercises.txt");;  // exercises to do
    protected File solutions = new File("./src/shallowThought/learning/solutions.txt");;  // write solutions (= best configs) here
    protected File learning = new File("./src/shallowThought/learning/learning.txt");;  // this is the temporary learning-file, save all necessary information for optimization here
    
    // Writer for the actions file.
    private BufferedWriter writer;

     // Set this variable to FALSE to avoid logging the actions to a file.
    private static final boolean SHOULD_LOG = true;
    private static final boolean LEARNING = true;

	private static final boolean DEBUG = true;
    
    // Different agents
    protected AbstractSubAgent[] subAgents;
    protected AbstractSubAgent chosenAgent;
    protected OLMCTSAgent olmcts;
    protected GAAgent ga;
    protected OSLAAgent osla;
    protected BreadthFS breadthFS;
    
    
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
        breadthFS = new BreadthFS(so, elapsedTimer);
        // list with choosable subagents
        subAgents = new AbstractSubAgent[] {
            olmcts, ga, osla, breadthFS
        };
        // Record-File-Writer:
        this.recordFile = new File("./src/shallowThought/records/test.txt");

        chosenAgent = olmcts;
        if (LEARNING)
        {
        	File cma_temp = new File("./src/shallowThought/offline/cma_temp.txt");
        	Charset charset = Charset.forName("US-ASCII");
        	String line = null;
        	try (BufferedReader reader = Files.newBufferedReader(cma_temp.toPath(), charset)) {
        	    line = reader.readLine();
        	} catch (IOException x) {
        	    System.err.format("IOException: %s%n", x);
        	}
        	if (line == null) {}
        	// Use regex to split config in its components
        	String[] parameters_pre = line.split(":", 0);
        	switch(parameters_pre[0]) {
        	case "olmcts":
        		chosenAgent = olmcts;
        		break;
        	case "ga":
        		chosenAgent = ga;
        		break;
        	case "osla":
        		chosenAgent = osla;
        		break;
        	case "breadthFS":
        		chosenAgent = breadthFS;
        		break;
        	}
        	String[][] parameters = new String[parameters_pre.length-1][4];
        	for (int i = 1; i < parameters_pre.length; i++) {
        		parameters[i] = parameters_pre[i].split(",", 0);
        	}
        	for (String[] para : parameters) {
        		chosenAgent.setParameter(para[0], para[1]);
        	}
        	
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
     * Gets the player the control to draw something on the screen.
     * It can be used for debug purposes.
     * @param g Graphics device to draw to.
     */
    public void draw(Graphics2D g)
    {
    	int half_block = (int) (block_size*0.5);
    	// print chosen agent and parameters
    	g.drawString("chose: "+chosenAgent.getClass().getName(), 20,20);
    	String[] parameters = chosenAgent.getParameterList();
    	for(int i = 0; i < parameters.length; i++)
		{
    		String para = parameters[i];
    		g.drawString(para + "=" + chosenAgent.getParameter(para), 20, 20* (i+2));
		}
    	// if chosen agent = breadthFS, paint all fields that have been visited
    	if (DEBUG && chosenAgent == breadthFS) {
    		for(int j = 0; j < grid[0].length; ++j)
            {
                for(int i = 0; i < grid.length; ++i)
                {
                    if(breadthFS.debug_visited[j][i])
                    {
                        g.drawString("V", i*block_size+half_block,j*block_size+half_block);
                    }
                }
            }
    	}
    	
    	// print sprite id
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
        }
    }
}