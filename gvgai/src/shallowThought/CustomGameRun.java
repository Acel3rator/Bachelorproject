package shallowThought;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import core.game.Event;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.Vector2d;

/**
 * This class takes care of a whole game run, saving for each tick
 * the custom state (as a CustomState-Object) and providing a function
 * to interpret each attribute as a f: gametick x value.
 * Either construct from recorded game or update on the run.
 */
public class CustomGameRun {

	/**
	 * This is a list of game-state-observations. Each observation has its own tick.
	 * For this Class to work properly, you have to guarantee to add the customStates
	 * in the correct gameTick-order.
	 */
	private ArrayList<CustomState> customState;
	
	// constructors
	public CustomGameRun() {
		customState = new ArrayList<CustomState>(2000);
	}
	
	/**
	 * This method updates the object.
	 */
	public void update(StateObservation so) {
		update(new CustomState(so));
	}
	
	/**
	 * This method updates the object. For proper work, always update in the order
	 * of game-ticks. 
	 */
	public void update(CustomState cs) {
		// If we skipped a few states during the updating
				if (customState.size() < cs.getGameTick()) {
					// TODO: Create and estimate states in between
				}
				// This should be normal case:
				else if (customState.size() == cs.getGameTick()) {
					customState.add(cs);
				}
				// This is an afterwards-modification, should be well justified
				else if (customState.size() > cs.getGameTick()) {
					customState.set(cs.getGameTick(), cs);
				}
	}

	/**
	 * Here we decide which features to give to the NN
	 * Naive method: simply return as many basic features as possible from last observed state
	 * @return double[] array of double-values
	 */
	public Double[] getNNIntput() {
		ArrayList<Double> result = new ArrayList<Double>();
		result.add((double) customState.get(customState.size()-1).getNumNPCs());
		result.add((double) customState.get(customState.size()-1).getNumImmov());
		result.add((double) customState.get(customState.size()-1).getNumMov());
		result.add((double) customState.get(customState.size()-1).getNumRes());
		result.add((double) customState.get(customState.size()-1).getNumPortal());
		return result.toArray(new Double[result.size()]);
	}
	
	/**
	 * This method is supposed to write a customGameRun to a txt-file.
	 * @param file
	 */
	private void writeToFile(File file) {
		for (CustomState cs : customState) {
			if (customState.indexOf(cs) == 0) {cs.writeToFile(file, false);}
			else {cs.writeToFile(file, true);}
		}
	}
	
	/**
	 * This method is supposed to read a customGameRun from a txt-file.
	 * @param file
	 */
	private void readFromFile(File file) {
    	BufferedReader reader = null;
    	String line = "";
    	try {
    		reader = new BufferedReader(new FileReader(file));
    		while ((line = reader.readLine()) != null) {
    			CustomState cs = new CustomState(line);
    			this.update(cs);
       		}

    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		if (reader != null) {
    			try {
    				reader.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
	}
}
