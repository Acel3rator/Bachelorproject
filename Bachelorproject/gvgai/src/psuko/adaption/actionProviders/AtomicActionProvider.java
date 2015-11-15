package psuko.adaption.actionProviders;

import java.util.ArrayList;
import java.util.List;

import ontology.Types.ACTIONS;
import core.game.StateObservation;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.Action;

public class AtomicActionProvider extends AbstractActionProvider<StateObservation, ACTIONS> {

	public AtomicActionProvider(StateObservation initialState) {
		super(initialState);
	}

	@Override
	protected List<Action<StateObservation, ACTIONS>> initAvailableActions(
			StateObservation initialState) {
		
		final List<Action<StateObservation, ACTIONS>> actionList = new ArrayList<>();
		
		for (final ACTIONS action : initialState.getAvailableActions())
		{
			actionList.add(new Action<StateObservation, ACTIONS>(action));
		}
		
		return actionList;
	}

}
