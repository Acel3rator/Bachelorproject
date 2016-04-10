package psuko.ai.genetics.impl.breed;

import java.util.List;
import java.util.Random;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractActionProvider;

public abstract class AbstractEliteBreeder<S, A> extends AbstractIndividualBreeder<S, A> {

	private Random rand = new Random();
	
	private List<ActionIndividual<S,A>> elite;
	
	public AbstractEliteBreeder(AbstractActionProvider<S, A> actionProvider,
			int chromosomeSize) {
		super(actionProvider, chromosomeSize);
	}
	
	public boolean isReady()
	{
		return this.elite != null && !this.elite.isEmpty();
	}
	
	public final void updateElite(final List<ActionIndividual<S,A>> elite)
	{
		this.elite = elite;
	}

	@Override
	public ActionIndividual<S, A> createIndividual() 
	{
		final int eliteIdx = rand.nextInt(this.elite.size());
		
		return this.createIndividualFromElite(this.elite.get(eliteIdx));
	}
	
	protected abstract ActionIndividual<S, A> createIndividualFromElite(ActionIndividual<S, A> eliteIndividual);

}
