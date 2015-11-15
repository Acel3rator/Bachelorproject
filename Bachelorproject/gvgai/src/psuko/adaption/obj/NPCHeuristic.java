package psuko.adaption.obj;

import java.awt.Dimension;
import java.util.ArrayList;

import psuko.adaption.Knowledge;
import psuko.adaption.KnowledgeBase;
import tools.Vector2d;
import core.game.Observation;
import core.game.StateObservation;

public class NPCHeuristic extends AbstractGVGStateObjective {

	public NPCHeuristic() {
		super("NPCS", Sign.MAXIMIZE);
	}
	
	private double badReward = -0.75;
	private double goodReward = 0.75;

	@Override
	protected double computeHeuristicValue(StateObservation actualState) {
		
		final ArrayList<Observation>[] observableLists = actualState.getNPCPositions(actualState.getAvatarPosition());
		
		if (observableLists == null)
		{
			return 0.0;
		}
		
		final Vector2d avPos = actualState.getAvatarPosition();
		final Dimension worldDim = actualState.getWorldDimension();
		final double height = worldDim.getHeight();
		final double width = worldDim.getWidth();
		final double blockSize = actualState.getBlockSize();
		
		double nearest = Double.MAX_VALUE;
		
		int counter = 0;
		double badDistCount = 0.0;
		
		for (ArrayList<Observation> observableList : observableLists) {
			for (Observation observable : observableList)
			{				
				double xDist = Math.abs(observable.position.x - avPos.x) / blockSize;
				double yDist = Math.abs(observable.position.y - avPos.y) / blockSize;
				double mhDist = xDist + yDist;
				
				if (KnowledgeBase.getInstance().eventKnowledge.containsKey(observable.itype))
				{
					final Knowledge k = KnowledgeBase.getInstance().eventKnowledge.get(observable.itype);
					
					if (k.reward < badReward && mhDist < 3)
					{
						badDistCount += 3 - mhDist;
					}
					else if (k.reward > this.goodReward)
					{
						nearest = (mhDist < nearest) ? mhDist : nearest;
						counter ++;
					}
					else
					{
						//ignore
					}
				}
				else
				{
					//ignore
//					nearest = (mhDist < nearest) ? mhDist : nearest;
//					counter ++;
				}
			}
		}
		
		if (badDistCount > 0)
		{
			return - badDistCount;
		}
		if (nearest != Double.MAX_VALUE)
		{
			return - nearest / (height + width);
		}
		
		return 0.0;
	}

	@Override
	protected void updateHeuristic(StateObservation actualState) {
		//-
	}

}
