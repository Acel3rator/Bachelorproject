package psuko.adaption;

public class Knowledge {

	public enum GameStatus
	{
		NONE, WON, LOST;
	}
	
	public int occurences = 0;
//	public double scoreSum = 0.0;
	public int numLost = 0;
	public int numWon = 0;
	
	public int numScoredPositive = 0;
	public int numScoredNegative = 0;
	
	public double reward = 0.0;
	
	public Knowledge()
	{
		
	}

	public void update(GameStatus gameStatusChange, double scoreDiff)
	{
		this.occurences ++;
		
		if (scoreDiff > 0.0)
		{
			numScoredPositive ++;
		} else if (scoreDiff < 0.0)
		{
			numScoredNegative ++;
		}
		
		if (gameStatusChange == GameStatus.LOST)
		{
			this.numLost ++;
		}
		else if (gameStatusChange == GameStatus.WON)
		{
			this.numWon ++;
		}
		
		this.reward = (double) numScoredPositive / occurences + 
				(double) numWon / occurences - 
				(double) numScoredNegative / occurences - 
				(double) numLost / occurences;
	}
	
	@Override
	public String toString() {
		return "Knowledge [occurences=" + occurences + ", numLost=" + numLost
				+ ", numWon=" + numWon + ", numScoredPositive="
				+ numScoredPositive + ", numScoredNegative="
				+ numScoredNegative + ", reward=" + reward + "]";
	}

}
