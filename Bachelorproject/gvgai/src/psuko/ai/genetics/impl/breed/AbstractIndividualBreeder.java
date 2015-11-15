package psuko.ai.genetics.impl.breed;

import psuko.ai.genetics.ActionIndividualBreeder;
import psuko.ai.markov.AbstractActionProvider;

public abstract class AbstractIndividualBreeder<S, A> implements ActionIndividualBreeder<S, A> {

//<S, A> implements IndividualBreeder<Action<S, A>, Solution, Individual<S,A>> {

	protected final AbstractActionProvider<S, A> actionProvider;
	
	protected final int chromosomeSize;
	
	public AbstractIndividualBreeder(final AbstractActionProvider<S, A> actionProvider, final int chromosomeSize)
	{
		this.actionProvider = actionProvider;
		this.chromosomeSize = chromosomeSize;
	}
	
}
