package psuko.ai.genetics.abstr.crossover;

import psuko.ai.genetics.abstr.BaseIndividual;

public interface BaseSingleOffspringCrossover<T, I extends BaseIndividual<T>> {
	
	I mutate(I theChosenOneA, I theChosenOneB);
	
}
