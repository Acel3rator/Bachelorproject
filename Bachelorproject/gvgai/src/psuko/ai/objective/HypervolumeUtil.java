package psuko.ai.objective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HypervolumeUtil {

	public static void main(String[] args) {

		List<List<Double>> paretoFront = new ArrayList<>();

//		paretoFront.add(Arrays.asList(new Double[] { 0.5, 0.5 }));
		paretoFront.add(Arrays.asList(new Double[] { 0.6 }));
		System.out.println(calculateHypervolume(paretoFront));

	}
	
	/**
	 * assumes maximazation!
	 */
	public static final double calculateHypervolume(List<List<Double>> paretoFront) {
		
		final int dimension = paretoFront.get(0).size();
		
		if (dimension == 1)
		{
			throw new RuntimeException("Whats the 1-dimensional hypervolume?");
		}
		
		return calculateHypervolume(paretoFront, dimension);
	}
	
	private static final double calculateHypervolume(List<List<Double>> paretoFront,
			int numObjectives) {

		double volume = 0.0;
		double distance = 0.0;

		while (!paretoFront.isEmpty()) {

			List<List<Double>> filteredParetoFront = filterNonDominatedSet(
					paretoFront, numObjectives - 1);

			double tempVolume = (numObjectives < 3) ? filteredParetoFront
					.get(0).get(0) : calculateHypervolume(filteredParetoFront,
					numObjectives - 1);

			double tempDistance = surfaceUnchangedTo(paretoFront,
					numObjectives - 1);
			volume += tempVolume * (tempDistance - distance);
			distance = tempDistance;
			paretoFront = reduceNondominatedSet(paretoFront, numObjectives - 1,
					distance);
		}

		return volume;
	}

	private final static List<List<Double>> filterNonDominatedSet(
			List<List<Double>> front, int numObjectives) {

		List<List<Double>> nondominated = new ArrayList<>();

		for (List<Double> solA : front) {
			boolean dominated = false;
			for (List<Double> solB : nondominated) {
				if (dominates(solB, solA, numObjectives)) {
					dominated = true;
					break;
				}
			}

			if (!dominated) {
				for (Iterator<List<Double>> it = nondominated.iterator(); it
						.hasNext();) {
					List<Double> solB = it.next();
					if (dominates(solA, solB, numObjectives)) {
						it.remove();
					}
				}
				nondominated.add(solA);
			}
		}

		return nondominated;
	}

	private final static boolean dominates(List<Double> solA,
			List<Double> solB, int numObjectives) {

		boolean strong = false;

		for (int i = 0; i < numObjectives; i++) {
			if (solA.get(i) > solB.get(i)) {
				strong = true;
			} else if (solA.get(i) < solB.get(i)) {
				return false;
			}
		}
		return strong;
	}

	private final static double surfaceUnchangedTo(List<List<Double>> front,
			int objectiveIdx) {

		double value = Double.MAX_VALUE;

		for (List<Double> solution : front) {
			final double solValue = solution.get(objectiveIdx);

			if (solValue < value) {
				value = solValue;
			}
		}

		return value;
	}

	private final static List<List<Double>> reduceNondominatedSet(
			List<List<Double>> front, int objectiveIdx, double tresDistance) {

		List<List<Double>> result = new ArrayList<>();

		for (List<Double> solution : front) {
			if (solution.get(objectiveIdx) > tresDistance) {
				result.add(solution);
			}
		}
		return result;
	}

}
