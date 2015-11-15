package psuko.ai.markov;

import java.util.List;
import java.util.Random;

public abstract class AbstractActionProvider<S, A> {
	
	private static final Random RAND = new Random();
	
	protected List<Action<S, A>> availableActions;
	
	public AbstractActionProvider(S initialState)
	{
		this.availableActions = this.initAvailableActions(initialState);
	}
	
	protected abstract List<Action<S, A>> initAvailableActions(S initialState);
	
	public Action<S, A> randomAction()
	{
		return availableActions.get(RAND.nextInt(availableActions.size()));
	}

	public List<Action<S, A>> getAvailableActions() {
		return this.availableActions;
	}
	
	

}
