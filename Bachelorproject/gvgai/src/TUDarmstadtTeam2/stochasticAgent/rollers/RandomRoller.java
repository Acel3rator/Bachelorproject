package TUDarmstadtTeam2.stochasticAgent.rollers;

import java.util.Random;

import TUDarmstadtTeam2.stochasticAgent.StateRec;
import core.game.StateObservation;

public class RandomRoller extends AbstractRoller {


	public RandomRoller(StateObservation state, StateRec features, Random rnd) {
		super(state, features, rnd);
	}

	@Override
	public int roll(StateObservation gameState) {
		return rand.nextInt(gameState.getAvailableActions().size());
	}

}
