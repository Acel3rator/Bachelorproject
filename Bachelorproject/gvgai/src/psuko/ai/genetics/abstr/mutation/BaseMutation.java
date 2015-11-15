package psuko.ai.genetics.abstr.mutation;

import psuko.ai.genetics.abstr.BaseIndividual;

public interface BaseMutation<T, I extends BaseIndividual<T>> {
	
	I mutate(I theChosenOne);
	
}
