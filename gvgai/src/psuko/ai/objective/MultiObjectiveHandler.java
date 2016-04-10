package psuko.ai.objective;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import psuko.ai.markov.AbstractState;

public final class MultiObjectiveHandler<S, A> extends BaseMultiObjectiveProvider {
	
	MultiObjectiveHandler(List<BaseObjective> objectives, List<Double> weights) {
		super(objectives, weights);
	}
	
	public MultiObjectiveHandler(final LinkedHashMap<BaseObjective, Double> orderedObjectiveWeightMap)
	{
		super(orderedObjectiveWeightMap);
	}
	
	public final void updateHeuristics(final AbstractState<S, A> realState)
	{
		for (final BaseObjective obj : this.orderedObjectiveWeightMap.keySet())
		{
			@SuppressWarnings("unchecked")
			final AbstractAIObjective<S, A> objective = (AbstractAIObjective<S, A>) obj;
			objective.updateHeuristic(realState);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Solution createSolution(final AbstractState<S, A> actualState)
	{
		final List<BaseObjective> objectives = new ArrayList<>();
		final List<Double> values = new ArrayList<>();
		
		for (final BaseObjective obj : this.orderedObjectiveWeightMap.keySet())
		{
			objectives.add(obj);
			values.add(((AbstractAIObjective<S, A>) obj).evaluateState(actualState));
		}
		
		return new Solution(objectives, values);
	}
	
}
