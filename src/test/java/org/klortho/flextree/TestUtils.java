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
        BoundingBox bb = new BoundingBox(t);
        ArrayList<Tree> nodes = t.allNodes();
        for (int i = 0 ; i < nodes.size(); i++) {
            for (int j = 0 ; j < i ; j++) {
                if (nodeOverlaps(bb, nodes.get(i), nodes.get(j))) {
                    ps.printf("Nodes %d and %d overlap!\n", i, j);
                    printNode(ps, nodes, i);
                    printNode(ps, nodes, j);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean nodeOverlaps(BoundingBox bb, Tree a, Tree b) {
        return overlap(bb.x_size(), a.x - a.x_size/2, a.x + a.x_size/2, 
                       b.x - b.x_size/2, b.x + b.x_size/2) &&
               overlap(bb.y_size(), a.y, a.y + a.y_size, b.y, b.y + b.y_size);
    }

    private static boolean overlap(double scale, double start0, double end0, 
                                   double start1, double end1) 
    {
        return (lt(scale, start1, end0) && lt(scale, start0, end1)) ||
               (lt(scale, start0, end1) && lt(scale, start1, end0));
    }
    
    private static boolean lt(double scale, double a, double b) {
        return (a < b && (b - a > scale * 0.00000000001));
    }

    private static void printNode(PrintStream ps, ArrayList<Tree> nodes, int i) {
        Tree node = nodes.get(i);
        ps.print(
            "Node " + i + ":\n" +
            "  x: " + node.x + "\n" +
            "  y: " + node.y + "\n" +
            "  x_size: " + node.x_size + "\n" +
            "  y_size: " + node.y_size + "\n"
        );
        
    }

}
