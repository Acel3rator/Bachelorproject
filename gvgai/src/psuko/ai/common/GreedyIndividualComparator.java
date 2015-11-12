package psuko.ai.common;

import java.util.Comparator;
import java.util.Random;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.objective.Solution;

public class GreedyIndividualComparator<S, A> implements Comparator<ActionIndividual<S, A>>{

	private final Random random = new Random();
	private final double e = 1e-6;
	
	@Override
	public int compare(ActionIndividual<S, A> indA, ActionIndividual<S, A> indB) {
		final Solution mosA = indA.getFitness();
		final Solution mosB = indB.getFitness();
		
		if (mosA == null && mosB == null)
		{
			return 0;
		}
		
		if (mosA == null)
		{
			return 1;
		}
		
		if (mosB == null)
		{
			return -1;
		}
		
		final double valA = mosA.normalisedWeightedSum();
		final double valB = mosB.normalisedWeightedSum();	
		
		return - Double.compare(valA, valB);
	}

}
