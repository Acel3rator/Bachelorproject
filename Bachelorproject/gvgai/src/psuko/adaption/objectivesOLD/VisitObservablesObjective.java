package psuko.adaption.objectivesOLD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tools.Utils;
import tools.Vector2d;
import core.game.Observation;
import core.game.StateObservation;

public class VisitObservablesObjective extends GVGObjective {

	public enum ObservableType
	{
		MOVABLE, IMMOVABLE, NPC, PORTAL, RESOURCE;
	}
	
	public enum ValueType
	{
		NEAREST, MINIMIZE, MINIMIZE_NEAREST;
	}
	
	final Map<Integer, Integer> tabooMap = new HashMap<>();
	final int tabooTime;
	
	private final ObservableType obsType;
	
	private final ValueType valType;
	
//	private int nearestObsID = Integer.MAX_VALUE;
//	private int sameObsIDCounter = 0;
//	private final int sameObsIDCounterMax;
	
	public VisitObservablesObjective(ObservableType obsType, int tabooTime, ValueType valType) {
		super("visit" + obsType.toString(), true);
		
		this.obsType = obsType;
		this.tabooTime = tabooTime;
		this.valType = valType;
//		this.sameObsIDCounterMax = tabooTime / 2;
	}

	private ArrayList<Observation>[] getObservables(StateObservation state)
	{
		switch (this.obsType)
		{
		case IMMOVABLE:
			return state.getImmovablePositions();
		case MOVABLE:
			return state.getMovablePositions();
		case NPC:
			return state.getNPCPositions();
		case PORTAL:
			return state.getPortalsPositions();
		case RESOURCE:
			return state.getResourcesPositions();
		default:
			return state.getPortalsPositions();
		}
	}
	
	@Override
	protected double computeHeuristicValue(StateObservation actualState) {
		
		final Vector2d avPos = actualState.getAvatarPosition();
		
		final ArrayList<Observation>[] observables = this.getObservables(actualState);
			
		if (observables == null || observables.length == 0)
		{
			return 0;
		}
		
		double nearest = Double.MAX_VALUE;
		
		int counter = 0;
		
		double distSum = 0.0;
		
		for (ArrayList<Observation> observableList : observables) {
			for (Observation observable : observableList)
			{
				if (this.tabooMap.containsKey(observable.obsID))
				{
					continue;
				}
				
				counter++;
				double xDist = (observable.position.x - avPos.x);
				double yDist = (observable.position.y - avPos.y);

				double mhDist = observable.position.dist(avPos);//Math.abs(xDist + yDist);
				
				distSum += mhDist;
				
				if (mhDist < nearest)
				{
					nearest = mhDist;
				}
			}
		}
		
//		System.out.println(this.objectiveName + ": " + counter +  " - " + nearest);
		
		if (counter == 0)
		{
			return 0.0;
		}
		
		double height = actualState.getWorldDimension().getHeight() * actualState.getBlockSize();
		double width = actualState.getWorldDimension().getWidth() * actualState.getBlockSize();
		
		nearest = Utils.normalise(nearest, 0, height * height + width * width);
		
		switch(this.valType)
		{
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
		
		final Vector2d avPos = actualState.getAvatarPosition();
		
		final ArrayList<Observation>[] observables = this.getObservables(actualState);
		
		if (observables == null || observables.length == 0)
		{
			return;
		}
		
//		double nearest = Double.MAX_VALUE;
//		int nearestID = Integer.MAX_VALUE;
		
		for (ArrayList<Observation> observableList : observables) {
			for (Observation observable : observableList)
			{
//				double mhDist = observable.position.dist(avPos);//Math.abs(xDist + yDist);
//				
//				if (mhDist < nearest && !(this.tabooMap.containsKey(observable.obsID)))
//				{
//					nearest = mhDist;
//					nearestID = observable.obsID;
//				}
				
				if (avPos.equals(observable.position))
				{
					this.tabooMap.put(observable.obsID, actualState.getGameTick());
				}
				
				if (this.tabooMap.containsKey(observable.obsID) 
						&& this.tabooMap.get(observable.obsID).intValue() + this.tabooTime < actualState.getGameTick())
				{
					this.tabooMap.remove(observable.obsID);
				}
				
			}
		}
		
//		if (nearestID == this.nearestObsID)
//		{
//			sameObsIDCounter++;
//			
//			if (sameObsIDCounter > sameObsIDCounterMax)
//			{
//				this.tabooMap.put(nearestID, actualState.getGameTick());
//				this.nearestObsID = Integer.MAX_VALUE;
//				this.sameObsIDCounter = 0;
//			}
//		}
//		else
//		{
//			this.nearestObsID = nearestID;
//			sameObsIDCounter = 0;
//		}
		
	}
	
}
