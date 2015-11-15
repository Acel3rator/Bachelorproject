package psuko.ai.genetics.old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import psuko.ai.genetics.abstr.ListIndividual;

public abstract class SequenceInvertMutation<T, F, I extends ListIndividual<T, F>> implements DeprecatedMutation<T, F, I> {

	@Override
	public I mutate(I theChosenOne) {
		
		//shallow copy
		final List<T> chromosome = new ArrayList<>(theChosenOne.getChromosome());
		
		int startIdx = this.sequenceStart(chromosome.size());
		int EndIdx = this.sequenceEnd(chromosome.size());
		
		Collections.reverse(chromosome.subList(startIdx, EndIdx));

		return this.createIndividual(chromosome);
	}
	
	public abstract int sequenceStart(int length);
	public abstract int sequenceEnd(int length);
	
	public abstract I createIndividual(List<T> chromosome);
	
}
