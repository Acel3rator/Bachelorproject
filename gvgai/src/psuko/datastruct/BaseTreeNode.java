package psuko.datastruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of the TreeNode Interface
 * @author Patrick
 *
 */
public abstract class BaseTreeNode implements TreeNode {

	private TreeNode parent;
	private final List<TreeNode> children = new ArrayList<>();
	
	private int depth;

	public BaseTreeNode(TreeNode parent) {
		this.parent = parent;
		
		if (parent != null)
		{
			this.depth = parent.getDepth() + 1;
		}
	}
	
	public BaseTreeNode() {
		this(null);
	}

	@Override
	public final TreeNode getParent() {
		return this.parent;
	}

	@Override
	public final void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	@Override
	public final List<TreeNode> getChildren() {
		return this.children;
	}

	@Override
	public final int getDepth() {
		return this.depth;
	}

	@Override
	public final void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public final void addChild(TreeNode child) {
		this.children.add(child);
		child.setDepth(this.depth + 1);
		child.setParent(this);
	}

	@Override
	public final boolean isLeaf() {
		return this.getChildCount() == 0;
	}

	@Override
	public final boolean isRoot() {
		return this.parent == null;
	}

	@Override
	public final int getChildCount() {
		return this.children.size();
	}

	@Override
	public void accept(TreeNodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return this.getStringRepresentation();
	}

}
