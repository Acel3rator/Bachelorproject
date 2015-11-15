package TUDarmstadtTeam2.stochasticAgent;

import TUDarmstadtTeam2.utils.Config;
import tools.Vector2d;

import java.util.concurrent.BlockingDeque;

/**
 * Created by philipp on 28.05.15.
 */
public class Goal {
    private Vector2d pos;
    private int typ;

    public Goal(Vector2d pos, int typ){
        this.pos = pos;
        this.typ = typ;
    }

    public int getTyp() {
        return typ;
    }

    public Vector2d getPosition() {
        return pos;
    }
}
