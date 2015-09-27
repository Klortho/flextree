package org.klortho.flextree;

import java.util.Stack;

/**
 * To use this class, you have to make sure you set the setNodeSizes attribute to true
 * on the layout engine, so that it sets the x_size and y_size attribute on every
 * tree node.  It doesn't do that by default.
 */
public class BoundingBox {
	double min_x, min_y, max_x, max_y;

	public BoundingBox(Tree t) {
		min_x = max_x = t.x;
		min_y = max_y = t.y + t.y_size;
		Stack<Tree> toVisit = new Stack<Tree>();
		toVisit.push(t);
		while (toVisit.size() > 0) {
			Tree n = toVisit.pop();

			min_x = Math.min(min_x, n.x - n.x_size / 2);
			max_x = Math.max(max_x, n.x + n.x_size / 2);
			min_y = Math.min(min_y, n.y);
			max_y = Math.max(max_y, n.y + n.y_size);
			toVisit.addAll(n.children);
		}
	}

	public double x_size() {
		return max_x - min_x;
	}
	public double y_size() {
		return max_y - min_y;
	}
}