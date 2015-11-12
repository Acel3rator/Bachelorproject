package psuko.ai.genetics.impl.breed;

import java.util.ArrayList;
import java.util.List;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.Action;

public class RandomBreeder<S, A> extends AbstractIndividualBreeder<S, A> {

	public RandomBreeder(final AbstractActionProvider<S, A> actionProvider, final int chromosomeSize) {
		super(actionProvider, chromosomeSize);
	}

	@Override
	public ActionIndividual<S, A> createIndividual() {
		final List<Action<S, A>> newChromosome = new ArrayList<>();
		
		for (int i = 0; i < this.chromosomeSize; i++)
		{
			newChromosome.add(this.actionProvider.randomAction());
		}
		
		return new ActionIndividual<>(newChromosome);
	}

}
