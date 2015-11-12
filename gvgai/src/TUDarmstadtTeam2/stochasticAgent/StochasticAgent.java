package TUDarmstadtTeam2.stochasticAgent;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import TUDarmstadtTeam2.stochasticAgent.rollers.*;
import TUDarmstadtTeam2.utils.AbstractTUTeams2Player;
import TUDarmstadtTeam2.utils.AvatarMovement;
import TUDarmstadtTeam2.utils.Config;
import ontology.Types;
import tools.ElapsedCpuTimer;
import core.game.Event;
import core.game.StateObservation;

public class StochasticAgent extends AbstractTUTeams2Player {
	public MCTSSearch search;

	public AbstractRoller roller;

	public Random rnd;

	private KnowledgeBase memory;
	private ArrayList<Types.ACTIONS> actionList;
	private boolean knowledgeBaseActive;

	private int blocksize;
	private boolean initDone = false;

	private int wanders = 0;
	private long maxDurationOfWanderIteration = 0;
    
	public StochasticAgent(StateObservation so, ElapsedCpuTimer elapsedTimer,
			int simulationDepth, boolean activateKnowledgeBase, RollerType type) {
		knowledgeBaseActive = activateKnowledgeBase;
		Config.NUMBEROFACTIONS = so.getAvailableActions().size();
		rnd = new Random();
		StateRec stateRecognizer = new StateRec(so);
		roller = getRollerfromType(type, so, stateRecognizer, rnd);
		if (knowledgeBaseActive) {
			memory = new KnowledgeBase(stateRecognizer);
			search = new MCTSSearch(so.getAvailableActions(), roller,
					rnd, memory, simulationDepth);
		} else {
			search = new MCTSSearch(so.getAvailableActions(), roller,
					rnd, simulationDepth);
		}
		blocksize = so.getBlockSize();
		
		maxDurationOfWanderIteration = 0;
		if (knowledgeBaseActive) {
			while (elapsedTimer.elapsedMillis()
					+ Config.SAFETY_TIME_FOR_AGENT_INIT < elapsedTimer
						.remainingTimeMillis()) {
				wander(so.copy(), elapsedTimer);
			}
		}
		System.out.println("Agent init: " + wanders + " steps calculated");

		initDone = true;
	}

	private void wander(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		Random randomGenerator = new Random();
		ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();
		boolean gameOver = stateObs.isGameOver();
		StateObservation lastAdvancedState = null;
		StateObservation stCopy = stateObs;

		long lastDuration = 0;
		long lastEnd = elapsedTimer.remainingTimeMillis();

		while (!gameOver
				&& (elapsedTimer.remainingTimeMillis()
						+ maxDurationOfWanderIteration > Config.SAFETY_TIME_FOR_AGENT_INIT)) {
			int index = randomGenerator.nextInt(actions.size());
			Types.ACTIONS action = actions.get(index);

			lastAdvancedState = stCopy.copy();
			stCopy.advance(action);

			analyzeStateChange(lastAdvancedState, action, stCopy);

			gameOver = stCopy.isGameOver();
			wanders++;
			lastDuration = lastEnd - elapsedTimer.remainingTimeMillis();
			maxDurationOfWanderIteration = Math.max(lastDuration,
					maxDurationOfWanderIteration);
			lastEnd = elapsedTimer.remainingTimeMillis();
		}
		// System.out.println("maxDur: " +maxDurationOfWanderIteration);

	}

	private void analyzeStateChange(StateObservation prev,
			Types.ACTIONS action, StateObservation next) {
		double scorePrev = prev.getGameScore();
		double scoreNext = next.getGameScore();

		double scoreDiff = scoreNext - scorePrev;
		Event ev = memory.retrieveLastUniqueEvent(next); // This is LIKELY the
															// event that caused
															// the game end or
															// score change.
		if (ev != null) {
			AvatarMovement avatarMovement = new AvatarMovement(prev,next, action);
			memory.manageEvent(ev, scoreDiff, next.getGameWinner(), true, avatarMovement);
		}

		// TODO check if action let agent move
		// if(action != Types.ACTIONS.ACTION_USE && action !=
		// Types.ACTIONS.ACTION_NIL)
		// {
		//
		// if(next.getAvatarPosition().equals(prev.getAvatarPosition()))
		// {
		// ev = memory.retrieveLastUniqueEvent(next); //This is LIKELY the event
		// that caused the game end or score change.
		// if(ev != null){
		// //System.out.println("Colliding with " + ev.passiveTypeId +
		// " didn't let me move " + action);
		// //TODO analyze Collisions
		// memory.manageTraverse(ev, false);
		// }
		// }
		// }
	}

	@Override
	public Types.ACTIONS act(StateObservation stateObs,
			ElapsedCpuTimer elapsedTimer) {

		Types.ACTIONS action = search.search(stateObs, elapsedTimer);
		// System.out.println("rem: " + elapsedTimer.remainingTimeMillis());
		return action;
	}

	public void draw(Graphics2D g) {
		if (Config.DEBUGGING) {
			drawSampleEndpositions(g);
		}
	}

	private void drawSampleEndpositions(Graphics2D g) {
		if (!initDone || MCTSSearch.sampleMap == null) {
			return;
		}
		for (int i = 0; i < MCTSSearch.sampleMap.length; ++i) {
			for (int j = 0; j < MCTSSearch.sampleMap[0].length; ++j) {
				double hits = MCTSSearch.sampleMap[i][j][0];
				double highscore = MCTSSearch.sampleMap[i][j][1];
				// double avgscore = MCTSSearch.sampleMap[i][j][2];

				if (hits > 0) {
					// setColor(g,(int)hits);
					g.setFont(new Font("TimesRoman", Font.BOLD, 18));

					int x = (int) (i * blocksize + blocksize * 0.5);
					int y = (int) (j * blocksize + blocksize * 0.5);
					g.drawString("*", x, y);

					g.setFont(new Font("TimesRoman", Font.PLAIN, 10));

					// used to round to the 4. decimal point as the double
					// values are generally to long for the visualization
					double normHighscore = Math.floor(highscore * 1000) / 1000;

					g.drawString("[" + hits + "]", x, y + 10);
					g.drawString("[" + normHighscore + "]", x, y + 20);
					// g.drawString("["+avgscore+"]",x,y+30);
				}

			}
		}
	}

	Color BLACK = new Color(0, 0, 0);
	Color GREY0 = new Color(50, 50, 50);
	Color GREY1 = new Color(100, 100, 100);
	Color GREY2 = new Color(150, 150, 150);
	Color GREY3 = new Color(195, 195, 195);

	private void setColor(Graphics2D g, int hits) {

		if (hits <= 1)
			g.setColor(GREY2);
		else if (hits <= 4)
			g.setColor(GREY1);
		else if (hits <= 8)
			g.setColor(GREY0);
		else if (hits <= 16)
			g.setColor(BLACK);
	}

	private AbstractRoller getRollerfromType(RollerType type,
			StateObservation ob, StateRec features, Random rand) {
		switch (type) {
		case EvoRoller:
			return new EvoRoller(ob, features, rand);
			/*
			 * case RepeatRoller: return new RepeatRoller(ob,features,rand);
			 */
		default:
			return new RandomRoller(ob, features, rand);
		}
	}

    
}
