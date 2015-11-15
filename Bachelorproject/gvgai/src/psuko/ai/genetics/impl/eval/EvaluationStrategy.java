package psuko.ai.genetics.impl.eval;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.MultiObjectiveHandler;

public interface EvaluationStrategy<S, A> {

	void evaluateIndividual(ActionIndividual<S, A> individual,
			AbstractState<S, A> baseStateCopy,
			MultiObjectiveHandler<S, A> moProvider);

}
