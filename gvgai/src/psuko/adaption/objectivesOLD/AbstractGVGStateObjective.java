package psuko.adaption.objectivesOLD;

import ontology.Types.ACTIONS;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import core.game.StateObservation;

public abstract class AbstractGVGStateObjective extends AbstractAIObjective<StateObservation, ACTIONS> {

	protected AbstractGVGStateObjective(String objectiveName, Sign sign) {
		super(objectiveName, sign);
	}

	protected abstract double computeHeuristicValue(StateObservation actualState);
	
	protected abstract void updateHeuristic(StateObservation actualState);
	
	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		final StateObservation actualStateObs = actualState.getAdaptedState();
		
		return this.computeHeuristicValue(actualStateObs);
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		final StateObservation actualStateObs = actualState.getAdaptedState();
		
		this.updateHeuristic(actualStateObs);
	}

}
