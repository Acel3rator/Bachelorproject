package psuko.ai.genetics.abstr.crossover;

import java.util.List;

import psuko.ai.genetics.abstr.ListIndividual;

public interface ListSingleOffspringCrossover<T, F, I extends ListIndividual<T, F>> extends BaseSingleOffspringCrossover<List<T>, I> {
	
}