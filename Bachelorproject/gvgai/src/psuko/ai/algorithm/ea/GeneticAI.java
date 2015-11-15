package psuko.ai.algorithm.ea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import psuko.ai.AbstractAIAgent;
import psuko.ai.algorithm.ea.IndividualCreator.MutationType;
import psuko.ai.common.GreedyIndividualComparator;
import psuko.ai.genetics.ActionIndividual;
import psuko.ai.genetics.impl.eval.EvaluationStrategy;
import psuko.ai.genetics.impl.eval.ExtendedBreakEvaluationStrategy;
import psuko.ai.genetics.population.ActionPopulation;
import psuko.ai.genetics.population.NonDominatedActionPopulation;
import psuko.ai.genetics.population.TemporaryStoreActionPopulation;
import psuko.ai.genetics.population.TemporaryStorePopulation;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.markov.AbstractState;
import psuko.ai.markov.Action;
import psuko.ai.objective.MultiObjectiveHandler;
import psuko.math.RouletteWheelSelector;
import psuko.math.RouletteWheelSelector.SelectableItem;

public class GeneticAI<S, A> extends AbstractAIAgent<S, A> {

	private final RouletteWheelSelector<IndividualCreator<S, A>> rws;
	final EvaluationStrategy<S, A> evalStrat;
	
	private final NonDominatedActionPopulation<S, A> population = new NonDominatedActionPopulation<>();
	private final TemporaryStoreActionPopulation<S, A> tempPopulation = new TemporaryStoreActionPopulation<>();
	private final int elitismCount = 2;
	
	private Action<S, A> applied = null;
	
	public GeneticAI(MultiObjectiveHandler<S, A> moHandler,
			AbstractActionProvider<S, A> actionProvider,
			AbstractState<S, A> initialState, int maxSimulationDepth) {
		super(moHandler, actionProvider, initialState);
		
		this.evalStrat = new ExtendedBreakEvaluationStrategy<>();

		
		
		final List<SelectableItem<IndividualCreator<S, A>>> gods = new ArrayList<>();	
		gods.add(new SelectableItem<IndividualCreator<S, A>>(
				new IndividualCreator<S, A>(MutationType.MUTATION_LINEAR_EVOLVING, actionProvider, maxSimulationDepth), 1.0));
//		gods.add(new SelectableItem<IndividualCreator<S, A>>(
//				new IndividualCreator<S, A>(MutationType.MUTATION_SEQUENCE_KEEP_RANDOM, actionProvider, maxSimulationDepth), 1.0));
		gods.add(new SelectableItem<IndividualCreator<S, A>>(
				new IndividualCreator<S, A>(MutationType.MUTATION_STANDARD, actionProvider, maxSimulationDepth), 1.0));
//		gods.add(new SelectableItem<IndividualCreator<S, A>>(
//		new IndividualCreator<S, A>(MutationType.MUTATION_EXCHANGE, actionProvider, maxSimulationDepth), 1.0));	
//		gods.add(new SelectableItem<IndividualCreator<S, A>>(
//		new IndividualCreator<S, A>(MutationType.MUTATION_ONEPOINT, actionProvider, maxSimulationDepth), 1.0));	
		gods.add(new SelectableItem<IndividualCreator<S, A>>(
				new IndividualCreator<S, A>(MutationType.RANDOM, actionProvider, maxSimulationDepth), 1.0));	
		this.rws = new RouletteWheelSelector<>(gods);
	}

	@Override
	public void doComputeStep() {
		
		final ActionIndividual<S, A> offspring;
		
		if (!this.tempPopulation.isEmpty())
		{
			offspring = this.tempPopulation.takeNextIndividual();
//			System.out.println("im here");
		}
		else
		{
			offspring = rws.selectItem().createIndividual(this.population.getIndividuals());
		}
		
		this.evalStrat.evaluateIndividual(offspring, this.getCopiedOpenLoopState(), this.moHandler);
		this.population.addIndividual(offspring);
	}

	@Override
	public List<Action<S, A>> getNextActionSequence() {
		
//		System.out.println(this.population.getIndividuals().size());
		
		Collections.sort(this.population.getIndividuals(), new GreedyIndividualComparator<S, A>());
		
//		Random rng = new Random();
		Action<S, A> action1 = this.population.getIndividuals().get(0).getChromosome().get(0);
		
		List<Action<S, A>> actionList = new ArrayList<>();
		
		actionList.add(action1);
		
		this.applied = action1;
		
		return actionList;
	}

	@Override
	public void handleAdvancing(int numAdvanceSteps) {
		
		Collections.sort(this.population.getIndividuals(), new GreedyIndividualComparator<S, A>());
		
		int count = 0;
		
		for (ActionIndividual<S, A> individual : this.population.getIndividuals())
		{
			if (count >= this.elitismCount)
			{
				break;
			}
			
//			if (this.actionProvider.getAvailableActions().indexOf(this.applied) 
//					!= this.actionProvider.getAvailableActions().indexOf(individual.getChromosome().get(0)))
//			{
//				System.out.println("meh.");
//				continue;
//			}

			count ++;

			individual.moveOn(actionProvider, numAdvanceSteps);
			this.tempPopulation.addIndividual(individual);
		}

		population.clearPopulation();
	}

}
