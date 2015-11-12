package TUDarmstadtTeam2.stochasticAgent;

import TUDarmstadtTeam2.utils.Pair;
import ontology.Types.ACTIONS;

public class MapNode implements Comparable<MapNode> {

	private double heuristicDistance;
	private Pair<Integer, Integer> position;
	private int stepsTaken;
	private MapNode predecessor;
	private ACTIONS prevAction;

	public MapNode(Pair<Integer, Integer> start, ACTIONS prevAction) {
		stepsTaken = 0;
		this.position = start;
		this.prevAction = prevAction;
	}

	public boolean isAt(Pair<Integer, Integer> position2) {
		return position.getFirst() == position2.getFirst()
				&& position.getSecond() == position2.getSecond();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MapNode) {
			MapNode other = (MapNode) obj;
			return other.isAt(position);
		}
		return false;
	}
	
	/**
	 * Compute the cantor pairing function for giving a vector an unique identifier.
	 * @param x
	 * @param y
	 * @return
	 */
	private int cantor(int x, int y) {
		return (x+y)*(x+y+1)/2 + y;
	}
	
	@Override
	public int hashCode() {
		return cantor(this.getPosition().getFirst(), this.getPosition().getSecond());
	}

	public void update(MapNode currentNode, Pair<Integer, Integer> goal) {
		predecessor = currentNode;
		stepsTaken = currentNode.getStepsTaken() + 1;
		// manhattenDistance is a bit wonky when it comes to which nodes are
		// expanded, but it seems to be as good as the euclidian distance
		heuristicDistance = Math.abs(position.getFirst() - goal.getFirst())
				+ Math.abs(position.getSecond() - goal.getSecond());
		// heuristicDistance = Math.sqrt((position.getFirst() - goal.getFirst()) *
		// (position.getFirst() - goal.getFirst())
		// + (position.getSecond() - goal.getSecond()) *(position.getSecond() -
		// goal.getSecond()));
	}

	public int getStepsTaken() {
		return stepsTaken;
	}

	public double getTotalValue() {
		return (heuristicDistance + stepsTaken);
	}

	public MapNode getPredecessor() {
		return predecessor;
	}

	public void setStepsTaken(int stepsTaken) {
		this.stepsTaken = stepsTaken;
	}

	public Pair<Integer, Integer> getPosition() {
		return position;
	}

	public ACTIONS getprevAction() {
		return prevAction;
	}

	@Override
	public int compareTo(MapNode o) {
		if (this.getTotalValue() < o.getTotalValue())
			return -1;
		if (this.getTotalValue() == o.getTotalValue())
			return 0;
		if (this.getTotalValue() > o.getTotalValue())
			return 1;
		throw new RuntimeException("Two MapNodes are not comparable. "
				+ this.getTotalValue() + " " + o.getTotalValue());	}

}
