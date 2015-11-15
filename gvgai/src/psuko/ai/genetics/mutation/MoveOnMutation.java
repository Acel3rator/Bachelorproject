package psuko.ai.genetics.mutation;

import java.util.ArrayList;
import java.util.List;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.Action;

public class MoveOnMutation<S, A> implements ActionMutation<S, A> {

	private final AbstractActionProvider<S, A> actionProvider;
	
	public MoveOnMutation(final AbstractActionProvider<S, A> actionProvider)
	{
		this.actionProvider = actionProvider;
	}
	
	@Override
	public ActionIndividual<S, A> mutate(ActionIndividual<S, A> theChosenOne) {
		
		final List<Action<S, A>> newChromosome = new ArrayList<>(theChosenOne.getChromosome());
		
		newChromosome.add(actionProvider.randomAction());

		return new ActionIndividual<>(newChromosome.subList(1, newChromosome.size()));
	}

}
