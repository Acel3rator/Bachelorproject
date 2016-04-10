package TUDarmstadtTeam2.stochasticAgent;

import ontology.Types.ACTIONS;
import TUDarmstadtTeam2.utils.Config;
import TUDarmstadtTeam2.utils.TuUtils;
import tools.Vector2d;

/**
 * Tree node
 *
 */
public class TreeNode {
	/* Tree structure */
	private TreeNode parent;
	private TreeNode[] children;
	/* the index of this node in the child array of its parent */
	private int index;
	/* values corresponding to node */
	private double score;
	private int nrVisited;
	private int nrChildren;
	private int depth;

	// private double epsilon = 1e-6;

	public TreeNode(TreeNode parent, int childIndex, int depth) {
		this.parent = parent;
		this.index = childIndex;
		this.depth = depth;
		children = new TreeNode[Config.NUMBEROFACTIONS];
		score = 0;
		nrVisited = 0;
		nrChildren = 0;
	}

	public TreeNode(TreeNode parent, int childIndex, int depth,double bias) {
		this(parent,childIndex,depth);
		score = bias;
		nrVisited = 1;
	}

	/**
	 * Updates the score and increases number of visits recalculates uct value
	 * 
	 * @param value
	 */
	public void updateScore(double value) {
		nrVisited++;
		score += value;
	}

	/**
	 *
	 * @return whether this node is fully expanded
	 */
	public boolean fullyExpanded() {
		return (nrChildren == children.length);
	}

	public boolean childAt(int index) {
		return children[index] != null;
	}

	/**
	 * Adds a child
	 * 
	 * @param index
	 *            field of the array at which the child is added
	 * @param child
	 */
	public void addChild(int index, TreeNode child) {
		if (children[index] == null) {
			nrChildren++;
		}
		children[index] = child;
	}

	/* Getter */
	public int getIndex() {
		return this.index;
	}

	public double getUctValue(double lobound, double upbound) {
		double normScore = TuUtils.normalise(score
				/ (nrVisited + Config.EPSILON), lobound, upbound);
		return normScore
				+ Config.C
				* Math.sqrt(Math.log(parent.nrVisited)
						/ (nrVisited + Config.EPSILON));
	}

	public TreeNode getParent() {
		return parent;
	}

	public TreeNode[] getChildren() {
		return this.children;
	}

	public double getAverageScore() {
		return this.score / nrVisited;
	}

	public int getNum() {
		return this.nrChildren;
	}
	
	public int getDepth(){
		return depth;
	}

	public int getNrVisits() {
		return nrVisited;
	}

}
