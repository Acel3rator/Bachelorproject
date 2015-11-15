package psuko.ai.genetics.abstr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BasePopulation<T, I extends BaseIndividual<T>> implements Iterable<I> {

	protected List<I> individuals = new ArrayList<>();
	
	public final List<I> getIndividuals()
	{
		return this.individuals;
	}
	
	public final boolean isEmpty()
	{
		return this.individuals.isEmpty();
	}
	
	public final void clearPopulation()
	{
		this.individuals.clear();
	}
	
	public void addIndividual(final I individual)
	{
		this.individuals.add(individual);
	}
	
	@Override
	public final Iterator<I> iterator() {
		return this.individuals.iterator();
	}

}
