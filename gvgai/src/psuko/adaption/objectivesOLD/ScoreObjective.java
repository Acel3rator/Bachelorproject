package psuko.adaption.objectivesOLD;

import ontology.Types.ACTIONS;
import core.game.StateObservation;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;

public class ScoreObjective	extends AbstractAIObjective<StateObservation, ACTIONS> {

	public ScoreObjective() {
		super("score", Sign.MAXIMIZE);
	}
	
	private int baseTick = 0;

	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
		final int currTick = actualState.getAdaptedState().getGameTick();
		
		final double weight = (double) (baseTick + 1) / (currTick + 1);
		
		double score = actualState.getAdaptedState().getGameScore();
		
		return score * weight;
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
		this.baseTick = actualState.getAdaptedState().getGameTick();
		
	}

}
