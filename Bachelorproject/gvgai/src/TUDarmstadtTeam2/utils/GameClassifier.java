package TUDarmstadtTeam2.utils;

import TUDarmstadtTeam2.stateIdentification.StateIdentification;
import core.game.StateObservation;
import ontology.Types;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by philipp on 15.05.15.
 *
 * This class does the classification on which type of
 * game we are dealing with, using the classes defined
 * in utils.GameClass.
 */
public class GameClassifier {

	// simulation parameter
	// number Of simulations
	private int simulationIterations;
	// number of games played simultaneously
	private int simulationWidth;
	// number of advances executed per simulation
	private int simulationDepth;

	private Random random;
	private StateIdentification hashFactory;

	/**
	 * To initialize the GameClassifier.
	 *
	 * The runtime of the method classify will depend
	 * directly on the size of the chosen parameters,
	 * so its important to set them as small as possible.
	 * 
	 * @param simulationIterations amount of times to repeat
	 *                             the experiment
	 * @param simulationWidth amount of games to play
	 *                        simultaneously
	 * @param simulationDepth amount of actions to perform on
	 *                        each run
	 */
	public GameClassifier(int simulationIterations, int simulationWidth,
			int simulationDepth) {
		this.simulationWidth = simulationWidth;
		this.simulationIterations = simulationIterations;
		this.simulationDepth = simulationDepth;
		random = new Random();
		hashFactory = new StateIdentification();
	}

	/**
	 * This method does the classification.
	 *
	 * It chooses randomly a sequence of possible actions
	 * and advances the currents game state according to
	 * this sequence.
	 * The game may be potentially stochastic, so the method
	 * performs the same sequence of actions several times and
	 * checks if the actions results in different game states.
	 *
	 * To reduce the probability of not detecting a stochastic
	 * game, the experiment will be repeated a few times.
	 *
	 * @param observation the current game
	 * @return the game class
	 */
	public GameClass classify(StateObservation observation) {
		ArrayList<Types.ACTIONS> availableActions = observation
				.getAvailableActions();
		// If NPCs are part of the game it is probably storachstic
		if (observation.getNPCPositions() != null) {
			return GameClass.STOCHASTIC_GAME;
		}
		// Simulate
		StateObservation[] observations = new StateObservation[simulationWidth];
		int[] hashes = new int[simulationWidth];
		Types.ACTIONS chosenAction;

		for (int i = 0; i < simulationIterations; i++) {
			// fill array with root
			for (int j = 0; j < simulationWidth; j++) {
				observations[j] = observation.copy();
			}
			for (int j = 0; j < simulationDepth; j++) {
				// pick action
				chosenAction = availableActions.get(random
						.nextInt(availableActions.size()));
				for (int k = 0; k < simulationWidth; k++) {
					observations[k].advance(chosenAction);
					hashes[k] = hashFactory
							.generateHashedState(observations[k]);
					if (observations[k].getNPCPositions() != null) {
						return GameClass.STOCHASTIC_GAME;
					}
					for (int m = 0; m < k; m++) {
						if (hashes[k] != hashes[m]) {
							return GameClass.STOCHASTIC_GAME;
						}
					}
				}
			}
		}
		return GameClass.DETERMINISTIC_GAME;
	}

	/**
	 * Reclassifies a game without considering the given class. Used if first
	 * classification was not correct.
	 * 
	 * @param oldClass the old classification result
	 * @param observation the current game
	 * @return the corrected class
	 */
	public GameClass reclassify(GameClass oldClass, StateObservation observation) {
		if (oldClass == GameClass.DETERMINISTIC_GAME) {
			return GameClass.DETERMINISTIC_BACKUP;
		} else {
			return GameClass.DETERMINISTIC_GAME;
		}
	}
}
