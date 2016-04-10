package psuko.ai.genetics.population;

import java.util.Comparator;
import java.util.Iterator;

import psuko.ai.genetics.abstr.ListIndividual;
import psuko.ai.genetics.abstr.ListIndividualPopulation;

public class NonDominatedPopulation<T, F, I extends ListIndividual<T, F>> extends ListIndividualPopulation<T, F, I> {
	
	private final Comparator<F> dominationComparator;
	
	public NonDominatedPopulation(final Comparator<F> dominationComparator)
	{
		this.dominationComparator = dominationComparator;
	}
	
	@Override
	public void addIndividual(I individual)
	{		
		for (Iterator<I> iter = this.individuals.iterator(); iter.hasNext();)
		{
			final I listIndividual = iter.next();
			
			final int dom = this.dominationComparator.compare(listIndividual.getFitness(), individual.getFitness());
			
			// need check! - checked! (works)
			
			if (dom == 1)
			{
				iter.remove();
			} else if (dom == -1)
			{
				return;
			}
		}
		
		this.individuals.add(individual);
		
	}
}
