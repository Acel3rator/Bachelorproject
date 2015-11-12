package psuko;

import ontology.Types.ACTIONS;
import psuko.adaption.GVGAdaptedState;
import psuko.ai.AbstractAIAgent;
import tools.ElapsedCpuTimer;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public class Agent extends AbstractPlayer {

	private final AbstractAIAgent<StateObservation, ACTIONS> aiAgent;
	
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
    	this.aiAgent = ControlUnit.createAgent(so);
    }
    
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

		aiAgent.reportUpdatedState(new GVGAdaptedState(stateObs, null));
		
		//
//    	int adv = StateObservation.advanceCount;
//    	int cpy = StateObservation.copyCount;
		//
		
		long remaining = elapsedTimer.remainingTimeMillis();
		int numIters = 0;

		long remainingTimeAtLoopStart;
		long remainingTimeAtLoopEnd = remaining;
		long timeDiffAcc = 0;
		double averageTimeDiff = 0;

		int remainingLimit = 2;
		
		while (remainingTimeAtLoopEnd > averageTimeDiff + remainingLimit) {
//		while (numIters < 25) {
			
			remainingTimeAtLoopStart = elapsedTimer.remainingTimeMillis();

			aiAgent.doComputeStep();
			
			numIters++;

			remainingTimeAtLoopEnd = elapsedTimer.remainingTimeMillis();
			timeDiffAcc += remainingTimeAtLoopStart - remainingTimeAtLoopEnd;

			averageTimeDiff = (double) timeDiffAcc / numIters;

		}
		
//		System.out.println("iterations: " + numIters + " - " + 
//				((double)(StateObservation.advanceCount - adv) / numIters) + " - " + 
//				((double)(StateObservation.copyCount - cpy) / numIters));
		
//		ScenarioRunner.getInstance().getCurrentGameStatistics().addScore(stateObs.getGameScore());
		
		return aiAgent.getNextAction();
	}
	
}
