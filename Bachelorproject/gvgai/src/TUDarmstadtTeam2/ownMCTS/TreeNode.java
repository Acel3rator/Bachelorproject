package TUDarmstadtTeam2.ownMCTS;

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

    public int getNrVisited() {
        return nrVisited;
    }

    private int nrVisited;
    private int nrChildren;
    private int depth;

    public TreeNode(TreeNode parent, int childIndex,int depth) {
        this.parent = parent;
        this.index = childIndex;
        this.depth = depth;
        children = new TreeNode[Agent.NUMBEROFACTIONS];
        score = 0.0;
        nrVisited = 0;
        nrChildren = 0;
        uctValue = 0;
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
        if (parent != null) {
            uctValue = score /nrVisited + Agent.C * Math.sqrt(Math.log(parent.nrVisited) / nrVisited);
        }
    }
    /**
     *
     * @return whether this node is fully expanded
     */
    public boolean fullyExpanded(){
        /*Old Implementation faster but i wasnt sure/
        /*return (nrChildren == Agent.NUMBEROFACTIONS);*/
        for(int i = 0; i < Agent.NUMBEROFACTIONS; i++){
            if(children[i] == null){
                return false;
            }
        }
        return true;
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
    public int getDepth(){return this.depth;}
}

