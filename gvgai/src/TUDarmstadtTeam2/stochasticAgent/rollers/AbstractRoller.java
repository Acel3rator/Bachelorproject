package TUDarmstadtTeam2.stochasticAgent.rollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import TUDarmstadtTeam2.stochasticAgent.StateRec;
import core.game.StateObservation;

public abstract class AbstractRoller {
	
    StateRec features;
    Random rand;

    public AbstractRoller(StateObservation state, StateRec features, Random rnd) {
    	rand = rnd;
    	this.features = features;
    }
    
	public abstract int roll(StateObservation state);


	public StateRec getFeatures(StateObservation state) {
		features.updateState(state);
    	return features;
    }
	public StateRec getFeatures() {
    	return features;
    }
    
    public String[] getFeatureNames(StateObservation state) {
        return features.getFeatureVectorKeys(state);
    }


    public boolean rollerFinished(){
    	return false;
    }
    
    
    
    
    
    
    
    /************** Legacy methods for EvoRoller ****************/
    
    /**
     * legacy method for EvoRoller
     * @param genMapping
     * @param weights
     */
	public void setParams(HashMap<String, Integer> genMapping, double[] weights) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * legacy method for EvoRoller
	 * @return
	 */
	public boolean newFeaturesFound() {
		return false;
	}

	/**
	 * legacy method for EvoRoller
	 * @return
	 */
	public ArrayList<String> getNewFeatures() {
		return null;
	}


}
