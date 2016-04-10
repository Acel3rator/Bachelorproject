package psuko.ai.genetics.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.Action;

public class OnePointMutation<S, A> implements ActionMutation<S, A> {

	private final AbstractActionProvider<S, A> actionProvider;
	private final Random rand = new Random();
	
	public OnePointMutation(final AbstractActionProvider<S, A> actionProvider)
	{
		this.actionProvider = actionProvider;
	}
	
	@Override
	public ActionIndividual<S, A> mutate(ActionIndividual<S, A> theChosenOne) {
		final List<Action<S, A>> newChromosome = new ArrayList<>(theChosenOne.getChromosome());
		
		final int mutIdx = rand.nextInt(newChromosome.size());
		
		newChromosome.set(mutIdx, this.actionProvider.randomAction());
		
		return new ActionIndividual<>(newChromosome);
	}

}
