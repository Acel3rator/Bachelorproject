package psuko.ai.genetics.impl.eval;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractState;
import psuko.ai.markov.Action;
import psuko.ai.objective.MultiObjectiveHandler;
import psuko.ai.objective.Solution;

public class StandardBreakEvaluationStrategy<S, A> implements EvaluationStrategy<S, A> {

	@Override
	public void evaluateIndividual(ActionIndividual<S, A> individual,
			AbstractState<S, A> baseStateCopy,
			MultiObjectiveHandler<S, A> moProvider) {
		
		final AbstractState<S, A> rollerState = baseStateCopy;
		
		for (Action<S, A> action : individual)
		{
			rollerState.advance(action);
		}
		
		individual.setFitness(moProvider.createSolution(rollerState));
	}

}
