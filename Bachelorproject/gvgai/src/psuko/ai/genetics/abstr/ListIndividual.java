package psuko.ai.genetics.abstr;

import java.util.Iterator;
import java.util.List;

public class ListIndividual<T, F> extends BaseIndividual<List<T>> implements Iterable<T> {
	
	protected F fitness;
	
	public ListIndividual(final List<T> chromosome)
	{
		super(chromosome);
	}
	
	public final void setFitness(final F fitness)
	{
		this.fitness = fitness;
	}
	
	public final F getFitness()
	{
		return this.fitness;
	}
	
	public final int getLength()
	{
		return this.chromosome.size();
	}

	@Override
	public final Iterator<T> iterator() {
		return this.chromosome.iterator();
	}
}
