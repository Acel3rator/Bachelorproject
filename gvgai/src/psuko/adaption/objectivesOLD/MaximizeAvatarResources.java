package psuko.adaption.objectivesOLD;

import java.util.Map;

import ontology.Types.ACTIONS;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import core.game.StateObservation;

public class MaximizeAvatarResources extends AbstractAIObjective<StateObservation, ACTIONS> {

	public MaximizeAvatarResources() {
		super("avRes", Sign.MAXIMIZE);
	}
	
	private int baseTick = 0;

	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
		final int currTick = actualState.getAdaptedState().getGameTick();
		
		final double weight = (double) (baseTick + 1) / (currTick + 1);
		
		final StateObservation actualStateObs = actualState.getAdaptedState();
		
		Map<Integer, Integer> avResources = actualStateObs.getAvatarResources();
		
		if (avResources == null || avResources.isEmpty())
		{
			return 0;
		}
		
		int counter = 0;
		int amount = 0;
		
		for (Map.Entry<Integer, Integer> resource : avResources.entrySet())
		{
			amount += resource.getValue();
			counter++;
		}
		
		
		
//		System.out.println("avResources: " + counter + " - " + amount);
		
		return amount * weight;
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		this.baseTick = actualState.getAdaptedState().getGameTick();
		this.maxValueBound = computeHeuristicValue(actualState);
	}

}
