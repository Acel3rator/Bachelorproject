package psuko.ai.genetics.impl.eval;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractState;
import psuko.ai.markov.Action;
import psuko.ai.objective.MultiObjectiveHandler;
import psuko.ai.objective.Solution;

public class ExtendedEvaluationStrategy<S, A> implements EvaluationStrategy<S, A> {

	@Override
	public void evaluateIndividual(ActionIndividual<S, A> individual,
			AbstractState<S, A> baseStateCopy,
			MultiObjectiveHandler<S, A> moProvider) {
		
		final AbstractState<S, A> rollerState = baseStateCopy;
		
		Solution solution = null;
		
		int iter = 0;
		
		for (Action<S, A> action : individual)
		{
			rollerState.advance(action);
			
			iter++;
			
			if (solution == null)
			{
				solution = moProvider.createSolution(rollerState);
			}
			else
			{
				solution.integrateSolution(moProvider.createSolution(rollerState), (1.0 - (1.0 / (iter))));
			}
		}
		
		individual.setFitness(solution);
	}

}
