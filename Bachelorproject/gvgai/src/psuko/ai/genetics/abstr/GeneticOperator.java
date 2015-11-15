package psuko.ai.genetics.abstr;

import java.util.List;
import java.util.Random;

public interface GeneticOperator<T> {

	List<T> generateOffspring(List<T> candidates, int offspringSize, Random rng);
	
}
