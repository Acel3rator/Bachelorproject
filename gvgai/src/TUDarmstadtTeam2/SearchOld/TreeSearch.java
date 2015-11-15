package TUDarmstadtTeam2.SearchOld;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;

/**
 * Created by philipp on 14.05.15.
 */
public interface TreeSearch {
    ArrayList<Types.ACTIONS> startSearch(StateObservation observation, ElapsedCpuTimer timer, int ... SearchInterval);
    ArrayList<Types.ACTIONS> continueSearch (ElapsedCpuTimer timer);
    int getCount();
}
