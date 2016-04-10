package psuko;

import java.util.LinkedHashMap;

import ontology.Types.ACTIONS;
import psuko.adaption.GVGAdaptedState;
import psuko.adaption.actionProviders.AtomicActionProvider;
import psuko.adaption.obj.ExtendedObservablesHeuristic;
import psuko.adaption.obj.ExtendedPositionHeuristic;
import psuko.adaption.obj.NPCHeuristic;
import psuko.adaption.objectivesOLD.KeepDistanceToNPC;
import psuko.adaption.objectivesOLD.MaximizeAvatarResources;
import psuko.adaption.objectivesOLD.PositionHeuristic;
import psuko.adaption.objectivesOLD.ScoreObjective;
import psuko.adaption.objectivesOLD.TheHeuristic;
import psuko.adaption.objectivesOLD.VisitObservablesObjective;
import psuko.adaption.objectivesOLD.VisitObservablesObjective.ObservableType;
import psuko.adaption.objectivesOLD.VisitObservablesObjective.ValueType;
import psuko.adaption.objectivesOLD.WinLoseObjective;
import psuko.adaption.objectivesOLD.WinScoreHeuristicAdaption;
import psuko.ai.AbstractAIAgent;
import psuko.ai.algorithm.ea.GeneticAI;
import psuko.ai.markov.AbstractActionProvider;
import psuko.ai.objective.BaseObjective;
import psuko.ai.objective.MultiObjectiveHandler;
import core.game.StateObservation;

public class ControlUnit {
	
	public static int mo_config = 5;
	public static int ap_config = 6;
	
//	public static SimulationType simType = SimulationType.EX_MONTE_CARLO_SIMULATIONS;
//	public static LoopType loopType = LoopType.OPEN_LOOP;
	public static int simulationDepth = 12;
	
	public static final AbstractAIAgent<StateObservation, ACTIONS> createAgent(StateObservation initialState)
	{
		
		return new GeneticAI<StateObservation, ACTIONS>(getMOHandler(initialState)
				, getActionProvider(initialState), new GVGAdaptedState(initialState, null), simulationDepth);
		
//		return new RandomAI<StateObservation, ACTIONS>(getMOHandler(initialState)
//				, getActionProvider(initialState), new GVGAdaptedState(initialState, null), simulationDepth);

//		return new MCTSAgent<StateObservation, ACTIONS>(getMOHandler(initialState), getActionProvider(initialState), new GVGAdaptedState(initialState, null)
//		, MCTSPolicies.<StateObservation, ACTIONS>SampleMCTS(simType, loopType, simulationDepth));
		
//		return new MCTSAgent<StateObservation, ACTIONS>(getMOHandler(initialState), getActionProvider(initialState), new GVGAdaptedState(initialState, null)
//		, MCTSPolicies.<StateObservation, ACTIONS>UCT(simType, loopType, simulationDepth));
	}
	
	private static final AbstractActionProvider<StateObservation, ACTIONS> getActionProvider(StateObservation initialState)
	{
		return new AtomicActionProvider(initialState);
	}

	private static final MultiObjectiveHandler<StateObservation, ACTIONS> getMOHandler(StateObservation initialState)
	{		
		final LinkedHashMap<BaseObjective, Double> 
			objectivesAndRatiosMap = new LinkedHashMap<>();
		
		switch (mo_config)
		{
		case -1:
			objectivesAndRatiosMap.put(new WinScoreHeuristicAdaption(), 1.0);
			objectivesAndRatiosMap.put(new ExtendedPositionHeuristic(), 0.1);
			objectivesAndRatiosMap.put(new ExtendedObservablesHeuristic(), 1.0);
			objectivesAndRatiosMap.put(new MaximizeAvatarResources(), 0.1);
			break;
		case 0:
			objectivesAndRatiosMap.put(new WinScoreHeuristicAdaption(), 1.0);
			break;
		case 1:
			objectivesAndRatiosMap.put(new ScoreObjective(), 1.0);
			objectivesAndRatiosMap.put(new WinLoseObjective(), 1000.0);
			break;
		case 2: //best
			objectivesAndRatiosMap.put(new WinLoseObjective(), 1000.0);
			objectivesAndRatiosMap.put(new ScoreObjective(), 1.0);
			objectivesAndRatiosMap.put(new PositionHeuristic(), 0.1);
			break;
		case 3: 
			objectivesAndRatiosMap.put(new WinScoreHeuristicAdaption(), 1.0);
			objectivesAndRatiosMap.put(new PositionHeuristic(), 0.1);
			break;
		//
		//	
		case 4:
			objectivesAndRatiosMap.put(new WinLoseObjective(), 1000.0);
			objectivesAndRatiosMap.put(new ScoreObjective(), 1.0);
			objectivesAndRatiosMap.put(new PositionHeuristic(), 0.1);
			
			objectivesAndRatiosMap.put(new TheHeuristic(), 2.0);
			objectivesAndRatiosMap.put(new KeepDistanceToNPC(), 0.2);			
			break;
		case 5:
			objectivesAndRatiosMap.put(new WinLoseObjective(), 10.0);
			objectivesAndRatiosMap.put(new ScoreObjective(), 2.0);
			
			boolean up = false;
			boolean left = false;
	
			for (ACTIONS action : initialState.getAvailableActions()) {
				if (action == ACTIONS.ACTION_DOWN || action == ACTIONS.ACTION_UP)
					up = true;
				if (action == ACTIONS.ACTION_LEFT || action == ACTIONS.ACTION_RIGHT)
					left = true;
			}
	
			if (up && left)
			{
				objectivesAndRatiosMap.put(new PositionHeuristic(), 0.1);
			}
			
//			System.out.println("bla");
			
			objectivesAndRatiosMap.put(new NPCHeuristic(), 0.25);

//			objectivesAndRatiosMap.put(new KeepDistanceToNPC(), 1.0);
//			objectivesAndRatiosMap.put(new VisitResources(), 0.1);
			objectivesAndRatiosMap.put(new MaximizeAvatarResources(), 0.1);
			objectivesAndRatiosMap.put(new VisitObservablesObjective(ObservableType.PORTAL, 300, ValueType.NEAREST), 0.1);
//			objectivesAndRatiosMap.put(new VisitObservablesObjective(ObservableType.NPC, 400, ValueType.MINIMIZE_NEAREST), 0.025);
			objectivesAndRatiosMap.put(new VisitObservablesObjective(ObservableType.RESOURCE, 200,ValueType.MINIMIZE_NEAREST), 0.1);
			objectivesAndRatiosMap.put(new VisitObservablesObjective(ObservableType.MOVABLE, 200, ValueType.MINIMIZE_NEAREST), 0.1);
//			objectivesAndRatiosMap.put(new VisitObservablesObjective(ObservableType.IMMOVABLE, 200, ValueType.MINIMIZE_NEAREST), 0.1);
			break;
			
		default:
			objectivesAndRatiosMap.put(new WinScoreHeuristicAdaption(), 1.0);
			break;
		}
		
		return new MultiObjectiveHandler<StateObservation, ACTIONS>(objectivesAndRatiosMap);
	}
}
