package psuko.adaption.obj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import psuko.adaption.KnowledgeBase;
import tools.Vector2d;
import core.game.StateObservation;

public class ExtendedPositionHeuristic extends AbstractGVGStateObjective {
	
	private List<Vector2d> visitedPositions = new ArrayList<>();
//	private final int capSize = 200;
//	private final int shrinkSize = 100;
	
	public ExtendedPositionHeuristic() {
		super("ex-position", Sign.MAXIMIZE);
	}

	@Override
	protected double computeHeuristicValue(StateObservation actualState) {
		double val = -Collections.frequency(visitedPositions, actualState.getAvatarPosition());
		
		double multiplier = KnowledgeBase.getInstance().averageTicksBetweenScoresLately / KnowledgeBase.getInstance().averageTicksBetweenScores;
		
		multiplier /= KnowledgeBase.getInstance().interval;
		
		return val * multiplier;
	}

	@Override
	protected void updateHeuristic(StateObservation actualState) {
		visitedPositions.add(0, actualState.getAvatarPosition());
	}
}