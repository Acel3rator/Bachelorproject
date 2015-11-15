package psuko.ai.objective;

import psuko.math.IntegretyChecker;

public abstract class BaseObjective implements Comparable<BaseObjective> {

	protected enum Sign
	{
		MINIMIZE, MAXIMIZE;
	}
	
	protected final String objectiveName;
	protected final Sign sign;
	
	protected double minValueBound;
	protected double maxValueBound;
	
	private final double defaultValue = 0.0;

	private double weight = 1.0;
	
	private BaseObjective(final String objectiveName, final Sign sign
			, final double initialMinValueBound, final double initialMaxValueBound)
	{
		this.objectiveName = objectiveName;
		this.sign = sign;
		this.minValueBound = initialMinValueBound;
		this.maxValueBound = initialMaxValueBound;
	}
	
	protected BaseObjective(final String objectiveName, final Sign sign)
	{
		this(objectiveName, sign, Double.MAX_VALUE, - Double.MIN_VALUE);
	}
	
	void updateWeight(final double weight)
	{
		IntegretyChecker.checkBetweenZeroAndOne(weight);
		this.weight = weight;
	}
	
	protected final void updateBounds(final double newValue)
	{
		if (newValue > this.maxValueBound)
		{
			this.maxValueBound = newValue;
		}
		
		if (newValue < this.minValueBound)
		{
			this.minValueBound = newValue;
		}
	}
	
	double normalizedSolution(final double value)
	{
		// prevent division by zero 
		//-> all values that ever occurred were the same
		// or just one value was evaluated yet
		if (this.minValueBound == this.maxValueBound)
		{
			return defaultValue;
		}
		
		return (value - this.minValueBound) 
				/ (this.maxValueBound - this.minValueBound);
	}
	
	double normalizedWeightedSolution(final double value)
	{
		return this.normalizedSolution(value) * this.weight;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((objectiveName == null) ? 0 : objectiveName.hashCode());
		result = prime * result + ((sign == null) ? 0 : sign.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseObjective other = (BaseObjective) obj;
		if (objectiveName == null) {
			if (other.objectiveName != null)
				return false;
		} else if (!objectiveName.equals(other.objectiveName))
			return false;
		if (sign != other.sign)
			return false;
		return true;
	}

	@Override
	public int compareTo(BaseObjective other) {
		if (this.equals(other))
		{
			return 0;
		} else if (other == null)
		{
			return 1;
		} else
		{
			return this.objectiveName.compareTo(other.objectiveName);
		}
	}

	@Override
	public String toString() {
		return "BaseObjective [objectiveName=" + objectiveName + ", sign="
				+ sign + ", minValueBound=" + minValueBound
				+ ", maxValueBound=" + maxValueBound + ", defaultValue="
				+ defaultValue + ", weight=" + weight + "]";
	}
	
}
