package psuko.ai.genetics.population;

import psuko.ai.genetics.abstr.ListIndividual;
import psuko.ai.genetics.abstr.ListIndividualPopulation;

public class TemporaryStorePopulation<T, F, I extends ListIndividual<T, F>> extends ListIndividualPopulation<T, F, I> {
	
	public I takeNextIndividual()
	{
		if (this.isEmpty())
		{
			return null;
		}
		//else
		return this.individuals.remove(0);
	}

}
