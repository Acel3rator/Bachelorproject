package psuko.adaption.objectivesOLD;

import controllers.Heuristics.StateHeuristic;
import controllers.Heuristics.WinScoreHeuristic;
import ontology.Types.ACTIONS;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import core.game.StateObservation;

public class WinScoreHeuristicAdaption extends AbstractAIObjective<StateObservation, ACTIONS> {

	private final StateHeuristic heuristic = new WinScoreHeuristic(null);
	
	public WinScoreHeuristicAdaption() {
		super("winScore", Sign.MAXIMIZE);
	}

	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		return heuristic.evaluateState(actualState.getAdaptedState());
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		// TODO Auto-generated method stub
		
	}
}
