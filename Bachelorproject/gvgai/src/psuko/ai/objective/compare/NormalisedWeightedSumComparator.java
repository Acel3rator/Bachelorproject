package psuko.ai.objective.compare;

import java.util.Comparator;

import psuko.ai.objective.Solution;

public class NormalisedWeightedSumComparator implements Comparator<Solution> {

	@Override
	public int compare(Solution mosA,
			Solution mosB) {

		return Double.compare(mosA.normalisedWeightedSum(), mosB.normalisedWeightedSum());
	}
}