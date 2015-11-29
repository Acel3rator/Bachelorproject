package pRobot;

import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public class Agent extends AbstractPlayer {

    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;

    /**
     * Observation grid.
     */
    protected ArrayList<Observation> grid[][];

    /**
     * block size
     */
    protected int block_size;


    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
        grid = so.getObservationGrid();
        block_size = so.getBlockSize();
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions();
        ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
        ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
        ArrayList<Observation>[] resourcesPositions = stateObs.getResourcesPositions();
        ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions();
        grid = stateObs.getObservationGrid();

        /*printDêebug(npcPositions,"npc");
        printDebug(fixedPositions,"fix");
        printDebug(movingPositions,"mov");
        printDebug(resourcesPositions,"res");
        printDebug(portalPositions,"por");
        System.out.println();               */

        // Check for a record that matches this category
        
        
        Types.ACTIONS action = null;
        StateObservation stCopy = stateObs.copy();

        double avgTimeTaken = 0;
        double acumTimeTaken = 0;
        long remaining = elapsedTimer.remainingTimeMillis();
        int numIters = 0;

        int remainingLimit = 5;
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit)
        {
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();
            int index = randomGenerator.nextInt(actions.size());
            action = actions.get(index);

            stCopy.advance(action);
            if(stCopy.isGameOver())
            {
                stCopy = stateObs.copy();
            }

            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
            //System.out.println(elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + ")");
            avgTimeTaken  = acumTimeTaken/numIters;
            remaining = elapsedTimer.remainingTimeMillis();
        }

        return action;
    }
}
/*  psuko    
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
	}*/
