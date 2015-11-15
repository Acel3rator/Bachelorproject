package TUDarmstadtTeam2.stochasticAgent;

import TUDarmstadtTeam2.stochasticAgent.rollers.AbstractRoller;
import TUDarmstadtTeam2.utils.AvatarMovement;
import TUDarmstadtTeam2.utils.Config;
import TUDarmstadtTeam2.utils.Pair;
import TUDarmstadtTeam2.utils.TuUtils;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * MCTS SearchOld
 */
public class MCTSSearch {

	private TreeNode root;
	/* Holds the root of the search */
	private StateObservation rootState;
	/* Holds the state of the node currently considered */
	private StateObservation currentState;

	private Random random;

	private Types.ACTIONS[] availableActions;

	private AbstractRoller roller;
	private double initialScore;
	private ArrayList<Vector2d> positions;
	private final double HUGE_NEGATIVE = -10000000.0;
	private final double HUGE_POSITIVE = 10000000.0;
	private boolean knowledgeBaseActive;
	protected static double[] bounds = new double[] { Double.MAX_VALUE,
			-Double.MAX_VALUE };

	private KnowledgeBase memory;
	public static double[][][] sampleMap;

	private int currentRolloutDepth;

	/**
	 * Additional constructor to reproduce results (random is seeded);
	 * 
	 * @param memory
	 */
	public MCTSSearch(ArrayList<Types.ACTIONS> availableActions,
			AbstractRoller roller, Random rnd, KnowledgeBase memory,
			int simulationDepth) {
		this(availableActions, roller, rnd, simulationDepth);
		knowledgeBaseActive = true;
		this.memory = memory;
	}

	public MCTSSearch(ArrayList<Types.ACTIONS> availableActions,
			AbstractRoller roller, Random rnd, int simulationDepth) {
		this.availableActions = availableActions
				.toArray(new Types.ACTIONS[Config.NUMBEROFACTIONS]);
		this.currentRolloutDepth = simulationDepth;
		this.random = rnd;
		this.roller = roller;
		knowledgeBaseActive = false;

	}

	/**
	 * the search Algorithm - called at the beginning of every move
	 * 
	 * @param rootState
	 *            the current state of the actual game
	 * @param timer
	 *            to measure the remaining time
	 */
	public Types.ACTIONS search(StateObservation rootState,
			ElapsedCpuTimer timer) {
		this.rootState = rootState;
		this.root = new TreeNode(null, 0, 0);

		if (Config.DEBUGGING) {
			int dimx = rootState.getWorldDimension().width;
			int dimy = rootState.getWorldDimension().height;
			int blockSize = rootState.getBlockSize();
			sampleMap = new double[(int) (dimx / blockSize)][(int) (dimy / blockSize)][3];
		}
		if (positions != null && positions.size() != 0) {
			int index = requiredMove();
			if (index != -1) {
				TreeNode child = new TreeNode(root, index, 1, HUGE_POSITIVE / 2);
				root.addChild(index, child);
			}
		}

		if (knowledgeBaseActive) {
			memory.remember(rootState.getAvatarPosition(),
					rootState.getGameTick());
			memory.forget(rootState.getGameTick());
			if ((memory.isAgentStuck() || !A_star.finished) && Config.USE_MAP) {
				if (A_star.finished) {
					// A_star stops if less than GOAL_SEARCH_ABORT_TIME is left
					// and continues where it left of in the next iteration
					positions = A_star.findPathFromTo(rootState
							.getAvatarPosition(), memory.getNextGoal(rootState.getGameScore())
							.getPosition(), rootState.getObservationGrid(),
							memory, timer, availableActions, rootState
									.getBlockSize());
					memory.resetFootPrints();
				} else {
					positions = A_star.continueSearch(timer);
				}
//				if (positions != null) {
//					System.out.println(positions.toString());
//					System.out.println("We stuck. "
//							+ timer.remainingTimeMillis());
//				}
			}
		}
		initialScore = rootState.getGameScore();

		// currentNode corresponds to current State - currentState is global
		// since it is needed elsewhere
		TreeNode currentNode;
		while (timer.remainingTimeMillis() > Config.ABORT_TIME_START_OF_NEW_SAMPLE) {

			if (knowledgeBaseActive) {
				memory.updateOld(roller.getFeatures(rootState));
			}

			currentNode = treePolicy();

			// run simulation
			double value = simulate(currentNode, currentState, timer);
			// back propagate result of simulation
			propagate(currentNode, value);

			if (Config.DEBUGGING) {
				int avatar_x = (int) (currentState.getAvatarPosition().x / currentState
						.getBlockSize());
				int avatar_y = (int) (currentState.getAvatarPosition().y / currentState
						.getBlockSize());
				sampleMap[avatar_x][avatar_y][0]++;
				// if the highest score is 0 than this is probably because the
				// the initial value is 0.
				if (sampleMap[avatar_x][avatar_y][1] > value
						|| sampleMap[avatar_x][avatar_y][1] == 0) {
					sampleMap[avatar_x][avatar_y][1] = value;
				}
			}

		}

		return availableActions[getBestChildIndex()];
	}

	/* navigates trough tree until leaf is reached */
	private TreeNode treePolicy() {
		TreeNode selected = root;
		currentState = rootState.copy();
		int rolloutCount = 0;
		while (!currentState.isGameOver()
				&& rolloutCount <= currentRolloutDepth) {
			rolloutCount++;
			// if current node is not fully expanded, expand it
			if (!selected.fullyExpanded()) {
				return expand(selected);
			}
			// else select next node in tree
			else {
				selected = selectNext(selected);
				currentState.advance(availableActions[selected.getIndex()]);
			}
		}
		return selected;
	}

	/* the next node is the child with the highest uct value */
	private TreeNode selectNext(TreeNode current) {
		double bestValue = -Double.MAX_VALUE;
		double childValue;
		// Child not null since method is only entered if node fully expanded
		for (TreeNode child : current.getChildren()) {
			// we add noise to choose a child randomly if their UctValue is
			// equal.
			childValue = child.getUctValue(bounds[0], bounds[1]);
			childValue = noise(childValue);
			if (bestValue < childValue) {
				bestValue = childValue;
				current = child;
			}
		}
		return current;
	}

	/*
	 * Expands a node
	 */
	private TreeNode expand(TreeNode toExpand) {
		/* TODO: Add clever heuristic to chose Child */
		int childIndex;
		do {
			childIndex = random.nextInt(Config.NUMBEROFACTIONS);
		} while (toExpand.childAt(childIndex));
		TreeNode newChild = new TreeNode(toExpand, childIndex,
				toExpand.getDepth() + 1);
		toExpand.addChild(childIndex, newChild);
		if (knowledgeBaseActive) {
			memoryUpdate(childIndex);
		} else {
			currentState.advance(availableActions[childIndex]);
		}
		return newChild;
	}

	// save values of prev state to use for memory
	private void memoryUpdate(int childIndex) {
		int nEvents = currentState.getEventsHistory().size();
		double prevScore = currentState.getGameScore();
		AvatarMovement avatarMovement = new AvatarMovement(currentState,
				availableActions[childIndex]);
		currentState.advance(availableActions[childIndex]);
		memory.addInformation(nEvents, prevScore, currentState, roller
				.getFeatures(currentState).getFeatureVector(), avatarMovement);
	}

	/*
	 * Simulates a game until it is finished or MAX Simulation Depth is reached
	 */
	private double simulate(TreeNode node, StateObservation state,
			ElapsedCpuTimer timer) {
		int depth = node.getDepth();
		HashMap<Integer, Double> features = null;
		while (depth < currentRolloutDepth
				&& !state.isGameOver()
				&& timer.remainingTimeMillis() > Config.ABORT_TIME_SINGLE_SIMULATION) {
			int action = roller.roll(state);

			
			// check for stupid action
			Vector2d nextPosition = TuUtils.getNextPositionForAction(state, action);
			ArrayList<Observation> observationsOnNextField = TuUtils.getObservationsOnPosition(state, nextPosition);
			if(knowledgeBaseActive && memory != null && memory.isBlockingOrDeadly(observationsOnNextField)){
				// stupid action => simulate other action
				if(random.nextDouble() > Config.PROBABILITY_CUT_BLOCKING_OR_DEADLY){
					if(Config.DEBUGGING){
					System.out.println("Cut stupid action  " + action + " : " + state.getAvatarPosition().x / state.getBlockSize() +" " + state.getAvatarPosition().y / state.getBlockSize());
					}
					continue;
				}
			}


			int prevEvCount = state.getEventsHistory().size();
			double scorePrev = state.getGameScore();
			AvatarMovement avatarMovement = new AvatarMovement(state,
					availableActions[action]);
			state.advance(availableActions[action]);
			features = roller.getFeatures().getFeatureVector(state);
			if (knowledgeBaseActive) {
				memory.addInformation(prevEvCount, scorePrev, state, features,
						avatarMovement);
			}
			depth++;
		}

		double deltaScore = calcDeltaScore(state);

		if (deltaScore < bounds[0])
			bounds[0] = deltaScore;

		if (deltaScore > bounds[1])
			bounds[1] = deltaScore;

		if (deltaScore == HUGE_POSITIVE || deltaScore == HUGE_NEGATIVE) {
			return deltaScore;
		}

		if (features == null) {
			features = roller.getFeatures(state).getFeatureVector();
		}

		if (deltaScore == 0 && knowledgeBaseActive) {
			Pair<Double, Double> val = memory.getKnowledgeGain(features);
			double infoGain = val.getFirst();
			double disc = val.getSecond();
			return infoGain * Config.DiscScoreWeight + disc
					* (1 - Config.DiscScoreWeight);
		} else {
			if (knowledgeBaseActive) {
				// as long as we get score we are not stuck.
				memory.resetFootPrints();
			}
			return deltaScore;
		}
	}

	private double calcDeltaScore(StateObservation state) {
		boolean gameOver = state.isGameOver();
		Types.WINNER win = state.getGameWinner();
		double rawScore = state.getGameScore();
		rawScore = rawScore - initialScore;

		if (gameOver && win == Types.WINNER.PLAYER_LOSES)
			return HUGE_NEGATIVE;

		if (gameOver && win == Types.WINNER.PLAYER_WINS)
			return HUGE_POSITIVE;

		return rawScore;
	}

	/*
	 * Navigates to tree (from bottom to top) until root is reached. Updates
	 * value in each node.
	 */
	private void propagate(TreeNode node, double value) {
		while (node != null) {
			node.updateScore(value);
			node = node.getParent();
		}
	}

	private int getBestChildIndex() {
		int selected = -1;
		double bestValue = -Double.MAX_VALUE;
		TreeNode[] rootChildren = root.getChildren();
		for (int i = 0; i < rootChildren.length; i++) {
			if (rootChildren[i] != null) {
				double childValue = rootChildren[i].getAverageScore();
				childValue = noise(childValue);
				if (childValue > bestValue) {
					bestValue = childValue;
					selected = i;
				}
			}
		}
		return selected;
	}

	private int requiredMove() {
		Vector2d target = positions.get(0);
		Vector2d pos = rootState.getAvatarPosition();
		while (pos.equals(target) && positions.size() != 0) {
			// yes this makes sense
			positions.remove(0);
			if (positions.size() != 0) {
				target = positions.get(0);
			}
		}
		Vector2d dir = new Vector2d(target.x - pos.x, target.y - pos.y); // overwrites
																			// target
		Types.ACTIONS action = ACTIONS.ACTION_NIL;
		if (dir.x > 0) {
			action = ACTIONS.ACTION_RIGHT;
		}
		if (dir.x < 0) {
			action = ACTIONS.ACTION_LEFT;
		}
		if (dir.y > 0) {
			if (action == ACTIONS.ACTION_NIL) {
				action = ACTIONS.ACTION_DOWN;
			} else {
				if (Math.abs(dir.y) > Math.abs(dir.x)) {
					action = ACTIONS.ACTION_DOWN;
				}
			}

		}
		if (dir.y < 0) {
			if (action == ACTIONS.ACTION_NIL) {
				action = ACTIONS.ACTION_UP;
			} else {
				if (Math.abs(dir.y) > Math.abs(dir.x)) {
					action = ACTIONS.ACTION_UP;
				}
			}
		}
		int index = -1;
		for (int i = 0; i < Config.NUMBEROFACTIONS; i++) {
			if (availableActions[i] == action) {
				index = i;
			}
		}
		return index;
	}

	private double noise(double input) {
		return (input + Config.EPSILON)
				* (1.0 + Config.EPSILON * (random.nextDouble() - 0.5));
	}
}