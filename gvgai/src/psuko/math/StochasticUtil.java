package psuko.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StochasticUtil {

	private StochasticUtil() throws InstantiationException
	{
	    throw new InstantiationException("Instances of this type are forbidden. (Utility Class)");
	}
	
	/**
	 * Computes the mean of a given sample list
	 * @param sampleList the list with the values
	 * @return the computed mean
	 */
	public static double getMean(final List<Double> sampleList)
	{
		if (sampleList.size() == 0)
		{
			return 0.0;
		}
		
		double sum = 0.0;
		
		for (Double d : sampleList)
		{
			sum += d;
		}
		
		return sum / sampleList.size();
		
	}
	
	/**
	 * Computes the median of a given sample list
	 * @param sampleList the list with the values
	 * @return the computed median
	 */
	public static double getMedian(final List<Double> sampleList)
	{
		//sort first
		final List<Double> sortedSampling = new ArrayList<>(sampleList);
		Collections.sort(sortedSampling);
		
		final int midIdx = sortedSampling.size() / 2;
		
		if(sortedSampling.size() % 2 == 0)
		{
			return (sortedSampling.get(midIdx - 1)
					+ sortedSampling.get(midIdx))
						/ 2.0;
		}
		//else
		return sortedSampling.get(midIdx);
	}
	
	/**
	 * Computes the first quartile of a given sample list
	 * @param sampleList the list with the values
	 * @return the computed first quartile
	 */
	public static double getFirstQuartile(List<Double> sampleList)
	{
		final List<Double> sortedSampling = new ArrayList<>(sampleList);
		Collections.sort(sortedSampling);
		
		final int midIdx = sortedSampling.size() / 2;
		
		final List<Double> firstHalfList;
		
		if(sortedSampling.size() % 2 == 0)
		{
			firstHalfList = sortedSampling.subList(0, midIdx);
		}
		else
		{
			firstHalfList = sortedSampling.subList(0, midIdx + 1);
		}
		
		return getMedian(firstHalfList);
	}
	
	/**
	 * Computes the third quartile of a given sample list
	 * @param sampleList the list with the values
	 * @return the computed third quartile
	 */
	public static double getThirdQuartile(final List<Double> sampleList)
	{
		final List<Double> sortedSampling = new ArrayList<>(sampleList);
		Collections.sort(sortedSampling);
		
		final int midIdx = sortedSampling.size() / 2;
		
		final List<Double> firstHalfList;
		
		if(sortedSampling.size() % 2 == 0)
		{
			firstHalfList = sortedSampling.subList(midIdx, sortedSampling.size());
		}
		else
		{
			firstHalfList = sortedSampling.subList(midIdx + 1, sortedSampling.size());
		}
		
		
		return getMedian(firstHalfList);
	}
	
	/**
	 * Computes the variance of a given sample list
	 * @param sampleList the list with the values
	 * @return the computed variance
	 */
	public static double getVariance(final List<Double> sampleList)
	{
		if (sampleList.size() <= 1)
		{
			return 0.0;
		}
		
		double sum = 0.0;
		
		final double mean = getMean(sampleList);
		
		for (Double d : sampleList)
		{
			double meanDiff = d - mean;
			sum += meanDiff * meanDiff;
		}
		
		return sum / (sampleList.size() - 1);
	}
	
	/**
	 * Computes the range of a given sample list;
	 * i.e. the distance from smallest to largest value in the list
	 * @param sampleList the list with the values
	 * @return the computed range
	 */
	public static double getRange(final List<Double> sampleList)
	{		
		return Collections.max(sampleList) - Collections.min(sampleList);
	}
	
	/**
	 * Computes the standard deviation of a given sample list
	 * @param sampleList the list with the values
	 * @return the computed standard deviation
	 */
	public static double getStandardDeviation(final List<Double> sampleList)
	{
		return Math.sqrt(getVariance(sampleList));
	}
	
	/**
	 * Computes the standard error of a given sample list
	 * @param sampleList the list with the values
	 * @return the computed standard error
	 */
	public static double getStandardError(final List<Double> sampleList)
	{
		return getStandardDeviation(sampleList) / Math.sqrt(sampleList.size());
	}
	
	//to lazy 
//	public static List<Double> mode(final List<Double> sampleList)
//	{
//		return null;
//	}
	
}
