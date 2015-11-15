package psuko.ai.genetics.impl.breed;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.genetics.mutation.ActionMutation;
import psuko.ai.genetics.mutation.StandardMutation;
import psuko.ai.markov.AbstractActionProvider;

public class HillClimbingBreeder<S, A> extends AbstractEliteBreeder<S, A> {
	
	private final ActionMutation<S, A> mutation;
	
	public HillClimbingBreeder(final AbstractActionProvider<S, A> actionProvider, final StandardMutation<S, A> standardMutation,
			final int chromosomeSize)
	{
		super(actionProvider, chromosomeSize);
		this.mutation = standardMutation;
	}

	@Override
	protected ActionIndividual<S, A> createIndividualFromElite(
			ActionIndividual<S, A> eliteIndividual) {
		return this.mutation.mutate(eliteIndividual);
	}
	
}
