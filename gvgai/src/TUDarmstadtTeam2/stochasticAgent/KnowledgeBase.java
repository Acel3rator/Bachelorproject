package TUDarmstadtTeam2.stochasticAgent;

import java.util.*;

import ontology.Types;
import tools.Vector2d;
import TUDarmstadtTeam2.utils.AvatarMovement;
import TUDarmstadtTeam2.utils.Cantor;
import TUDarmstadtTeam2.utils.Config;
import TUDarmstadtTeam2.utils.FootPrint;
import TUDarmstadtTeam2.utils.Pair;
import core.game.Event;
import core.game.Observation;
import core.game.StateObservation;

/**
 * Inspired by Diego Perez Algorithm: https://github.com/diegopliebana/EvoMCTS
 */
public class KnowledgeBase {


	private HashMap<Integer, KnowledgeItem> memory;
	private HashMap<Integer, KnowledgeItem> pastMemory;
	private HashMap<Integer, Double> pastDistances;
	private FootPrint[] footprints;
	private StateRec features;
	private double epsilon = 1e-6;
	private StateRec stateFeatures;
	private HashSet<Integer> visitedITypes;
	private double lastScoreForBeforeGoalsearch = 0;
	private ArrayList<Integer> itypeGoalQueue;

	public KnowledgeBase(StateRec feat) {
		visitedITypes = new HashSet<Integer>();
		memory = new HashMap<Integer, KnowledgeItem>();
		pastMemory = new HashMap<Integer, KnowledgeItem>();
		pastDistances = new HashMap<Integer, Double>();
		footprints = new FootPrint[Config.MAX_NUMBER_OF_POSITIONS_TO_REMEMBER];
		features = feat;
	}

	public void manageGameEnd(StateObservation prev, StateObservation next) {
		double scorePrev = prev.getGameScore();
		double scoreNext = next.getGameScore();

		double scoreDiff = scoreNext - scorePrev;
		Event ev = retrieveLastUniqueEvent(next); // This is LIKELY the event
													// that caused the game end
													// or score change.

		if (ev != null) {
			AvatarMovement avatarMovement = new AvatarMovement(prev, next, null);
			manageEvent(ev, scoreDiff, next.getGameWinner(), true,
					avatarMovement);
		}
	}

	public Event retrieveLastUniqueEvent(StateObservation stObs) {
		Event event = null;

		TreeSet<Event> events = stObs.getEventsHistory();
		if (events != null && events.size() > 0) {
			Iterator<Event> it = events.descendingSet().iterator();

			event = it.next();

			if (event.gameStep != stObs.getGameTick() - 1) {
				return null; // do not use previous events if current action did
								// not yield an event
			}
			int gameStep = event.gameStep;

			if (it.hasNext()) {
				Event e = it.next();

				if ((gameStep == e.gameStep)) {
					return null; // A second event with the same gameStep. Not
									// unique, return null.

				} else {
					return event; // Another event with a different gameStep. As
									// it is ordered, is unique, return it.
				}
			}
		}
		return event;
	}

	// TODO check if this conversion form Hashmap<String, Double> to
	// Hashmap<Integer, Double> is necessary or useful.
	private HashMap<Integer, Double> translateFeatures(
			HashMap<Integer, Double> fs) {
		HashMap<Integer, Double> translated = new HashMap<Integer, Double>();

		Iterator<Map.Entry<Integer, Double>> itEntries = fs.entrySet()
				.iterator();
		while (itEntries.hasNext()) {
			Map.Entry<Integer, Double> entry = itEntries.next();
			int key = Cantor.computeY(entry.getKey());
			double dist = entry.getValue();

			translated.put(key, dist);
		}
		return translated;
	}

	private int getIntFromStringKey(String infoType) {
		String[] type = infoType.split(":");
		return Integer.parseInt(type[1]);
	}

	public void updateOld(StateRec featureExtraction) {
		HashMap<Integer, Double> feat = translateFeatures(featureExtraction
				.getFeatureVector());

		pastDistances.clear();
		pastMemory.clear();
		Iterator<Map.Entry<Integer, KnowledgeItem>> itEntries = memory
				.entrySet().iterator();
		while (itEntries.hasNext()) {
			Map.Entry<Integer, KnowledgeItem> entry = itEntries.next();
			Integer key = entry.getKey();
			KnowledgeItem mem = entry.getValue();

			pastMemory.put(key, mem.copy());

			if (feat.containsKey(key)) {
				pastDistances.put(key, feat.get(key));
			}

		}
	}

	public Pair<Double, Double> getKnowledgeGain(
			HashMap<Integer, Double> features) {
		Pair<Double, Double> returnVal = new Pair<Double, Double>(0.0, 0.0);
		if (features == null)
			return returnVal;

		double gain = 0;
		double discoverScore = 0;

		HashMap<Integer, Double> feat = translateFeatures(features);

		Iterator<Map.Entry<Integer, KnowledgeItem>> itEntries = pastMemory
				.entrySet().iterator();
		while (itEntries.hasNext()) {
			Map.Entry<Integer, KnowledgeItem> entry = itEntries.next();
			Integer key = entry.getKey();

			gain += calcGain(entry, key);
			discoverScore += calcDiscoverScore(feat, key);
		}

		returnVal.setFirst(gain);
		returnVal.setSecond(discoverScore);
		return returnVal;
	}

	private double calcGain(Map.Entry<Integer, KnowledgeItem> entry, Integer key) {
		KnowledgeItem memItem = entry.getValue();

		if (memory.containsKey(key)) {
			return memory.get(key).computeGain(memItem);
		} else {
			return memItem.computeGain();
		}
	}

	private double calcDiscoverScore(HashMap<Integer, Double> feat, Integer key) {
		double discoverScore = 0;
		if (pastDistances.containsKey(key) && pastMemory.containsKey(key)
				&& feat.containsKey(key)) {
			int occ = pastMemory.get(key).calcOccurences();
			if (occ == 0) {
				double dist = 1 - (feat.get(key) / (pastDistances.get(key) + epsilon));
				if (Double.isNaN(dist)) {
					dist = 0;
				}

				discoverScore += dist;

			} else if (occ > 0 && pastMemory.get(key).calcAvgScoreChange() >= 0
					&& pastDistances.get(key) > 0) {
				double dist = 1 - (feat.get(key) / pastDistances.get(key));
				double meanSc = pastMemory.get(key).calcAvgScoreChange();

				if (!Double.isNaN(meanSc)) {
					discoverScore += dist * meanSc;
				}

				discoverScore += dist * pastMemory.get(key).calcWinPercentage()
						- dist * pastMemory.get(key).calcLossPercentage();
			}
		}
		return discoverScore;
	}

	public double getPercWins(int key) {
		if (memory.containsKey(key))
			return memory.get(key).calcWinPercentage();
		return 0.0;
	}

	public double getPercLoses(int key) {
		if (memory.containsKey(key))
			return memory.get(key).calcLossPercentage();
		return 0.0;
	}

	public double getTotalMeanScore(int key) {
		if (memory.containsKey(key))
			return memory.get(key).calcAvgScoreChange();
		return 0.0;
	}

	public void forget(int timestamp) {
		Iterator<Map.Entry<Integer, KnowledgeItem>> itEntries = memory
				.entrySet().iterator();
		while (itEntries.hasNext()) {
			Map.Entry<Integer, KnowledgeItem> entry = itEntries.next();
			KnowledgeItem mem = entry.getValue();

			if (mem.getLastModificationStep() + Config.FORGET_TIME < timestamp)
				mem.reset();
		}
	}

	public void addInfoType(String infoType) {
		// TODO why not storing ores/ressources?
		if (!infoType.contains("ores")) {
			int intID = getIntFromStringKey(infoType);
			if (!memory.containsKey(intID)) {
				memory.put(intID, new KnowledgeItem());
			}
		}
	}

	public void addInformation(int prevNumEvents, double prevScore,
			StateObservation soNext, HashMap<Integer, Double> features,
			AvatarMovement avatarMovement) {
		double gainedScore = getGainedScore(soNext, prevScore);
		int numNewEvents = soNext.getEventsHistory().size() - prevNumEvents;
		int totalNewGameEvents = numNewEvents;
		avatarMovement.setNewState(soNext);

		int manEvents = manualCollisions(features, soNext, gainedScore,
				numNewEvents, avatarMovement);

		Iterator<Event> itEvent = soNext.getEventsHistory().descendingSet()
				.iterator();
		while (numNewEvents > 0 && itEvent.hasNext()) {
			Event ev = itEvent.next();
			manageEvent(ev, gainedScore, soNext.getGameWinner(),
					(manEvents + totalNewGameEvents) == 1, avatarMovement);
			numNewEvents--;
		}
	}

	private double getGainedScore(StateObservation soNext, double prevScore) {

		boolean gameLost = soNext.isGameOver()
				&& soNext.getGameWinner() == Types.WINNER.PLAYER_LOSES;
		double gainedScore = 0;
		if (gameLost)
			gainedScore = Config.VALUE_OF_LOST_GAME;
		else
			gainedScore = soNext.getGameScore() - prevScore;
		return gainedScore;
	}

	public void manageEvent(Event ev, double gainedScore, Types.WINNER winner,
			boolean standalone, AvatarMovement avatarMovement) {
		KnowledgeItem mem;
		if (!memory.containsKey(ev.passiveTypeId)) {
			mem = new KnowledgeItem();
			memory.put(ev.passiveTypeId, mem);
		} else {
			mem = memory.get(ev.passiveTypeId);
		}

		boolean itBlocked = (avatarMovement.avatarDidNotMoved() && avatarMovement
				.avatarDidMovementAction());
		mem.addOcc(gainedScore, winner, ev.gameStep, itBlocked);
	}


	private int manualCollisions(HashMap<Integer, Double> features,
			StateObservation soNext, double gainedScore, int nGameEvents,
			AvatarMovement avatarMovement) {
		int blockSize = soNext.getBlockSize();
		Event[] tmpEvents = new Event[features.size()];
		int i = 0;
		int manEvents = 0;

		for (int key : features.keySet()) {
			double dist = features.get(key);

			// check for ores
			if (!(Cantor.computeX(key) == 13) && dist < blockSize) {
				tmpEvents[i++] = new Event(soNext.getGameTick(), false, 1, key,
						0, 0, soNext.getAvatarPosition());
				manEvents++;
			}
		}

		for (i = 0; i < manEvents; ++i) {
			manageEvent(tmpEvents[i], gainedScore, soNext.getGameWinner(),
					(manEvents + nGameEvents) == 1, avatarMovement);
		}

		return manEvents;
	}

	public void remember(Vector2d avatarPosition, int tick) {
		// already in map?
		int minFootPrintIndex = -1;
		int minLastChanged = 50000;
		for (int i = 0; i < footprints.length; i++) {
			FootPrint footprint = footprints[i];

			// there is still place in the list:
			if (footprint == null) {
				footprints[i] = new FootPrint(avatarPosition, tick);
				return;
			}

			// if this position is already in the list:
			if (footprint.isPosition(avatarPosition)) {
				footprint.increaseFootprint(tick);
				return;
			}

			// find minimum value to remove later
			int footPrintLastChanged = footprint.getLastChange();
			if (minLastChanged > footPrintLastChanged) {
				minLastChanged = footPrintLastChanged;
				minFootPrintIndex = i;
			}
		}

		// if we have a new position and the list is full:
		footprints[minFootPrintIndex] = new FootPrint(avatarPosition, tick);
	}

	public boolean isAgentStuck() {
		for (FootPrint footprint : footprints) {
			if (footprint != null
					&& footprint.getValue() >= Config.NUMBER_OF_REPEATED_POSITIONS_UNTIL_STUCK) {
				return true;
			}
		}
		return false;
	}

	public HashMap<Integer, KnowledgeItem> getMemory() {
		return memory;
	}

	public boolean isBlockingOrDeadly(ArrayList<Observation> observations) {

		if(observations == null){
			return false;
		}
		for(Observation obs : observations)
			if(obs != null && isBlocking(obs.itype)){ ;
					return true;				
			}
		return false;
	}

	public Goal getNextGoal(double gameScore) {

		HashMap<Integer, Double> map = features.getDistToObjects();
		updateGaolQueue(map.keySet(), gameScore);

		int goalItype = itypeGoalQueue.get(0);

		// if the goalItype does not exist anymore
		if (!map.containsKey(goalItype)) {
			return getNextGoal(gameScore);
		}

//		System.out.println("goal is: " + goalItype);
		lastScoreForBeforeGoalsearch = gameScore;
		return new Goal(features.getPosFromType(goalItype), goalItype);
	}

	private void updateGaolQueue(Set<Integer> itypes, double gameScore) {
		if (itypeGoalQueue == null) {
			itypeGoalQueue = sort(itypes);
			return;
		}
		// if our score improved compared to last time and the last goal
		// (itypeGaolQueue.get(0)) still exists then we want to search for it
		// again.
		// Otherwise remove the lastGoal from the itypeGoalQueue
		if (!(gameScore > lastScoreForBeforeGoalsearch && itypes
				.contains(itypeGoalQueue.get(0)))) {
			itypeGoalQueue.remove(0);
		}
		if (itypeGoalQueue.isEmpty()) {
			itypeGoalQueue = sort(itypes);
		}

	}

	/**
	 * sort the itypes like this:
	 * 
	 * 1. highest score is first
	 * 
	 * 2. if no score exists sort select those first who have not been seen yet
	 * (are not in memory)
	 * 
	 * 3. if no score and the itype is in memory then sort itypes prefer itypes
	 * that are more rarely seen.
	 * 
	 * not included should be things that kill us
	 * 
	 * @param itypes
	 * @return
	 */
	private ArrayList<Integer> sort(Set<Integer> itypes) {
		ArrayList<Integer> priorityQueue = new ArrayList<Integer>();
		for (Integer itype : itypes) {
			for (int i = 0; true; i++) {
				// end of queue
				if (i == priorityQueue.size()) {
					priorityQueue.add(itype);
					break;
				}

				int otherItype = priorityQueue.get(i);
				if (betterScore(itype, otherItype)) {
					priorityQueue.add(i, itype);
					break;
				} else if (moreUnkown(itype, otherItype)) {
					priorityQueue.add(i, itype);
					break;
				} else if (moreRare(itype, otherItype)) {
					priorityQueue.add(i, itype);
					break;
				}

			}

		}
		return priorityQueue;
	}

	private boolean moreRare(Integer itype, int otherItype) {
		if (memory.containsKey(itype) && memory.containsKey(otherItype)) {
			KnowledgeItem it = memory.get(itype);
			KnowledgeItem ot = memory.get(otherItype);
			return it.calcAvgScoreChange() == 0
					&& (ot.calcAvgScoreChange() < 0 || ot.calcAvgScoreChange() == 0
							&& it.calcOccurences() < ot.calcOccurences());
		}
		return false;
	}

	private boolean moreUnkown(Integer itype, int otherItype) {
		return !memory.containsKey(itype)
				&& (!memory.containsKey(itype) || memory.get(otherItype)
						.calcAvgScoreChange() <= 0);
	}

	private boolean betterScore(Integer itype, int otherItype) {
		if (memory.containsKey(itype)) {
			double avgScoreChange = memory.get(itype).calcAvgScoreChange();
			return avgScoreChange > 0
					&& memory.containsKey(otherItype)
					&& avgScoreChange >= memory.get(otherItype)
							.calcAvgScoreChange();
		}
		return false;
	}

	private boolean isBlocking(int itype) {
		if(memory.containsKey(itype)){
			KnowledgeItem kit = memory.get(itype); 
			return kit.isBlocking();
		}	
		return false;
	}

	public void resetFootPrints() {
		footprints = new FootPrint[Config.MAX_NUMBER_OF_POSITIONS_TO_REMEMBER];
	}
}
