package psuko.ai.objective.compare;

import java.util.Comparator;

import psuko.ai.objective.Solution;

public class DominationComparator implements Comparator<Solution> {

	private final DoubleListDominationComparator domComp = new DoubleListDominationComparator();
	
	@Override
	public int compare(Solution mosA,
			Solution mosB) {

		return domComp.compare(mosA.values(), mosB.values());
	}

}
