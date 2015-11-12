package psuko.ai.genetics.population;

import psuko.ai.common.SolutionParetoComparator;
import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.Action;
import psuko.ai.objective.Solution;

public class NonDominatedActionPopulation<S, A> extends NonDominatedPopulation<Action<S, A>, Solution, ActionIndividual<S,A>> {
	
	public NonDominatedActionPopulation()
	{
		super(new SolutionParetoComparator());
	}
	
}
