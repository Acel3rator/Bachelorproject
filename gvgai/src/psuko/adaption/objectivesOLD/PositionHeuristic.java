package psuko.adaption.objectivesOLD;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ontology.Types.ACTIONS;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import tools.Vector2d;
import core.game.StateObservation;

public class PositionHeuristic extends AbstractAIObjective<StateObservation, ACTIONS> {

	private Map<Vector2d, Double> valMap = new HashMap<>();
	
	private List<Vector2d> visitedPositions = new ArrayList<>();
	private final int capSize = 200;
	private final int shrinkSize = 100;
	
	public PositionHeuristic() {
		super("position", Sign.MAXIMIZE);
	}
//
//	@Override
//	protected double computeHeuristicValue(
//			AbstractState<StateObservation, ACTIONS> actualState,
//			AbstractState<StateObservation, ACTIONS> previousState) {
//		
//		final Vector2d avPos = actualState.getAdaptedState().getAvatarPosition();
//		
//		if (valMap.containsKey(avPos))
//		{
//			return - valMap.get(avPos);
//		}
//		return 0.0;
//	}
//
//	@Override
//	protected void updateHeuristic(
//			AbstractState<StateObservation, ACTIONS> actualState,
//			AbstractState<StateObservation, ACTIONS> previousState) {
//		
//		final Vector2d avPos = actualState.getAdaptedState().getAvatarPosition();
//		
//		valMap.put(avPos, 1.0);
//
//		for (Map.Entry<Vector2d, Double> entry : this.valMap.entrySet())
//		{
//			entry.setValue(entry.getValue()  / 1.01);
//		}
////		
//	}

	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
		
		
		double val = -Collections.frequency(visitedPositions, actualState.getAdaptedState().getAvatarPosition());
		
//		System.out.println("____________" + val + " - " + this.visitedPositions.size());
				
		return val;
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		final StateObservation stateObs = actualState.getAdaptedState();
		
		visitedPositions.add(0, stateObs.getAvatarPosition());
		
//		if (visitedPositions.size() > this.capSize)
//		{
//			this.visitedPositions = this.visitedPositions.subList(0, this.shrinkSize);
//		}		
	}
}