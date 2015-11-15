package psuko.ai.genetics.mutation;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.genetics.abstr.mutation.ListMutation;
import psuko.ai.markov.Action;
import psuko.ai.objective.Solution;

public interface ActionMutation<S, A> extends ListMutation<Action<S, A>, Solution, ActionIndividual<S, A>> {

}
