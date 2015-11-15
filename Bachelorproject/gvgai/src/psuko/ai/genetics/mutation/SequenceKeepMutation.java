package psuko.ai.genetics.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.Action;

public class SequenceKeepMutation<S, A> implements ActionMutation<S, A> {
	
	private Random rng = new Random();
	
	private final AbstractActionProvider<S, A> actionProvider;
	
	public SequenceKeepMutation(final AbstractActionProvider<S, A> actionProvider)
	{
		this.actionProvider = actionProvider;
	}
	
	@Override
	public ActionIndividual<S, A> mutate(ActionIndividual<S, A> theChosenOne) {
		
		final List<Action<S, A>> newChromosome = new ArrayList<>();
		
		int keepSize = rng.nextInt(theChosenOne.getLength() - 1);
		
		int iter = 0;
		
		for (Action<S, A> action : theChosenOne)
		{
			newChromosome.add(action);
			iter++;
			
			if (iter >= keepSize)
			{
				break;
			}
		}
		
		for (int i = iter; i < theChosenOne.getLength(); i++)
		{
			newChromosome.add(this.actionProvider.randomAction());
		}
		
		return new ActionIndividual<>(newChromosome);
	}
}
