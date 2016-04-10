package psuko.ai.genetics;

import java.util.List;

import psuko.ai.genetics.abstr.ListIndividual;
import psuko.ai.genetics.mutation.MoveOnMutation;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.Action;
import psuko.ai.objective.Solution;

public class ActionIndividual<S, A> extends ListIndividual<Action<S, A>, Solution> {

	public ActionIndividual(List<Action<S, A>> chromosome) {
		super(chromosome);
	}
	
	public void moveOn(final AbstractActionProvider<S, A> actionProvider, int portion)
	{
		MoveOnMutation<S, A> mutation = new MoveOnMutation<>(actionProvider);
		
		ActionIndividual<S, A> base = new ActionIndividual<>(this.chromosome);
		
		for (int i = 0; i < portion; i++)
		{
			base = mutation.mutate(base);
//			base.setFitness(null);
		}
		
//		this.fitness = null;
		
		this.chromosome = base.getChromosome();
	}

}
