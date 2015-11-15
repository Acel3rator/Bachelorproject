package psuko.ai.objective;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import psuko.math.IntegretyChecker;

public abstract class BaseMultiObjectiveProvider {

	final LinkedHashMap<BaseObjective, Double> orderedObjectiveWeightMap;
	
	BaseMultiObjectiveProvider(final List<BaseObjective> objectives, final List<Double> weights)
	{
		final int dim = IntegretyChecker.checkDimensionMatch(objectives.size(), weights.size());
		
		this.orderedObjectiveWeightMap = new LinkedHashMap<>();
		
		for (int i = 0; i < dim; i++)
		{
			this.orderedObjectiveWeightMap.put(objectives.get(i), weights.get(i));
		}
		
		this.normaliseWeights();
		this.setWeightsToObjectives();
	}
	
	BaseMultiObjectiveProvider(final LinkedHashMap<BaseObjective, Double> orderedObjectiveWeightMap)
	{
		this.orderedObjectiveWeightMap = orderedObjectiveWeightMap;
		this.normaliseWeights();
		this.setWeightsToObjectives();
	}
	
	public void setWeightsToObjectives()
	{
		for (Map.Entry<BaseObjective, Double> entry : this.orderedObjectiveWeightMap.entrySet())
		{
			entry.getKey().updateWeight(entry.getValue());
		}
	}
	
	final double getWeight(final BaseObjective objective)
	{
		return this.orderedObjectiveWeightMap.get(objective);
	}
	
	private void normaliseWeights()
	{
		double weightSum = 0.0;
		
		for (Map.Entry<BaseObjective, Double> entry : this.orderedObjectiveWeightMap.entrySet())
		{
			weightSum += entry.getValue();
		}
		
		if (weightSum == 0.0)
		{
			throw new RuntimeException("No weights given! (sum of weights == 0)");
		}
		
		for (Map.Entry<BaseObjective, Double> entry : this.orderedObjectiveWeightMap.entrySet())
		{
			entry.setValue(entry.getValue() / weightSum);
		}
	}
	
}
