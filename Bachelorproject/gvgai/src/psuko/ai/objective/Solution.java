package psuko.ai.objective;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import psuko.math.IntegretyChecker;

/*
Interface:
	List<Double> values();
	List<Double> normalisedValues();
	List<Double> normalisedWeightedValues();
	
	double normalisedWeightedSum();
	
	boolean dominates(Solution other);
	boolean weaklyDominates(Solution other);
 */

public class Solution {

	protected final SortedMap<BaseObjective, Double> objValMap = new TreeMap<>();
	
//	protected final List<BaseObjective> objectives;
//	protected final List<Double> values;

	protected Solution(final List<BaseObjective> objectives, final List<Double> values) {
//		this.objectives = objectives;
//		this.values = values;
		
		for (int i = 0; i < objectives.size(); i++)
		{
			objValMap.put(objectives.get(i), values.get(i));
		}
	}
	
	private Solution(final Solution moSol)
	{		
		this.objValMap.putAll(moSol.objValMap);
		
//		this.objectives = new ArrayList<>();
//		this.values = new ArrayList<>();
//		
//		for (final Double values : moSol.values)
//		{
//			this.values.add(values);
//		}
//		
//		for (final BaseObjective objective : moSol.objectives)
//		{
//			this.objectives.add(objective);
//		}
	}
	
	public final List<Double> values() {
		return new ArrayList<Double>(this.objValMap.values());
		
//		return this.values;
	}

	public final List<Double> normalisedValues() {
		
		final List<Double> nValues = new ArrayList<>();
		
		for (Map.Entry<BaseObjective, Double> entry : this.objValMap.entrySet())
		{
			nValues.add(entry.getKey().normalizedSolution(entry.getValue()));
		}
		
		return nValues;
		
//		final List<Double> nwValues = new ArrayList<>();
//		
//		for (int i = 0; i < this.values.size(); i++)
//		{
//			nwValues.add(this.objectives.get(i).normalizedSolution(this.values.get(i)));
//		}
//		
//		return nwValues;
	}
	
	public final List<Double> normalisedWeightedValues() {
		
		final List<Double> nwValues = new ArrayList<>();
		
		for (Map.Entry<BaseObjective, Double> entry : this.objValMap.entrySet())
		{
			nwValues.add(entry.getKey().normalizedWeightedSolution(entry.getValue()));
		}
		
		return nwValues;
		
//		final List<Double> nwValues = new ArrayList<>();
//		
//		for (int i = 0; i < this.values.size(); i++)
//		{
//			nwValues.add(this.objectives.get(i).normalizedWeightedSolution(this.values.get(i)));
//		}
//		
//		return nwValues;
	}

	public final double normalisedWeightedSum() {
		
		double sum = 0.0;

		for (final double val : this.normalisedWeightedValues()) {
			sum += val;
		}

		return sum;
	}
	

//	final void addObjective(final BaseObjective objective, final double value) {
//		this.objectives.add(objective);
//	}
//
//	final void removeObjective(final BaseObjective objective) {
//		final int idx = objectives.indexOf(objective);
//		
//		this.objectives.remove(idx);
//		this.values.remove(idx);
//	}





	public final void integrateSolution(Solution other, double oneMinusAlpha) {
		
		for (Map.Entry<BaseObjective, Double> entry : this.objValMap.entrySet())
		{
			entry.setValue(oneMinusAlpha * entry.getValue() + (1.0 - oneMinusAlpha) * other.objValMap.get(entry.getKey()));
		}
		
//		IntegretyChecker.checkBetweenZeroAndOne(alpha);
//		final int dim = IntegretyChecker.checkDimensionMatch(this.values.size(), other.values.size());
//		
//		for (int i = 0; i < dim; i++)
//		{
//			this.values.set(i, this.values.get(i) * alpha + other.values.get(i) * (1.0 - alpha));
//		}
	}
	
	public final Solution copy()
	{
		return new Solution(this);
	}

	public boolean dominates(Solution other) {
		
		final List<Double> thisValues = this.values();
		final List<Double> otherValues = other.values();

		int dim = IntegretyChecker.checkDimensionMatch(thisValues.size(), otherValues.size());
		
		boolean allEqual = true;;
		
		for (int i = 0; i < dim; i++)
		{
			if (thisValues.get(i) > otherValues.get(i))
			{
				return false;
			}
			else if (thisValues.get(i) < otherValues.get(i))
			{
				allEqual = false;
			}
		}
		
		return !allEqual;
	}
	
	public boolean weaklyDominates(Solution other) {
		
		final List<Double> thisValues = this.values();
		final List<Double> otherValues = other.values();

		int dim = IntegretyChecker.checkDimensionMatch(thisValues.size(), otherValues.size());
		
		for (int i = 0; i < dim; i++)
		{
			if (thisValues.get(i) > otherValues.get(i))
			{
				return false;
			}
		}
		
		return true;
	}

}
