package psuko.ai.genetics.abstr;


public interface BaseIndividualBreeder<T, I extends BaseIndividual<T>> {

	I createIndividual();
	
}
