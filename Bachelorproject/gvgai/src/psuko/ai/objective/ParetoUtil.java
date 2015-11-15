package psuko.ai.objective;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import psuko.math.IntegretyChecker;

public class ParetoUtil {

	//assumes minimization
	public boolean dominates(final List<Double> valuesA, final List<Double> valuesB) {
		
		int dim = IntegretyChecker.checkDimensionMatch(valuesA.size(), valuesB.size());
		
		boolean allEqual = true;
		
		for (int i = 0; i < dim; i++)
		{
			if (valuesA.get(i) < valuesB.get(i))
			{
				return false;
			}
			else if (valuesA.get(i) > valuesB.get(i))
			{
				allEqual = false;
			}
		}
		
		return !allEqual;
	}
	
	//assumes minimization
	public boolean weaklyDominates(final List<Double> valuesA, final List<Double> valuesB) {
		
		int dim = IntegretyChecker.checkDimensionMatch(valuesA.size(), valuesB.size());
		
		for (int i = 0; i < dim; i++)
		{
			if (valuesA.get(i) < valuesB.get(i))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean equals(final List<Double> valuesA, final List<Double> valuesB)
	{
		int dim = IntegretyChecker.checkDimensionMatch(valuesA.size(), valuesB.size());
		
		for (int i = 0; i < dim; i++)
		{
			if (valuesA.get(i) != valuesB.get(i))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static <T> List<T> paretoFront(List<T> solutions, Comparator<T> dominationComparator)
	{
		final List<T> paretoFront = new ArrayList<>();
		
		for (final T solution : solutions)
		{
			if (!isDominated(solution, solutions, dominationComparator))
			{
				paretoFront.add(solution);
			}
		}
		
		return paretoFront;
	}
	
	private static <T> boolean isDominated(T solution, List<T> solutionList, Comparator<T> dominationComparator)
	{
		for (final T compareSolution : solutionList)
		{
			if (dominationComparator.compare(solution, compareSolution) == 1)
			{
				return true;
			}
		}
		return false;
	}
	
}
