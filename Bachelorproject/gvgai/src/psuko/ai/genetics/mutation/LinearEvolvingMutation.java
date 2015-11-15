package psuko.ai.genetics.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.Action;

public class LinearEvolvingMutation<S, A> implements ActionMutation<S, A> {
	
	private final AbstractActionProvider<S, A> actionProvider;
	private final Random rand = new Random();
	
	private final double slopeMultiplier = 0.5;
	
	public LinearEvolvingMutation(final AbstractActionProvider<S, A> actionProvider)
	{
		this.actionProvider = actionProvider;
	}
	
	@Override
	public ActionIndividual<S, A> mutate(ActionIndividual<S, A> theChosenOne) {
		
		final List<Action<S, A>> newChromosome = new ArrayList<>();
		
		int iter = 0;
		
		double slope = this.calculateSlope(theChosenOne.getLength(), this.slopeMultiplier);
		double startMutRate = this.calculateMeanMutationRate(theChosenOne.getLength()) * this.slopeMultiplier;
		
		for (Action<S, A> action : theChosenOne)
		{
			newChromosome.add(this.mutateAction(startMutRate + slope * iter, action));
			iter++;
		}
		
		return new ActionIndividual<>(newChromosome);
	}
	
	private double calculateSlope(int individualLength, double slopeMultiplier)
	{
		final double meanMutationRate = this.calculateMeanMutationRate(individualLength);
		
		double x1 = 0.0;
		double y1 = meanMutationRate * slopeMultiplier;
		
		double x2 = this.pivotPointer(individualLength);
		double y2 = meanMutationRate;
		
		return (y2 - y1) / (x2 - x1);		
	}
	
	private double calculateMeanMutationRate(int individualLength)
	{
		return 1.0 / (double) individualLength;
	}
	
	private double pivotPointer(int size)
	{
		return ((double) size - 1.0) / 2.0;
	}
	
	private Action<S, A> mutateAction(final double mutationRate, final Action<S, A> action)
	{
		if (rand.nextDouble() > mutationRate)
		{
			return action;
		}
		
		return actionProvider.randomAction();
	}

}
