package psuko.adaption.objectivesOLD;

import ontology.Types.ACTIONS;
import ontology.Types.WINNER;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import core.game.StateObservation;

public abstract class GVGObjective extends AbstractAIObjective<StateObservation, ACTIONS> {
	
	final boolean rewardGameOver;
	
	protected GVGObjective(String objectiveName, boolean rewardGameOver) {
		super(objectiveName, Sign.MAXIMIZE);
		this.rewardGameOver = rewardGameOver;
	}

	protected abstract double computeHeuristicValue(StateObservation actualState);
	
	protected abstract void updateHeuristic(StateObservation actualState);
	
	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
		final StateObservation actualStateObs = actualState.getAdaptedState();
//		final StateObservation previousStateObs = actualState.getAdaptedState();
		
		if (rewardGameOver && actualStateObs.isGameOver())
		{
			return (actualStateObs.getGameWinner() == WINNER.PLAYER_WINS) ? 
					this.maxValueBound : this.minValueBound;
		}
		
		return  this.computeHeuristicValue(actualStateObs);
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		this.updateHeuristic(actualState.getAdaptedState());
	}

}
