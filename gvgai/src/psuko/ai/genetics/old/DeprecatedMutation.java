package psuko.ai.genetics.old;

import psuko.ai.genetics.abstr.ListIndividual;


public interface DeprecatedMutation<T, F, I extends ListIndividual<T, F>> {
	
	I mutate(I theChosenOne);
	
}
