package psuko.ai;

import java.util.ArrayList;
import java.util.List;

import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.AbstractState;
import psuko.ai.markov.Action;
import psuko.ai.objective.MultiObjectiveHandler;

public abstract class AbstractAIAgent<S, A> {

	protected final MultiObjectiveHandler<S, A> moHandler;
	protected final AbstractActionProvider<S, A> actionProvider;
	
	private AbstractState<S, A> actualState;
	
	protected List<Action<S, A>> bufferActions = new ArrayList<>();
	
	private boolean advanceFlag = false;
	
	public AbstractAIAgent(final MultiObjectiveHandler<S, A> moHandler
			, final AbstractActionProvider<S, A> actionProvider
			, final AbstractState<S, A> initialState)
	{
		this.moHandler = moHandler;
		this.actionProvider = actionProvider;
		this.actualState = initialState;
	}

	public abstract void doComputeStep();
	
	public abstract List<Action<S, A>> getNextActionSequence();
	
	public abstract void handleAdvancing(int numAdvanceSteps);
	
	public A getNextAction()
	{		
		if (bufferActions.isEmpty())
		{
			bufferActions = this.getNextActionSequence();
			this.advanceFlag = true;
		}
		
		final A returnAction = bufferActions.get(0).getAction();
		
		bufferActions = bufferActions.subList(1, bufferActions.size());
		
		return returnAction;
	}
	
	protected final AbstractState<S, A> getCopiedOpenLoopState()
	{
		final AbstractState<S, A> openLoopState = this.actualState.copy();

		for (final Action<S, A> act : this.bufferActions)
		{
			this.actualState.advance(act);
		}
		
		return openLoopState;
	}
	
	public void reportUpdatedState(AbstractState<S, A> newState) {		
		this.moHandler.updateHeuristics(newState);
		this.actualState = newState;
		
		if (this.advanceFlag)
		{
			this.handleAdvancing(this.bufferActions.size() + 1);
			this.advanceFlag = false;
		}
	}
	
}
