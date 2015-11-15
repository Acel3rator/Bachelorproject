package psuko.adaption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import ontology.Types.ACTIONS;
import ontology.Types.WINNER;
import psuko.adaption.Knowledge.GameStatus;
import tools.Vector2d;
import core.game.Event;
import core.game.Observation;
import core.game.StateObservation;

public class KnowledgeBase {
	
	private static KnowledgeBase INSTANCE;
	
	private KnowledgeBase()
	{
		//
	}
	
	public static KnowledgeBase getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new KnowledgeBase();
		}
		
		return INSTANCE;
	}
	
//	protected class EventIdent
//	{
//
//		public final int activeTypeId;
//	    public final int passiveTypeId;
//	    
//	    public EventIdent(final int activeTypeId, final int passiveTypeId)
//	    {
//	    	this.activeTypeId = activeTypeId;
//	    	this.passiveTypeId = passiveTypeId;
//	    }
//	    
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + activeTypeId;
//			result = prime * result + passiveTypeId;
//			return result;
//		}
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			EventIdent other = (EventIdent) obj;
//			if (activeTypeId != other.activeTypeId)
//				return false;
//			if (passiveTypeId != other.passiveTypeId)
//				return false;
//			return true;
//		}
//		
//	    @Override
//		public String toString() {
//			return "EventIdent [activeTypeId=" + activeTypeId
//					+ ", passiveTypeId=" + passiveTypeId + "]";
//		}
//	}
	
	protected class TempInfo
	{
		public final double score;
		public final WINNER winner;
		public final Vector2d avPos;
		public final int gametick;
		
		public TempInfo(StateObservation stateObs)
		{
			this.score = stateObs.getGameScore();
			this.winner = stateObs.getGameWinner();
			this.avPos = stateObs.getAvatarPosition();
			this.gametick = stateObs.getGameTick();
		}
		
	}
	
	public final Map<ACTIONS, Knowledge> actionKnowledge = new EnumMap<>(ACTIONS.class);
	public final Map<Integer, Knowledge> eventKnowledge = new HashMap<>();	

	public TempInfo beforeTempInfo;
	public TempInfo baseTempInfo;
	
	public int lastTickScored = 0;
	public int currTick = 0;
	public double currScore = 0.0;
	public double averageTicksBetweenScores = 0.0;
	public double averageTicksBetweenScoresLately = 0.0;
	public int timesScored = 0;
	public Vector2d lastPosScored;
	
	public List<Boolean> scored = new ArrayList<>();
	public int interval = 50;
	
	void updateBase(StateObservation stateObs)
	{
		this.baseTempInfo = new TempInfo(stateObs);
		
		if (baseTempInfo.gametick != this.currTick)
		{
			currTick = baseTempInfo.gametick;
			if (baseTempInfo.score > currScore)
			{
				timesScored++;
				lastTickScored = currTick;
				scored.add(Boolean.TRUE);
			}
			else
			{
				scored.add(Boolean.FALSE);
			}

			averageTicksBetweenScores = (double) (baseTempInfo.gametick + 1) / (timesScored + 1);
			if (scored.size() >= interval)
			{
				scored = scored.subList(1, scored.size());
			}
			averageTicksBetweenScoresLately = ((double) scored.size() + 1) / (Collections.frequency(scored, Boolean.TRUE) + 1);
		}
		currScore = baseTempInfo.score;
	}
	
	void updateBefore(StateObservation stateObs)
	{
		this.beforeTempInfo = new TempInfo(stateObs);
	}
	
	void updateAfter(StateObservation stateObs, ACTIONS appliedAction)
	{
		final TempInfo afterTempInfo = new TempInfo(stateObs);
		
		final double scoreDiff = afterTempInfo.score - this.beforeTempInfo.score;
		
		final GameStatus gameStatusChange;
		
		if (this.beforeTempInfo.winner != WINNER.PLAYER_WINS && afterTempInfo.winner == WINNER.PLAYER_WINS)
		{
			gameStatusChange = GameStatus.WON;
		} 
		else if (this.beforeTempInfo.winner != WINNER.PLAYER_LOSES && afterTempInfo.winner == WINNER.PLAYER_LOSES)
		{
			gameStatusChange = GameStatus.LOST;
		}
		else
		{
			gameStatusChange = GameStatus.NONE;
		}

		for (Iterator<Event> iter = stateObs.getEventsHistory().descendingIterator(); iter.hasNext();)
		{
			Event ev = iter.next();
			
			if (ev.gameStep != stateObs.getGameTick() - 1)
			{
				break;
			}
			
//			final EventIdent evId = new EventIdent(ev.activeTypeId, ev.passiveTypeId);
			
			final Knowledge k;
			
			if (this.eventKnowledge.containsKey(ev.passiveTypeId))
			{
				k = this.eventKnowledge.get(ev.passiveTypeId);
			}
			else
			{
				k = new Knowledge();
			}
			
			k.update(gameStatusChange, scoreDiff);
			
			this.eventKnowledge.put(ev.passiveTypeId, k);
		}
		
		final Knowledge k;
		
		if (this.actionKnowledge.containsKey(appliedAction))
		{
			k = this.actionKnowledge.get(appliedAction);
		} else
		{
			k = new Knowledge();
		}
		k.update(gameStatusChange, scoreDiff);
		
		this.actionKnowledge.put(appliedAction, k);
		
		
//		debug(stateObs);
		
	}
	
	public void debug(StateObservation stateObs)
	{
		System.out.println("_____DEBUGGING KnowledgeBase");
		System.out.println("...ACTIONS:");
		
		for (Map.Entry<ACTIONS, Knowledge> entry : this.actionKnowledge.entrySet())
		{
			System.out.println(entry.getKey().toString() + " - " + entry.getValue().toString());
		}
		
		System.out.println("...Events");
		
		for (Map.Entry<Integer, Knowledge> entry : this.eventKnowledge.entrySet())
		{
			System.out.println(entry.getKey().toString() + " - " + entry.getValue().toString());
		}
		
//		ArrayList<Observation>[] npcs = stateObs.getNPCPositions();
		
//		for (ArrayList<Observation> npc : npcs)
//		{
//			System.out.println(npc.get(0).itype);
//		}
		
		System.out.println("times scored: " + timesScored + 
				" - last scored: " + lastTickScored + 
				" - ticks between scores: " + this.averageTicksBetweenScores + " (" + averageTicksBetweenScoresLately + ")");
		
	}

}
