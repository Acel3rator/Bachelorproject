package psuko.adaption.objectivesOLD;

import java.util.ArrayList;

import ontology.Types.ACTIONS;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import tools.Vector2d;
import core.game.Observation;
import core.game.StateObservation;

public class KeepDistanceToNPC extends AbstractAIObjective<StateObservation, ACTIONS> {

	public KeepDistanceToNPC() {
		super("tes2t", Sign.MAXIMIZE);
	}

	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
		final StateObservation actualStateObs = actualState.getAdaptedState();
		
		if (actualStateObs.getNPCPositions() == null || actualStateObs.getNPCPositions().length == 0)
		{
			return 0;
		}
		
		double count = 0;
		
		int cap = 2;
		
		int counter = 0;
		
		Vector2d avPos = actualStateObs.getAvatarPosition();
		
		for (ArrayList<Observation> resourceLists : actualStateObs.getNPCPositions())
		{
			for (Observation obs : resourceLists)
			{
				counter++;
				double xDist = Math.abs(obs.position.x - avPos.x) / actualStateObs.getBlockSize();
				double yDist = Math.abs(obs.position.y - avPos.y) / actualStateObs.getBlockSize();
				
				double manHD = xDist + yDist;
				
				if (manHD <= cap)
				{
					count += cap + 1 - manHD;
				}
			}
		}
		
		System.out.println("npcs: " + counter);
		
		return -count;
	}
	
	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
	}

}
