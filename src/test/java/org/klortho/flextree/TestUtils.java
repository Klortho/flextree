package org.klortho.flextree;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class TestUtils {

	/**
	 * Test to see if the tree has any overlaps among its nodes. Returns true if there
	 * is any overlap. This requires that, when the tree was layed out, you set the
	 * setNodeSizes attribute to true, so that x_size and y_size are accurate.
	 */
	public static boolean hasOverlappingNodes(Tree t) {
		// dummy PrintStream -- output will not be used.
		PrintStream ps = new PrintStream(new ByteArrayOutputStream());
		return hasOverlappingNodes(t, ps);
	}
	
	/**
	 * Same as hasOverlappingNodes(t), but if an overlapping node is encountered, a message
	 * about it is written to the PrintStream.
	 */
	public static boolean hasOverlappingNodes(Tree t, PrintStream ps) {
		ArrayList<Tree> nodes = t.allNodes();
		for (int i = 0 ; i < nodes.size(); i++) {
			for (int j = 0 ; j < i ; j++) {
				if (nodeOverlaps(nodes.get(i), nodes.get(j))) {
					ps.printf("Nodes %d and %d overlap!\n", i, j);
					return true;
				}
			}
		}
		return false;
	}

	private static boolean nodeOverlaps(Tree a, Tree b) {
        return overlap(a.x - a.x_size/2, a.x + a.x_size/2, 
        		       b.x - b.x_size/2, b.x + b.x_size/2) &&
               overlap(a.y, a.y + a.y_size, b.y, b.y + b.y_size);
	}

	private static boolean overlap(double start0, double end0, 
            double start1, double end1) 
	{
		return (start1 < end0 && end1 > start0) ||
		(start0 < end1 && end0 > start1);
	}



}
