package TUDarmstadtTeam2.stochasticAgent;

import TUDarmstadtTeam2.utils.Config;
import ontology.Types;

/**
 * Inspired by Diego Perez Algorithm: https://github.com/diegopliebana/EvoMCTS
 */
public class KnowledgeItem {
	private int FIRST_KNOWLEDGE = 10;

	private int nWins = 0;
	private int nLoses = 0;
	private int nNone = 0;
	private int lastModificationStep = 0;
	// counts the number of times this item blocked us (this is a double so it
	// does not have to double be cast later
	private double blocked = 0.0;

	// TODO improvement idea: granularity standalone/multiples and
	// action/CreatedSprite
	private double scoreChange = 0.0;

	public void addOcc(double scoreChange, Types.WINNER winner,
			int currentGameStep, boolean itBlocked) {
		this.scoreChange += scoreChange;
		this.lastModificationStep = currentGameStep;

		if (winner == Types.WINNER.PLAYER_LOSES) {
			nLoses++;
		} else if (winner == Types.WINNER.PLAYER_WINS) {
			nWins++;
		} else {
			nNone++;
		}

		if (itBlocked) {
			blocked++;
		}
	}

	public double calcAvgScoreChange() {
		if(calcOccurences() != 0) {
			return scoreChange / calcOccurences();
		}
		else{
			return 0;
		}
	}

	public int calcOccurences() {
		return nWins + nLoses + nNone;
	}

	public double calcWinPercentage() {
		if (nWins == 0) {
			return 0;
		} else {
			return nWins / calcOccurences();
		}
	}

	public double calcLossPercentage() {
		if (nLoses == 0) {
			return 0;
		} else {
			return nLoses / calcOccurences();
		}
	}

	private double gain(double pre, double post) {
		if (pre == 0)
			return post * FIRST_KNOWLEDGE;
		return (post / pre) - 1;
	}

	public double computeGain() {
		return gain(0, calcOccurences());
	}

	public double computeGain(KnowledgeItem pastItem) {
		return gain(pastItem.calcOccurences(), calcOccurences());
	}

	public void reset() {
		nWins = 0;
		nLoses = 0;
		nNone = 0;
		scoreChange = 0;
	}

	public KnowledgeItem copy() {
		KnowledgeItem item = new KnowledgeItem();
		item.nWins = this.nWins;
		item.nLoses = this.nLoses;
		item.nNone = this.nNone;
		item.scoreChange = this.scoreChange;
		return item;
	}

	// --------------- GETTERS / SETTERS --------------
	public int getWins() {
		return nWins;
	}

	public int getLoses() {
		return nLoses;
	}

	public int getLastModificationStep() {
		return lastModificationStep;
	}

	public String toPrettyString() {
		StringBuilder builder = new StringBuilder();
		builder.append("score: ").append(scoreChange).append(", wins: ")
				.append(nWins).append(", loses: ").append(nLoses)
				.append(", none: ").append(nNone).append(", lastModStep: ")
				.append(lastModificationStep)
				.append(", blocked: ").append(blocked);
		return builder.toString();
	}

	public boolean isBlocking() {
		double blockedPercentage = blocked / calcOccurences();
//		double deathPercentage = calcLossPercentage(); 
		return blockedPercentage >= Config.BLOCK_PERCENTAGE_THRESHOLD; //|| deathPercentage >= Config.DEATH_PRECENTAGE_THRESHOLD;
	}
}
