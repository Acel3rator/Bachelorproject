package shallowThought;


import shallowThought.olmcts.*;
import shallowThought.ga.*;
import shallowThought.osla.*;

import ontology.Types;
import tools.ElapsedCpuTimer;

import java.io.BufferedWriter;
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
    protected AbstractPlayer[] subAgents;
    protected AbstractPlayer chosenAgent;
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
        subAgents = new AbstractPlayer[] {
            olmcts, ga, osla
        };
        // Record-File-Writer:
        this.recordFile = new File("./src/shallowThought/records/test.txt");
        
        if (LEARNING)
        {
        	// Choose random agent, document choice and level
        	//chosenAgent = subAgents[randomGenerator.nextInt(subAgents.length)];
        	//chosenAgent = olmcts;
        	
        	// Configure agent according to exercise:
        	String[] ex = glados.readExercise(this.exercises);
        	// Create sub-controller
        	noPlayerSpecified: {
        		for(AbstractPlayer player : subAgents)
        		{
        			if (player.getClass().getName() == ex[0]) {
        				chosenAgent = player;
        				break noPlayerSpecified;
        			}
        		}
        		// TODO: Exception: player in exercises.txt does not exist
        	}
        	// TODO: set fixed parameters
        	// TODO: find setting to be tried for exercise next
        	
        	glados.writeToFileAppend(learning, ex[0]+"-"+ex[1]+"-"+ex[2]+"-"+ex[3]+"-"+ex[4]+"-"+ex[5]);
        	
        	System.out.println("chose: "+chosenAgent.getClass().getName());
            writeLevelToFile(so);
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

        //ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
        ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();
           
        action = chosenAgent.act(stateObs, elapsedTimer);
        //action = ga.act(stateObs, elapsedTimer);
        
        return action;
    }
    
	void writeLevelToFile(StateObservation so) {
    	try {
        	if(SHOULD_LOG) {
        		// parameters:
        		boolean logEverything = true;
        		if(!this.recordFile.exists())
        		{
        			this.recordFile.createNewFile();
        		}
        		// create an APPENDING writer
        		writer = new BufferedWriter(new FileWriter(this.recordFile, true));
        		if (logEverything) {
        			// write npcs:
        		writer.write("NPC:(");
            	if (so.getNPCPositions() != null) {
            	  ArrayList<Observation>[] list = so.getNPCPositions();
            	  for (int i=0; i<list.length; i++) {
            		  for (int j=0; j<list[i].size(); j++) {
            			  if (list[i] != null) {
            				  writer.write("(" + list[i].get(j).itype + ",");
            				  writer.write(list[i].get(j).position.toString()+")");
            			  }
            		  }
            	  }
            	}
            	writer.write("),");
            	// write immovables
            	writer.write("Immovable:(");
            	if (so.getImmovablePositions() != null) {
            	  ArrayList<Observation>[] list = so.getImmovablePositions();
            	  for (int i=0; i<list.length; i++) {
            		  for (int j=0; j<list[i].size(); j++) {
            			  if (list[i] != null) {
            				  writer.write("(" + list[i].get(j).itype + ",");
            				  writer.write(list[i].get(j).position.toString()+")");
            			  }
            		  }
            	  }
            	}
            	writer.write("),");
            	// write movables
            	writer.write("Movable:(");
            	if (so.getMovablePositions() != null) {
            	  ArrayList<Observation>[] list = so.getMovablePositions();
            	  for (int i=0; i<list.length; i++) {
            		  for (int j=0; j<list[i].size(); j++) {
            			  if (list[i] != null) {
            				  writer.write("(" + list[i].get(j).itype + ",");
            				  writer.write(list[i].get(j).position.toString()+")");
            			  }
            		  }
            	  }
            	}
            	writer.write("),");
            	// write resources
            	writer.write("Resources:(");
            	if (so.getResourcesPositions() != null) {
            	  ArrayList<Observation>[] list = so.getResourcesPositions();
            	  for (int i=0; i<list.length; i++) {
            		  for (int j=0; j<list[i].size(); j++) {
            			  if (list[i] != null) {
            				  writer.write("(" + list[i].get(j).itype + ",");
            				  writer.write(list[i].get(j).position.toString()+")");
            			  }
            		  }
            	  }
            	}
            	writer.write("),");
            	// write portals
            	writer.write("Portals:(");
            	if (so.getPortalsPositions() != null) {
            	  ArrayList<Observation>[] list = so.getPortalsPositions();
            	  for (int i=0; i<list.length; i++) {
            		  for (int j=0; j<list[i].size(); j++) {
            			  if (list[i] != null) {
            				  writer.write("(" + list[i].get(j).itype + ",");
            				  writer.write(list[i].get(j).position.toString()+")");
            			  }
            		  }
            	  }
            	}
            	writer.write(")\r\n");
        		} else {
        			// log only counts of thing (simple)
        			writer.write("NPC: ");
        		}
            	// close writer to write from buffer to file
            	writer.close();
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}