package psuko.ai.genetics.impl.breed;

import java.util.ArrayList;
import java.util.List;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.Action;

public class PartialRandomBreeder<S, A> extends AbstractEliteBreeder<S, A> {

	private final int keepSize;
	
	public PartialRandomBreeder(AbstractActionProvider<S, A> actionProvider,
			int chromosomeSize, int keepSize) {
		super(actionProvider, chromosomeSize);
		this.keepSize = keepSize;
	}

	@Override
	protected ActionIndividual<S, A> createIndividualFromElite(
			ActionIndividual<S, A> eliteIndividual) {

		final List<Action<S, A>> newChromosome = new ArrayList<>();
		
		int iter = 0;
		
		for (Action<S, A> action : eliteIndividual)
		{
			newChromosome.add(action);
			iter++;
			
			if (iter >= keepSize)
			{
				break;
			}
		}
		
		for (int i = iter; i < this.chromosomeSize; i++)
		{
			newChromosome.add(this.actionProvider.randomAction());
		}
		
		assert eliteIndividual.getLength() == newChromosome.size();
		
		return new ActionIndividual<>(newChromosome);
	}

}
