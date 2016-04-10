package TUDarmstadtTeam2.standardMCTSv2_StateIdentification;

import ontology.Types;
import tools.ElapsedCpuTimer;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public class Agent extends AbstractPlayer {

    public static int ABORT_TIME = 5; //ms TODO set this automatically
    public static int MAX_SIMULATION_DEPTH = 10; //TODO make this dynamic
    public static int MAX_ROLLOUT_DEPTH = 25; //TODO make this dynamic
    // Set in Constructor
    public static int NUMBEROFACTIONS;
    /* Constant in UCT search */
    public static double C = Math.sqrt(2);
    public MCTSSearch search;

    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer){
        System.gc();
        System.out.println("own2");
        NUMBEROFACTIONS = so.getAvailableActions().size();
        search = new MCTSSearch(so.getAvailableActions());

    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return search.search(stateObs,elapsedTimer);
    }

}
