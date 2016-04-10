package TUDarmstadtTeam2.utils;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

/**
 * Created by philipp on 18.05.15.
 */
public abstract class AbstractTUTeams2Player extends AbstractPlayer {


    @Override
    public abstract Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer);

    public boolean isPlaying(){
        return true;
    }
}
