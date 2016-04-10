package psuko.ai.markov;

public class Action<S, A> {
	
	private final A action;
	
	public Action(final A action)
	{
		this.action = action;
	}

	final void advanceState(AbstractState<S, A> state) {
		state.advance(this.action);
	}
	
	public final A getAction()
	{
		return this.action;
	}
	
}
