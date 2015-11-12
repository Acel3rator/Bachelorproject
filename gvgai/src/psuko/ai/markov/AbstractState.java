package psuko.ai.markov;

/**
 * Abstract generic state object for the framework.
 * should be used as an adapter to integrate state-action-interaction
 * 
 * @author patrick
 *
 * @param <S> State Class
 * @param <A> Action Class
 */
public abstract class AbstractState<S, A> {

	protected final S state;
	
	protected Action<S, A> lastAppliedAction;
	
	

	public AbstractState(final S state, final Action<S, A> lastAppliedAction) {
		this.state = state;
		this.lastAppliedAction = lastAppliedAction;
	}

	public final void advance(Action<S, A> action) {
		action.advanceState(this);
		this.lastAppliedAction = action;
	}

	public final S getAdaptedState() {
		return this.state;
	}

	public Action<S, A> getLastAppliedAction() {
		return lastAppliedAction;
	}

	protected abstract void advance(A action);

	public abstract AbstractState<S, A> copy();
	
	public abstract boolean isGameOver();

}
