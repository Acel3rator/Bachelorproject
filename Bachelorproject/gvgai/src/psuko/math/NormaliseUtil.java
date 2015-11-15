package psuko.math;

import java.util.ArrayList;
import java.util.List;

public final class NormaliseUtil {

	private NormaliseUtil() throws InstantiationException
	{
	    throw new InstantiationException("Instances of this type are forbidden. (Utility Class)");
	}
	
	public static double normaliseValue(double value, double lowerBound,
			double upperBound) {
		
		if (lowerBound == upperBound)
		{
			return lowerBound;
		}
		
		return (value - lowerBound) / (upperBound - lowerBound);
	}
	
	public static final List<Double> getNormalisedWeightVector(final List<Double> weightRatioVector)
	{
		double weightSum = .0;
		for (final Double weight : weightRatioVector)
		{
			weightSum += weight;
		}
		
		final List<Double> normalisedWeights = new ArrayList<>();
		
		for (final Double weight : weightRatioVector)
		{
			normalisedWeights.add(weight / weightSum);
		}
		
		return normalisedWeights;
	}
	
}
