package psuko.ai.genetics.abstr;

public abstract class BaseIndividual<T> {
	
	protected T chromosome;
	
	protected BaseIndividual(T chromosome)
	{
		this.chromosome = chromosome;
	}

	public T getChromosome()
	{
		return this.chromosome;
	}
	
}