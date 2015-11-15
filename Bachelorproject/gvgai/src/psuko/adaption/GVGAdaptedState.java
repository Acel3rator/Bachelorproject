package psuko.adaption;

import ontology.Types.ACTIONS;
import psuko.ai.markov.AbstractState;
import psuko.ai.markov.Action;
import core.game.StateObservation;

public final class GVGAdaptedState extends AbstractState<StateObservation, ACTIONS> {
	
	public GVGAdaptedState(StateObservation state,
			Action<StateObservation, ACTIONS> lastAppliedAction) {
		super(state, lastAppliedAction);
		KnowledgeBase.getInstance().updateBase(state);
	}

	@Override
	public void advance(ACTIONS action) {
		KnowledgeBase.getInstance().updateBefore(this.state);
		this.state.advance(action);
		KnowledgeBase.getInstance().updateAfter(this.state, action);
	}

	@Override
	public AbstractState<StateObservation, ACTIONS> copy() {
		return new GVGAdaptedState(this.state.copy(), this.lastAppliedAction);
	}

	@Override
	public boolean isGameOver() {
		return this.getAdaptedState().isGameOver();
	}

}
