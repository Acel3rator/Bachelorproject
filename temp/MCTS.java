package pRobot.mcts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ontology.Types;

public class MCTS {
	TreeNode root;

    public MCTS(TreeNode root) {
        this.root = root;
    }

    public void selectAction(Types.ACTIONS[] actions) {
        List<TreeNode> visited = new LinkedList<TreeNode>();
        TreeNode cur = root;
        visited.add(cur);
        while (!cur.isLeaf()) {  // going along tree until we come to a leaf
            cur = cur.select();
            // System.out.println("Adding: " + cur);
            visited.add(cur);
        }
        cur.expand(actions);
        TreeNode newNode = cur.select();
        visited.add(newNode);
        double value = rollOut(newNode, 5);
        for (TreeNode node : visited) {
            // would need extra logic for n-player game
            // System.out.println(node);
            node.updateStats(value);
        }
    }
    
    // Select random actions until either we win/lose or we are at maxRollOut
    public double rollOut(TreeNode tn, int depth) {
        while () {
        	ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();
            int index = randomGenerator.nextInt(actions.size());
            action = actions.get(index);
        }
    	return r.nextInt(2);
    }
}
