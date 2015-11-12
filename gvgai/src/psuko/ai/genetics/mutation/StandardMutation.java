package psuko.ai.genetics.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.Action;

public class StandardMutation<S, A> implements ActionMutation<S,A > {
	
	private final AbstractActionProvider<S, A> actionProvider;
	private final Random rand = new Random();
	
	public StandardMutation(final AbstractActionProvider<S, A> actionProvider)
	{
		this.actionProvider = actionProvider;
	}
	
	@Override
	public ActionIndividual<S, A> mutate(ActionIndividual<S, A> theChosenOne) {
		
		final List<Action<S, A>> newChromosome = new ArrayList<>();
		
		for (Action<S, A> action : theChosenOne)
		{
			newChromosome.add(this.mutateAction(1.0 / (double) theChosenOne.getLength(), action));
		}
		
		return new ActionIndividual<>(newChromosome);
	}
	
	private Action<S, A> mutateAction(final double mutationRate, final Action<S, A> action)
	{
		if (rand.nextDouble() > mutationRate)
		{
			return action;
		}
		
		return actionProvider.randomAction();
	}

}
