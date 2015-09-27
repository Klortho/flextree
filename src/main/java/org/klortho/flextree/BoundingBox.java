package org.klortho.flextree;

import static java.lang.Math.max;

public class BoundingBox {
	double min_x, min_y, max_x, max_y;

    // FIXME: what to do about node size?
	public BoundingBox(Tree t) {
		min_x = max_x = t.x;
		min_y = max_y = t.y;
		for (Tree kid : t.children) {
			min_x = Math.min(min_x, kid.x - kid.x_size / 2);
			max_x = Math.max(max_x, kid.x + kid.x_size / 2);
			min_y = Math.min(min_y, kid.y - kid.y_size / 2);
			max_y = Math.max(max_y, kid.y + kid.y_size / 2);
		}
	}

	public double x_size() {
		return max_x - min_x;
	}
	public double y_size() {
		return max_y - min_y;
	}
}