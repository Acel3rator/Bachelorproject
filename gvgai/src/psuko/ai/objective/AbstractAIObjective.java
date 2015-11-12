package psuko.ai.objective;

import psuko.ai.markov.AbstractState;

/**
 * This class holds information for a single objective.
 *  
 * @author Patrick
 *
 * @param <S> state class
 * @param <A> action class
 */
public abstract class AbstractAIObjective<S, A> extends BaseObjective {
	
	protected AbstractAIObjective(final String objectiveName, final Sign sign)
	{
		super(objectiveName, sign);
	}
	
	/**
	 * make it more general here? (maybe for state comparison)
	 */
	protected abstract double computeHeuristicValue(final AbstractState<S, A> actualState);

	protected abstract void updateHeuristic(final AbstractState<S, A> actualState);
	
	final double evaluateState(final AbstractState<S, A> actualState)
	{
		double estimatedValue = this.computeHeuristicValue(actualState);
		
		if (this.sign == Sign.MINIMIZE)
		{
			estimatedValue = -estimatedValue;
		}
		
		this.updateBounds(estimatedValue);
		return estimatedValue;
	}	
	

}
