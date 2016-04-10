package psuko.math;

import java.util.List;
import java.util.Random;

public class RouletteWheelSelector<T> {

	public static class SelectableItem<T>
	{
		final T item;
		double relativeProbability;
		
		public SelectableItem(final T item, final double relativeProbability)
		{
			this.item = item;
			this.relativeProbability = relativeProbability;
		}
	}
	
	private final List<SelectableItem<T>> availableItems;
	private final Random rng = new Random();
	
	private final double probabilitySum;
	
	public RouletteWheelSelector(final List<SelectableItem<T>> availableItems)
	{
		this.availableItems = availableItems;
		
		double probSum = 0.0;
		for (final SelectableItem<T> item : availableItems)
		{
			probSum += item.relativeProbability;
		}
		
		this.probabilitySum = probSum;
	}
	
	public T selectItem()
	{
		double randomValue = this.probabilitySum * rng.nextDouble();
		
		double acc = 0.0;
		int idx = 0;
		
		while (acc < randomValue && idx++ < this.availableItems.size())
		{
			acc += this.availableItems.get(idx - 1).relativeProbability;
		}
		
		return this.availableItems.get(idx - 1).item;
	}
}
