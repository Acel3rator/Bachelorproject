package TUDarmstadtTeam2;

import TUDarmstadtTeam2.Search.SearchAgent;
import TUDarmstadtTeam2.SearchOld.SearchAgentOld;
import TUDarmstadtTeam2.stochasticAgent.StochasticAgent;
import TUDarmstadtTeam2.stochasticAgent.rollers.RollerType;
import TUDarmstadtTeam2.utils.AbstractTUTeams2Player;
import TUDarmstadtTeam2.utils.Config;
import TUDarmstadtTeam2.utils.GameClass;
import TUDarmstadtTeam2.utils.GameClassifier;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

/**
 * Created by philipp on 15.05.15.
 */
public class Agent extends AbstractPlayer {
	GameClass chosenClass;
	GameClassifier classifier;
	AbstractTUTeams2Player chosenAgent;

	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
		classifier = new GameClassifier(3, 3, 8);
		chosenClass = classifier.classify(so);
		if (Config.PRINT_OUTPUTS) {
			System.out.println("Chosen: " + chosenClass);
		}
		chosenAgent = getPlayerFromClass(chosenClass, so, elapsedTimer);
		while (elapsedTimer.elapsedMillis() < Config.Agent_Restart_Threshold
				&& !chosenAgent.isPlaying()) {
			if (Config.PRINT_OUTPUTS) {
				System.out.println("Reclassification Requested");
			}
			chosenClass = classifier.reclassify(chosenClass, so);
			chosenAgent = getPlayerFromClass(chosenClass, so, elapsedTimer);
		}

	}

	@Override
	public Types.ACTIONS act(StateObservation stateObs,
			ElapsedCpuTimer elapsedTimer) {
		if (chosenAgent.isPlaying()) {
			return chosenAgent.act(stateObs, elapsedTimer);
		} else {
			chosenClass = classifier.reclassify(chosenClass, stateObs);
			chosenAgent = getPlayerFromClass(chosenClass, stateObs,
					elapsedTimer);
			// Todo: change
			return Types.ACTIONS.ACTION_NIL;

		}
	}

	private AbstractTUTeams2Player getPlayerFromClass(GameClass chosenClass,
			StateObservation so, ElapsedCpuTimer elapsedTimer) {
		switch (chosenClass) {
		case DETERMINISTIC_GAME:
			return chosenAgent = new SearchAgent(so, elapsedTimer);
		case DETERMINISTIC_BACKUP:
			return chosenAgent = new StochasticAgent(so, elapsedTimer,
					Config.SIMULATION_DEPTH_FOR_BACKUPGAME, false,
					RollerType.RandomRoller);
		default: // stochasticGame or other
			return chosenAgent = new StochasticAgent(so, elapsedTimer,
					Config.INIT_ROLLOUT_DEPTH_STORACHSTIC_AGENT, true, RollerType.RandomRoller);
		}
	}
}
