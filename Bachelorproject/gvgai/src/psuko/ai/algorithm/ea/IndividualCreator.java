package psuko.ai.algorithm.ea;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import psuko.ai.genetics.ActionIndividual;
import psuko.ai.genetics.mutation.ActionMutation;
import psuko.ai.genetics.mutation.ExchangeMutation;
import psuko.ai.genetics.mutation.LinearEvolvingMutation;
import psuko.ai.genetics.mutation.OnePointMutation;
import psuko.ai.genetics.mutation.SequenceKeepMutation;
import psuko.ai.genetics.mutation.StandardMutation;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.Action;

public class IndividualCreator<S, A> {
	
	public enum MutationType
	{
		RANDOM, MUTATION_STANDARD, MUTATION_ONEPOINT, MUTATION_LINEAR_EVOLVING, MUTATION_EXCHANGE, MUTATION_SEQUENCE_KEEP_RANDOM;
	}
	
	private final Random rand = new Random();
	private final MutationType mutationType;
	private final AbstractActionProvider<S, A> actionProvider;
	private final int chromosomeSize;
	
	public IndividualCreator(final MutationType mutationType, final AbstractActionProvider<S, A> actionProvider, final int chromosomeSize)
	{
		this.mutationType = mutationType;
		this.actionProvider = actionProvider;
		this.chromosomeSize = chromosomeSize;
	}
	
	public ActionIndividual<S, A> createIndividual(List<ActionIndividual<S, A>> candidates) {
		
		if (this.mutationType == MutationType.RANDOM || candidates.isEmpty())
		{
			final List<Action<S, A>> actionChromosome = new ArrayList<>();
			
			for (int i = 0; i < chromosomeSize; i++)
			{
				actionChromosome.add(actionProvider.randomAction());
			}
			
			return new ActionIndividual<>(actionChromosome);
		}
		
		final ActionMutation<S, A> mutationOperator;
		
		switch(this.mutationType)
		{
		case MUTATION_EXCHANGE:
			mutationOperator = new ExchangeMutation<>();
			break;
		case MUTATION_ONEPOINT:
			mutationOperator = new OnePointMutation<>(actionProvider);
			break;
		case MUTATION_LINEAR_EVOLVING:
			mutationOperator = new LinearEvolvingMutation<>(actionProvider);
			break;
		case MUTATION_SEQUENCE_KEEP_RANDOM:
			mutationOperator = new SequenceKeepMutation<>(actionProvider);
			break;
		case MUTATION_STANDARD:
			mutationOperator = new StandardMutation<>(actionProvider);
			break;
		default:
				throw new RuntimeException("no mutation-type given");		
		}
		
		return mutationOperator.mutate(candidates.get(rand.nextInt(candidates.size())));
	}
}
