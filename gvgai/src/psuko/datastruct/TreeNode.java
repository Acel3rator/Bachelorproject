package psuko.datastruct;

import java.util.List;

/**
 * Interface providing basic operations to access tree-datastructures
 * @author Patrick
 *
 */
public interface TreeNode {

	TreeNode getParent();

	void setParent(TreeNode parent);

	int getDepth();

	void setDepth(int depth);
	
	List<TreeNode> getChildren();

	boolean isRoot();

	boolean isLeaf();

	int getChildCount();

	void addChild(TreeNode child);

	// accept a visitor to this treeNode (call visitor's visit method!)
	void accept(TreeNodeVisitor visitor);

	// force toString() for debug purposes
	String getStringRepresentation();
	
}
