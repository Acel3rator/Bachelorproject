package psuko.adaption.objectivesOLD;

import ontology.Types.ACTIONS;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import core.game.StateObservation;

public abstract class AbstractGVGStateCompareHeuristic extends AbstractAIObjective<StateObservation, ACTIONS> {

	protected AbstractGVGStateCompareHeuristic(String objectiveName, Sign sign) {
		super(objectiveName, sign);
	}

	protected abstract double computeHeuristicValue(StateObservation actualState, StateObservation previousState);
	
	protected abstract void updateHeuristic(StateObservation actualState, StateObservation previousState);
	
	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		final StateObservation actualStateObs = actualState.getAdaptedState();
		final StateObservation previousStateObs = null;
		
		return this.computeHeuristicValue(actualStateObs, previousStateObs);
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
		final StateObservation actualStateObs = actualState.getAdaptedState();
		final StateObservation previousStateObs = null;
		
		this.updateHeuristic(actualStateObs, previousStateObs);
	}

}
