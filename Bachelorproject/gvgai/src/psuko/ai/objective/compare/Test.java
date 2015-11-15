package psuko.ai.objective.compare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import psuko.ai.objective.ParetoUtil;

public class Test {

	public static void main(String[] args) {
		
		List<List<Double>> solutions = new ArrayList<>();
		
		solutions.add(Arrays.asList(new Double[]{1.0, 2.0, 1.0}));
		solutions.add(Arrays.asList(new Double[]{1.0, 1.0, 1.0}));
		solutions.add(Arrays.asList(new Double[]{2.0, 1.0, 1.0}));
		solutions.add(Arrays.asList(new Double[]{2.0, 1.0, 1.0}));
		solutions.add(Arrays.asList(new Double[]{1.0, 1.0, 1.0}));
		
		for (List<Double> paretoOptimalSolution : ParetoUtil.<List<Double>>paretoFront(solutions, new DoubleListDominationComparator()))
		{
			System.out.println(paretoOptimalSolution);
		}	
	}

}
