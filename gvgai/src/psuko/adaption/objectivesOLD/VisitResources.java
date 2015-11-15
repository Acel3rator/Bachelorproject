package psuko.adaption.objectivesOLD;

import java.util.ArrayList;

import ontology.Types.ACTIONS;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import tools.Utils;
import tools.Vector2d;
import core.game.Observation;
import core.game.StateObservation;

public class VisitResources extends AbstractAIObjective<StateObservation, ACTIONS> {

	public VisitResources() {
		super("vis", Sign.MAXIMIZE);
	}

	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
		final StateObservation actualStateObs = actualState.getAdaptedState();
		
		final Vector2d avPos = actualStateObs.getAvatarPosition();
		
		final ArrayList<Observation>[] portals = actualStateObs.getResourcesPositions();
		
		if (portals == null || portals.length == 0)
		{
			return 0;
		}
		
		if (actualStateObs.isGameOver())
		{
			return this.minValueBound;
		}
		
		double nearest = Double.MAX_VALUE;
		
		int counter = 0;
		
		for (ArrayList<Observation> portalList : portals) {
			for (Observation portal : portalList)
			{
				counter++;
				double xDist = (portal.position.x - avPos.x);
				double yDist = (portal.position.y - avPos.y);
				
				double mhDist = Math.abs(xDist + yDist);
				
				if (mhDist < nearest)
				{
					nearest = mhDist;
				}
			}
		}
		
		double height = actualStateObs.getWorldDimension().getHeight() * actualStateObs.getBlockSize();
		double width = actualStateObs.getWorldDimension().getWidth() * actualStateObs.getBlockSize();
		
		nearest = Utils.normalise(nearest, 0, height + width);
		
//		System.out.println("resources: " + counter + " - " + nearest);
		
		return - nearest + counter;
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
	}

}
