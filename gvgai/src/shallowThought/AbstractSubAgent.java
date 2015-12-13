package shallowThought;

import java.util.Hashtable;

import ontology.Types;
import tools.ElapsedCpuTimer;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public abstract class AbstractSubAgent extends AbstractPlayer {

	public abstract Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer);
	
	// Getter for all prameter names
	public abstract String[] getParameterList();
	
    public abstract void setParameter(String name, String value);
    public abstract Number getParameter(String name);
    /*
	// getters for parameter values
    public abstract Integer getIntParameter(String name);
    public abstract Double getDoubleParameter(String name);
    
    // setters for parameter values
    public abstract void setIntParameter(String name, Integer value);
    public abstract void setDoubleParameter(String name, Double value);
    */
}
