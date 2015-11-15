package psuko.math;

import java.util.List;

public class IntegretyChecker {

	private static double epsilon = 1e-6;
	
	private IntegretyChecker() throws InstantiationException
	{
		throw new InstantiationException("Utility Class");
	}
	
	public static int checkDimensionMatch(final int dimA, final int dimB)
	{
		if (dimA != dimB)
		{
			throw new RuntimeException("Dimension Mismatch: " + dimA + " != " + dimB);
		}
		
		return dimA;
	}
	
	public static void checkValueSumIsOne(final List<Double> values)
	{
		checkValueSumMatchesValue(values, 1.0);
	}
	
	public static void checkValueSumMatchesValue(final List<Double> values, final double matchValue)
	{
		double valSum = 0.0;
		
		for (final Double val : values)
		{
			valSum += val;
		}
		
		if (Math.abs(valSum - matchValue) > epsilon)
		{
			throw new RuntimeException("Sum Violation: " + valSum + " != " + matchValue);
		}
	}
	
	public static void checkBetweenZeroAndOne(final double value)
	{
		checkValueInRange(value, 0.0, 1.0);
	}
	
	public static void checkValueInRange(final double value, final double minBound, final double maxBound)
	{
		if (value < minBound || value > maxBound)
		{
			throw new RuntimeException("Range Violation: " + value + " ! (" + minBound + ", " + maxBound + ")");
		}
	}
	
}
