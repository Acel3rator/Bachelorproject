package psuko.ai.objective.compare;

import java.util.Comparator;
import java.util.List;

public class DoubleListDominationComparator implements Comparator<List<Double>> {

	@Override
	public int compare(List<Double> list1, List<Double> list2) {

		final int numValues = list1.size();
		
		boolean aDominatesB = false;
		boolean bDominatesA = false;

		for (int i = 0; i < numValues; i++)
		{
			if (list1.get(i) > list2.get(i))
			{
				if (bDominatesA)
				{
					return 0;
				}
				
				aDominatesB = true;
			} 
			else if (list1.get(i) < list2.get(i))
			{				
				if (aDominatesB)
				{
					return 0;
				}
				
				bDominatesA = true;
			}
		}
		
		return (aDominatesB == bDominatesA) ? 0 : aDominatesB ? -1 : 1;
	}
}
