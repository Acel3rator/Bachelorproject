package psuko.datastruct.debug;

import psuko.datastruct.TreeNode;
import psuko.datastruct.TreeNodeVisitor;


public class TreeStructurePrinter implements TreeNodeVisitor {

	private final StructureType printType;

	public enum StructureType {
		LeftToRightLeveled, NestedArrays;
	}

	public TreeStructurePrinter(StructureType printType) {
		this.printType = printType;
	}

	@Override
	public void visit(TreeNode treeNode) {
		switch (printType) {
		case LeftToRightLeveled:
			print(treeNode);
			break;
		case NestedArrays:
			System.out.println(this.toStringVerbose(treeNode));
			break;
		}
	}

	public void print(TreeNode treeNode) {
		print("", true, treeNode);
	}

	public void print(String prefix, boolean isTail, TreeNode treeNode) {
		System.out.println(prefix + (isTail ? "'- " : "|- ")
				+ treeNode.toString());
		for (int i = 0; i < treeNode.getChildCount() - 1; i++) {
			this.print(prefix + (isTail ? "   " : "|  "), false, treeNode
					.getChildren().get(i));
		}
		if (treeNode.getChildCount() > 0) {
			this.print(prefix + (isTail ? "   " : "|  "), true, treeNode
					.getChildren().get(treeNode.getChildCount() - 1));
		}
	}

	private final String toStringVerbose(TreeNode treeNode) {
		String s = "";

		s += "[" + treeNode.toString() + "]";
		if (!treeNode.isLeaf())
			s += ":<";
		for (int i = 0; i < treeNode.getChildCount(); i++) {
			s += toStringVerbose(treeNode.getChildren().get(i));
			if (i < treeNode.getChildCount() - 1) {
				s += ", ";
			}
		}
		if (!treeNode.isLeaf())
			s += ">";
		return s;
	}

}
