package psuko.ai.genetics;

import psuko.ai.genetics.abstr.ListIndividualBreeder;
import psuko.ai.markov.Action;
import psuko.ai.objective.Solution;

public interface ActionIndividualBreeder<S, A> extends ListIndividualBreeder<Action<S, A>, Solution, ActionIndividual<S, A>> {

}
