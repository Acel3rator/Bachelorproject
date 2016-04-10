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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.data.norm.MaxNormalizer;
import org.neuroph.util.data.norm.Normalizer;

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
    
    // Writer for the actions file.
    private BufferedWriter writer;

     // Set this variable to FALSE to avoid logging the actions to a file.
    private static final boolean LEARNING = false;

	private static final boolean DEBUG = true;
    
    // Different agents
    protected String[] agents;
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
        
        agents = new String[]{
         		"YOLOBOT.agent", "YBCriber.agent", "TUDarmstadtTeam2.agent", "SJA862.agent",
           		"NovTea.agent", "MH2015.agent", "alxio.agent", "olmcts", "ga", "osla", "breathFS"};
        
        // Record-File-Writer:
        this.recordFile = new File("./src/shallowThought/records/test.txt");

        chosenAgent = breadthFS;
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
        	String[][] parameters = new String[parameters_pre.length-1][2];
        	for (int i = 1; i < parameters_pre.length; i++) {
        		parameters[i-1] = parameters_pre[i].split(",", 0);
        	}
        	for (String[] para : parameters) {
        		chosenAgent.setParameter(para[0], para[1]);
        	}
        	
        	System.out.println("chose: "+chosenAgent.getClass().getName());
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
        	// glados.writeToFileReplaceLastLine(this.learning, Double.toString(stateObs.getGameScore()));
        	
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