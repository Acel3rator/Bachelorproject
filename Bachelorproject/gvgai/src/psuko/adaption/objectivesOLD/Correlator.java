package psuko.adaption.objectivesOLD;

import ontology.Types.ACTIONS;
import psuko.ai.markov.AbstractState;
import psuko.ai.objective.AbstractAIObjective;
import core.game.StateObservation;

public class Correlator extends AbstractAIObjective<StateObservation, ACTIONS> {

	public Correlator() {
		super("test", Sign.MAXIMIZE);
	}

	@Override
	protected double computeHeuristicValue(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
		final StateObservation actualStateObs = actualState.getAdaptedState();
		
//		for (ArrayList<Obseravtion> obs : actualStateObs.getImmovablePositions())
//		{
//			
//		}
		
		actualStateObs.getImmovablePositions();
		actualStateObs.getMovablePositions();
		
		actualStateObs.getNPCPositions();
		actualStateObs.getPortalsPositions();
		actualStateObs.getResourcesPositions();
		
		
		
		return 0;
	}

	@Override
	protected void updateHeuristic(
			AbstractState<StateObservation, ACTIONS> actualState) {
		
	}

}
