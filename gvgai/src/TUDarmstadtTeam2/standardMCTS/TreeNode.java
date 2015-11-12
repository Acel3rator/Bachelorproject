package TUDarmstadtTeam2.standardMCTS;


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
    private double uctValue;
    private int nrVisited;
    private int nrChildren;

    public TreeNode(TreeNode parent, int childIndex) {
        this.parent = parent;
        this.index = childIndex;
        children = new TreeNode[Agent.NUMBEROFACTIONS];
        score = 0;
        nrVisited = 0;
        nrChildren = 0;
    }

    /**
     * Updates the score and increases number of visits
     * recalculates uct value
     * @param value
     */
    public void updateScore(double value) {
        /*TODO add clever way to calc score */
        nrVisited++;
        score += value;
        uctValue = score + Agent.C * Math.sqrt(Math.log(nrVisited)/nrVisited);
    }

    /**
     *
     * @return whether this node is fully expanded
     */
    public boolean fullyExpanded(){
        return (nrChildren == children.length);
    }
    public boolean childAt(int index){
        return children[index] != null;
    }

    /**
     * Adds a child
     * @param index field of the array at which the child is added
     * @param child
     */
    public void addChild(int index, TreeNode child){
        if(children[index] == null){
            nrChildren ++;
        }
        children[index]  = child;
    }
    /*Getter */
    public int getIndex(){
        return this.index;
    }
    public double getUctValue(){
        return this.uctValue;
    }

    public TreeNode getParent() {
        return parent;
    }
    public TreeNode[] getChildren(){
        return this.children;
    }
    public double getScore(){
        return this.score;
    }
}
