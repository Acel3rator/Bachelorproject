package psuko.adaption.objectivesOLD;

import ontology.Types.ACTIONS;
import ontology.Types.WINNER;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import core.game.StateObservation;

public class WinLoseObjective extends AbstractAIObjective<StateObservation, ACTIONS> {

	public WinLoseObjective() {
		super("winlose", Sign.MAXIMIZE);
	}

	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		StateObservation stateObs = actualState.getAdaptedState();

		final boolean isGameOver = stateObs.isGameOver();
		final WINNER gameWinner = stateObs.getGameWinner();
		
		if (isGameOver && gameWinner == WINNER.PLAYER_LOSES)
		{
			return -1.0;
		}

		if (isGameOver && gameWinner == WINNER.PLAYER_WINS)
		{
			return 1.0;
		}
			
		return 0.0;
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {		
	}

}
