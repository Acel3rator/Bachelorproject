package shallowThought.subagents.breadthFS;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class SingleTreeNode
{
    private static final double HUGE_NEGATIVE = -10000000.0;
    private static final double HUGE_POSITIVE =  10000000.0;
    public static double epsilon = 1e-6;
    public static double egreedyEpsilon = 0.05;
    public SingleTreeNode parent;
    public SingleTreeNode[] children;
    public double totValue;
    public int nVisits;
    public static Random m_rnd;
    public int m_depth;
    protected static double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
    public int childIdx;
    public ArrayList<Integer> actionsSoFar = new ArrayList<Integer>();

    public StateObservation state;
    
    public SingleTreeNode(StateObservation so, Random rnd) {
    	this(null, -1, so, rnd);
    }
    
    public SingleTreeNode(SingleTreeNode parent, int childIdx, StateObservation so, Random rnd) {
        this.parent = parent;
        this.m_rnd = rnd;
        this.state = so;
        if (parent != null) this.actionsSoFar = new ArrayList<Integer>(parent.actionsSoFar);
        this.actionsSoFar.add(childIdx);
        children = new SingleTreeNode[BreadthFS.NUM_ACTIONS];
        totValue = 0.0;
        this.childIdx = childIdx;
        if(parent != null)
            m_depth = parent.m_depth+1;
        else
            m_depth = 0;
    }

    /**
     * returns an array with children-nodes
     * @return
     */
    public SingleTreeNode[] expand() {
        // Expand all children
    	if (m_depth % 10 == 0) {
    		state = state();
    		actionsSoFar = new ArrayList<Integer>();
    		actionsSoFar.add(-1);
    	}
    	for (int i = 0; i < BreadthFS.NUM_ACTIONS; i++) {
        	SingleTreeNode child = new SingleTreeNode(this, i, state, m_rnd);
        	children[i] = child;
        }
        return children;
    }

    /**
     * computes state from action-array
     * @return state that this node should be in (not saved for memory reasons)
     */
	public StateObservation state() {
		StateObservation s = state.copy();
    	int counter = 1;  // skip -1 from root
		while (counter < actionsSoFar.size()) {
			s.advance(BreadthFS.actions[actionsSoFar.get(counter)]);
			counter++;
		}
		return s;
	}
}
