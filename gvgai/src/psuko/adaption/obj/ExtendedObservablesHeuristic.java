package psuko.adaption.obj;

import java.util.ArrayList;

import psuko.adaption.Knowledge;
import psuko.adaption.KnowledgeBase;
import tools.Utils;
import tools.Vector2d;
import core.game.Observation;
import core.game.StateObservation;

public class ExtendedObservablesHeuristic extends AbstractGVGStateObjective {

	public enum BehaviourType {
		NEAREST, MINIMIZE, MINIMIZE_NEAREST, IGNORE, KEEP_DIST;
	}

	public enum ObsType {
		IMMOVABLE(0, BehaviourType.IGNORE), 
		MOVABLE(2, BehaviourType.IGNORE), 
		RESOURCE(0, BehaviourType.MINIMIZE_NEAREST), 
		NPC(3, BehaviourType.IGNORE), 
		PORTAL(0, BehaviourType.NEAREST);

		private int distCap;
		private BehaviourType defaultBehaviour;

		private ObsType(int distCap, BehaviourType defaultBehaviour) {
			this.distCap = distCap;
			this.defaultBehaviour = defaultBehaviour;
		}

		public int getDistCap() {
			return this.distCap;
		}
		
		public BehaviourType getDefaultBehaviour()
		{
			return this.defaultBehaviour;
		}
	}

	public ExtendedObservablesHeuristic() {
		super("ex-observables", Sign.MAXIMIZE);		
	}

	@Override
	protected double computeHeuristicValue(StateObservation actualState) {

		final ArrayList<Observation>[] imm = actualState
				.getImmovablePositions();
		final ArrayList<Observation>[] mov = actualState.getMovablePositions();
		final ArrayList<Observation>[] res = actualState
				.getResourcesPositions();
		final ArrayList<Observation>[] npc = actualState.getNPCPositions();
		final ArrayList<Observation>[] por = actualState.getPortalsPositions();

		double score = 0.0;

		if (imm != null) {
			for (ArrayList<Observation> obsList : imm) {
				score += scoreForObservables(obsList, ObsType.IMMOVABLE,
						actualState);
			}
		}
		if (mov != null) {
			for (ArrayList<Observation> obsList : mov) {
				score += scoreForObservables(obsList, ObsType.MOVABLE,
						actualState);
			}
		}
		if (res != null) {
			for (ArrayList<Observation> obsList : res) {
				score += scoreForObservables(obsList, ObsType.RESOURCE,
						actualState);
			}
		}
		if (npc != null) {
			for (ArrayList<Observation> obsList : npc) {
				score += scoreForObservables(obsList, ObsType.NPC, actualState);
			}
		}
		if (por != null) {
			for (ArrayList<Observation> obsList : por) {
				score += scoreForObservables(obsList, ObsType.PORTAL,
						actualState);
			}
		}

		System.out.println("score: " + score);

		return score;
	}

	private double scoreForObservables(ArrayList<Observation> observables,
			ObsType obsType, StateObservation stateObs) {

		if (observables == null || observables.isEmpty())
		{
			return 0;
		}
		
		BehaviourType behaviour;
		
		if (!KnowledgeBase.getInstance().eventKnowledge.containsKey(observables.get(0).itype))
		{
			behaviour = obsType.getDefaultBehaviour();
			System.out.println(obsType);
		} 
		else
		{
			final Knowledge k = KnowledgeBase.getInstance().eventKnowledge
					.get(observables.get(0).itype);
			if (k.reward > 0.8)
			{
				behaviour = BehaviourType.NEAREST;
			}
			else if (k.reward < 0.5)
			{
				behaviour = BehaviourType.KEEP_DIST;
			}
			else 
			{
				behaviour = BehaviourType.IGNORE;
			}
		}
		
		Vector2d avPos = stateObs.getAvatarPosition();

		int counter = 0;
		int count = 0;
		double nearest = Double.MAX_VALUE;

		for (Observation obs : observables) {

			counter++;

			double xDist = Math.abs(obs.position.x - avPos.x)
					/ stateObs.getBlockSize();
			double yDist = Math.abs(obs.position.y - avPos.y)
					/ stateObs.getBlockSize();

			double manHD = xDist + yDist;

			if (manHD < nearest) {
				nearest = manHD;
			}

			if (manHD <= obsType.distCap) {
				count += obsType.distCap + 1 - manHD;
			}
		}

		double height = stateObs.getWorldDimension().getHeight(); // *
																	// stateObs.getBlockSize();
		double width = stateObs.getWorldDimension().getWidth(); // *
																// stateObs.getBlockSize();
		nearest = Utils.normalise(nearest, 0, height + width);

		switch(behaviour)
		{
		case IGNORE:
			return 0.0;
		case KEEP_DIST:
			return count;
		case MINIMIZE:
			return - counter;
		case MINIMIZE_NEAREST:
			return - (counter + nearest);
		case NEAREST:
			return - nearest;
		default:
			return - nearest;
		}
	}

	@Override
	protected void updateHeuristic(StateObservation actualState) {
		// nothing to do
	}

}
