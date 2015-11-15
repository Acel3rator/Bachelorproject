package psuko.ai.common;

import java.util.Comparator;

import psuko.ai.objective.Solution;
import psuko.ai.objective.compare.DoubleListDominationComparator;

public class SolutionParetoComparator implements Comparator<Solution> {

	private final DoubleListDominationComparator domComp = new DoubleListDominationComparator();
	
	@Override
	public int compare(Solution solA, Solution solB) {		
		return this.domComp.compare(solA.values(), solB.values());
	}

}
