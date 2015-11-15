package psuko.datastruct.debug;

import java.util.ArrayList;
import java.util.List;

import psuko.datastruct.TreeNode;
import psuko.datastruct.TreeNodeVisitor;

public class TreeStatisticPrinter implements TreeNodeVisitor {

	private List<TreeNode> gatherAllSubNodes(TreeNode treeNode) {
		List<TreeNode> allNodes = new ArrayList<>();
		allNodes.add(treeNode);

		if (!treeNode.isLeaf()) {
			for (TreeNode child : treeNode.getChildren()) {
				allNodes.addAll(this.gatherAllSubNodes(child));
			}
		}

		return allNodes;
	}

	@Override
	public void visit(TreeNode treeNode) {

		int numNodes = 0;
		int numLeafs = 0;
		int maxDepth = 0;

		for (TreeNode node : this.gatherAllSubNodes(treeNode)) {
			numNodes++;
			if (node.isLeaf()) {
				numLeafs++;
			}
			if (node.getDepth() > maxDepth) {
				maxDepth = node.getDepth();
			}
		}

		System.out.println("Tree Statistics.." + " #nodes: " + numNodes
				+ ", #leafs: " + numLeafs + ", maxDepth: " + maxDepth
				+ ", numChildren: " + treeNode.getChildCount()
				+ ", nodeDepth: " + treeNode.getDepth());
	}

}
