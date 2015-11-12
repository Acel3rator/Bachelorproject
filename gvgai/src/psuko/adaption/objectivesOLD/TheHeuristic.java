package psuko.adaption.objectivesOLD;

import java.util.ArrayList;

import ontology.Types.ACTIONS;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import tools.Utils;
import core.game.Observation;
import core.game.StateObservation;

public class TheHeuristic extends AbstractAIObjective<StateObservation, ACTIONS> {

	public TheHeuristic() {
		super("test", Sign.MAXIMIZE);
	}

	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
		final StateObservation actualStateObs = actualState.getAdaptedState();
		
		if (actualStateObs.getResourcesPositions() == null || actualStateObs.getResourcesPositions().length == 0)
		{
			return 0;
		}
		
		int counter = 0;
		
		double nearest = Double.MAX_VALUE;
		
		for (ArrayList<Observation> resourceLists : actualStateObs.getResourcesPositions(actualStateObs.getAvatarPosition()))
		{
			counter += resourceLists.size();
			for (Observation obs : resourceLists)
			{
				if (obs.sqDist < nearest)
				{
					nearest = obs.sqDist;
				}
			}
		}
		
		double height = actualStateObs.getWorldDimension().getHeight() * actualStateObs.getBlockSize();
		double width = actualStateObs.getWorldDimension().getWidth() * actualStateObs.getBlockSize();
		
		nearest = Utils.normalise(nearest, 0, height * height + width * width);
			
		return counter == 0 ? 0 : - (nearest + (int) counter);
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
	}

}
