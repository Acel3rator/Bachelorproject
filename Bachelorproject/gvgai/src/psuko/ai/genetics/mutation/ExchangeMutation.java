package psuko.ai.genetics.mutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.Action;

/**
 * swaps 2 indices in the chromosome
 * 
 * @author Patrick
 *
 * @param <S>
 * @param <A>
 */
public class ExchangeMutation<S, A> implements ActionMutation<S, A> {

	private final Random rand = new Random();

	@Override
	public ActionIndividual<S, A> mutate(ActionIndividual<S, A> theChosenOne) {

		final List<Action<S, A>> newChromosome = new ArrayList<>(
				theChosenOne.getChromosome());
		
		final int firstIndex = rand.nextInt(newChromosome.size());
		int secondIndex = rand.nextInt(newChromosome.size());
		
		if (firstIndex == secondIndex)
		{
			secondIndex = (secondIndex + 1) % newChromosome.size();
		}

		Collections.swap(newChromosome, firstIndex, secondIndex);

		return new ActionIndividual<>(newChromosome);
	}

}
