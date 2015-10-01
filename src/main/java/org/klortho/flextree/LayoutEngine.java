package org.klortho.flextree;

import java.util.Arrays;
import java.util.Stack;

/**
 * The extended Reingold-Tilford algorithm as described in the paper
 * "Drawing Non-layered Tidy Trees in Linear Time" by Atze van der Ploeg
 * Accepted for publication in Software: Practice and Experience, to Appear.
 * 
 * This code is in the public domain, use it any way you wish. A reference to the paper is 
 * appreciated!
 * 
 * Instantiate one of these with either:
 * - LayoutEngine engine = new LayoutEngine();   // to use all defaults
 * - LayoutEngine engine = LayoutEngine.builder()
 *       .setSeparation(s)
 *       ...
 *       .build(); 
 */

public class LayoutEngine {

    // This interface defines a function that takes two trees, and returns a
    // double.  It's used for separation() and spacing().
    public interface TreeRelation {
        abstract double s(Tree a, Tree b);
    }
    
    // Separation
    public static final TreeRelation defaultSeparation = new TreeRelation() {
        public double s(Tree a, Tree b) {
            return a.parent == b.parent ? 1 : 2;
        }
    };
    TreeRelation separation = defaultSeparation;
    
    // Spacing
    TreeRelation spacing = null;
    
    // This spacing function is defined for convenience - always returns 0.
    public static final TreeRelation spacing0 = new TreeRelation() {
        public double s(Tree a, Tree b) {
            return 0;
        }
    };
    
    // Size
    public static final double[] defaultSize = new double[] {1.0, 1.0};
    double[] size = defaultSize;

    // NodeSizeFixed
    double[] nodeSizeFixed = null;

    // NodeSizeFunction - returns an array [x_size, y_size]
    public interface NodeSizeFunction {
        abstract double[] ns(Tree t);
    }
    NodeSizeFunction nodeSizeFunction = null;
    
    // This node size function is defined for convenience -- it gets the node size from
    // x_size and y_size attributes on the Tree node itself.
    public static final NodeSizeFunction nodeSizeFromTree = new NodeSizeFunction() {
        public double[] ns(Tree t) {
            return new double[] {t.x_size, t.y_size};
        }
    };
    
    // If this is set to true, then the layout engine will set the x_size and y_size
    // attributes on each tree node.
    boolean setNodeSizes = false;
    
    // This stores the x_size of the root node, for use with the spacing function
    double rootXSize;

    

    
    public static class Builder {
        public LayoutEngine build() {
            return new LayoutEngine(this);
        }
        public Builder setSeparation(TreeRelation s) {
            separation = s;
            spacing = null;
            return this;
        }
        public Builder setSpacing(TreeRelation s) {
            spacing = s;
            separation = null;
            return this;
        }
        public Builder setSize(double[] s) {
            size = s;
            nodeSizeFixed = null;
            nodeSizeFunction = null;
            return this;
        }
        public Builder setNodeSizeFixed(double[] nsf) {
            nodeSizeFixed = nsf;
            size = null;
            nodeSizeFunction = null;
            return this;
        }
        public Builder setNodeSizeFunction(NodeSizeFunction nsf) {
            nodeSizeFunction = nsf;
            size = null;
            nodeSizeFixed = null;
            return this;
        }
        public Builder setSetNodeSizes(boolean sns) {
            setNodeSizes = sns;
            return this;
        }
        
        private TreeRelation separation = defaultSeparation;
        private TreeRelation spacing = null;
        private double[] size = defaultSize;
        private double[] nodeSizeFixed = null;
        private NodeSizeFunction nodeSizeFunction = null;
        private boolean setNodeSizes = false;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    
    /**
     * Default constructor - when all the defaults are okay.
     */
    public LayoutEngine() {
    }
    
    /**
     * Construct a Layout engine from a Builder
     */
    public LayoutEngine(Builder b) {
        separation = b.separation;
        spacing = b.spacing;
        size = b.size;
        nodeSizeFixed = b.nodeSizeFixed;
        nodeSizeFunction = b.nodeSizeFunction;
        setNodeSizes = b.setNodeSizes;
    }
    
    /**
     * Does the layout, setting the following attributes on each Tree node:
     *   - parent - the parent node, or null for the root.
     *   - depth - the depth of the node, starting at 0 for the root.
     *   - x - the computed x-coordinate of the node position.
     *   - y - the computed y-coordinate of the node position.
     * Additionally, if setNodeSizes is true, this will set
     *   - x_size
     *   - y_size
     */
    public void layout(Tree t) { 
        WrappedTree wt = new WrappedTree(t);
        rootXSize = wt.x_size;
        zerothWalk(wt, 0);
        firstWalk(wt); 
        secondWalk(wt, 0);

        // If a fixed tree size is specified, scale x and y based on the extent.
        // Compute the left-most, right-most, and depth-most nodes for extents.
        if (size != null) {
            WrappedTree left = wt,
                        right = wt,
                        bottom = wt;
            Stack<WrappedTree> toVisit = new Stack<WrappedTree>();
            toVisit.push(wt);
            while (toVisit.size() > 0) {
                WrappedTree node = toVisit.pop();
                if (node.x() < left.x()) left = node;
                if (node.x() > right.x()) right = node;
                if (node.depth() > bottom.depth()) bottom = node;
                toVisit.addAll(Arrays.asList(node.children));
            }

            double sep = separation == null ? 0.5 : separation.s(left.t, right.t)/2;
            double tx = sep - left.x();
            double kx = size[0] / (right.x() + sep + tx);
            double ky = size[1] / (bottom.depth() > 0 ? bottom.depth() : 1);
            
            toVisit.push(wt);
            while (toVisit.size() > 0) {
                WrappedTree node = toVisit.pop();
                node.x((node.x() + tx) * kx);
                node.y(node.depth() * ky);
                if (setNodeSizes) {
                    node.t.x_size *= kx;
                    node.t.y_size *= ky;
                }
                toVisit.addAll(Arrays.asList(node.children));
            }
        }
        else {
            normalizeX(wt);
        }
    }


    private class WrappedTree {
        Tree t;
        double x_size, y_size;
        WrappedTree[] children; 
        int num_children;     // Array of children and number of children.

        double prelim, mod, shift, change;
        WrappedTree tl, tr;          // Left and right thread.                        
        WrappedTree el, er;          // Extreme left and right nodes. 
        double msel, mser;    // Sum of modifiers at the extreme nodes. 
       
        public WrappedTree(Tree t) {
            this.t = t;
            
            // Set the size attributes of this node, based on whatever method was selected
            // by the user.
            if (size != null) {
                x_size = 1;
                y_size = 1;
            }
            else if (nodeSizeFixed != null) {
                x_size = nodeSizeFixed[0];
                y_size = nodeSizeFixed[1];
            }
            else {  // use nodeSizeFunction
                double[] nodeSize = nodeSizeFunction.ns(t);
                x_size = nodeSize[0];
                y_size = nodeSize[1];
            }
            
            if (setNodeSizes) {
                t.x_size = x_size;
                t.y_size = y_size;
            }

            children = new WrappedTree[t.children.size()];
            num_children = children.length;
            for (int i = 0 ; i < children.length ; i++) {
                children[i] = new WrappedTree(t.children.get(i));
            }
        }       
        
        public double x_size() {
            return x_size;
        }
        public double y_size() {
            return y_size;
        }
        public void parent(Tree p) {
            t.parent = p;
        }
        public int depth() {
            return t.depth;
        }
        public void depth(int d) {
            t.depth = d;
        }
        public double x() {
            return t.x;
        }
        public void x(double _x) {
            t.x = _x;
        }
        public double y() {
            return t.y;
        }
        public void y(double _y) {
            t.y = _y;
        }
    }

    // Recursively set the y coordinate of the children, based on
    // the y coordinate of the parent, and its height. Also set parent and
    // depth.
    void zerothWalk(WrappedTree wt, double initial) {
        wt.y(initial);
        wt.depth(0);
        zerothWalk(wt);
    }
    
    void zerothWalk(WrappedTree wt) {
        double kid_y = wt.y() + wt.y_size();
        int kid_depth = wt.depth() + 1;
        for (int i = 0; i < wt.num_children; ++i) {
            WrappedTree kid = wt.children[i];
            kid.y(kid_y);
            kid.parent(wt.t);
            kid.depth(kid_depth);
            zerothWalk(wt.children[i]);
        }
    }

    void firstWalk(WrappedTree wt) {
        if (wt.num_children == 0) { 
            setExtremes(wt); 
            return; 
        }
        firstWalk(wt.children[0]);
        
        // Create siblings in contour minimal vertical coordinate and index list.
        IYL ih =  updateIYL(bottom(wt.children[0].el), 0, null);
        
        for (int i = 1; i < wt.num_children; i++) {
            firstWalk(wt.children[i]);
            
            // Store lowest vertical coordinate while extreme nodes still point in 
            // current subtree.
            double minY = bottom(wt.children[i].er);                                
            separate(wt, i, ih);
            ih = updateIYL(minY, i, ih);                                     
        }
        positionRoot(wt);
        setExtremes(wt);
    }
          
    void setExtremes(WrappedTree wt) {
        if (wt.num_children == 0) {
            wt.el = wt; 
            wt.er = wt;
            wt.msel = wt.mser = 0;
        }
        else {
            wt.el = wt.children[0].el; 
            wt.msel = wt.children[0].msel;
            wt.er = wt.children[wt.num_children - 1].er; 
            wt.mser = wt.children[wt.num_children - 1].mser;
        }
    }
      
    void separate(WrappedTree wt, int i, IYL ih) {
        // Right contour node of left siblings and its sum of modifiers.  
        WrappedTree sr = wt.children[i-1]; 
        double mssr = sr.mod;
       
        // Left contour node of current subtree and its sum of modifiers.  
        WrappedTree cl = wt.children[i]; 
        double mscl = cl.mod;
       
        while (sr != null && cl != null) {
            if (bottom(sr) > ih.lowY) ih = ih.nxt;
          
            // How far to the left of the right side of sr is the left side of cl?
            // First compute the center-to-center distance, then add the something
            // depending on separation/spacing
            double dist = (mssr + sr.prelim) - (mscl + cl.prelim);
            if (separation != null) {
                dist += separation.s(sr.t, cl.t) * rootXSize;
            }
            else if (spacing != null) {
                dist += sr.x_size()/2 + cl.x_size()/2 + spacing.s(sr.t, cl.t);
            }
            if (dist > 0) {
                mscl += dist;
                moveSubtree(wt, i, ih.index, dist);
            }
            double sy = bottom(sr), 
                   cy = bottom(cl);
          
            // Advance highest node(s) and sum(s) of modifiers  
            if (sy <= cy) {                                                    
                sr = nextRightContour(sr);
                if (sr != null) mssr += sr.mod;
            }                                                               
            if (sy >= cy) {                                           
                cl = nextLeftContour(cl);
                if (cl != null) mscl += cl.mod;
            }                                                              
        }

        // Set threads and update extreme nodes.  
        // In the first case, the current subtree must be taller than the left siblings.  
        if (sr == null && cl != null) setLeftThread(wt, i, cl, mscl);
        
        // In this case, the left siblings must be taller than the current subtree.  
        else if (sr != null && cl == null) setRightThread(wt, i, sr, mssr);
    }

    void moveSubtree(WrappedTree wt, int i, int si, double dist) {
        // Move subtree by changing mod.  
        wt.children[i].mod += dist; 
        wt.children[i].msel += dist; 
        wt.children[i].mser += dist;
        distributeExtra(wt, i, si, dist);                                  
    }
      
    WrappedTree nextLeftContour(WrappedTree wt) {
        return wt.num_children == 0 ? wt.tl : wt.children[0];
    }
    
    WrappedTree nextRightContour(WrappedTree wt) {
        return wt.num_children == 0 ? wt.tr : wt.children[wt.num_children - 1];
    }
    
    double bottom(WrappedTree wt) { 
        return wt.y() + wt.y_size(); 
    }
      
    void setLeftThread(WrappedTree wt, int i, WrappedTree cl, double modsumcl) {
        WrappedTree li = wt.children[0].el;
        li.tl = cl;
       
        // Change mod so that the sum of modifier after following thread is correct.  
        double diff = (modsumcl - cl.mod) - wt.children[0].msel;
        li.mod += diff; 
       
        // Change preliminary x coordinate so that the node does not move.  
        li.prelim -= diff;
       
        // Update extreme node and its sum of modifiers.  
        wt.children[0].el = wt.children[i].el; 
        wt.children[0].msel = wt.children[i].msel;
    }
      
    // Symmetrical to setLeftThread.  
    void setRightThread(WrappedTree wt, int i, WrappedTree sr, double modsumsr) {
        WrappedTree ri = wt.children[i].er;
        ri.tr = sr;
        double diff = (modsumsr - sr.mod) - wt.children[i].mser;
        ri.mod += diff; 
        ri.prelim -= diff;
        wt.children[i].er = wt.children[i - 1].er; 
        wt.children[i].mser = wt.children[i - 1].mser;
    }

    void positionRoot(WrappedTree wt) {
        // Position root between children, taking into account their mod.  
        wt.prelim = ( wt.children[0].prelim + 
                      wt.children[0].mod -
                      wt.children[0].x_size()/2 +
                      wt.children[wt.num_children - 1].mod + 
                      wt.children[wt.num_children - 1].prelim +
                      wt.children[wt.num_children - 1].x_size()/2) / 2;
    }

    void secondWalk(WrappedTree wt, double modsum) {
        modsum += wt.mod;
        // Set absolute (non-relative) horizontal coordinate.  
        wt.x(wt.prelim + modsum);
        addChildSpacing(wt);                                               
        for (int i = 0; i < wt.num_children; i++) 
            secondWalk(wt.children[i], modsum);
    }

    void distributeExtra(WrappedTree wt, int i, int si, double dist) {           
        // Are there intermediate children?
        if (si != i - 1) {                                                    
            double nr = i - si;                                            
            wt.children[si + 1].shift += dist / nr;                                     
            wt.children[i].shift -= dist / nr;                                         
            wt.children[i].change -= dist - dist / nr;                                 
        }                                                                 
    }                                                                    
     
    // Process change and shift to add intermediate spacing to mod.  
    void addChildSpacing(WrappedTree wt) {
        double d = 0, modsumdelta = 0;                                    
        for (int i = 0; i < wt.num_children; i++) {                                  
            d += wt.children[i].shift;                                               
            modsumdelta += d + wt.children[i].change;                                
            wt.children[i].mod += modsumdelta;                                       
        }                                                                 
    }                                                                    

    // A linked list of the indexes of left siblings and their lowest vertical coordinate.  
    static class IYL {                                                          
        double lowY; 
        int index; 
        IYL nxt;                                 
        public IYL(double lowY, int index, IYL nxt) {                         
            this.lowY = lowY; 
            this.index = index; 
            this.nxt = nxt;            
        }                                                                     
    }                                                                       
      
    IYL updateIYL(double minY, int i, IYL ih) {                         
        // Remove siblings that are hidden by the new subtree.  
        while (ih != null && minY >= ih.lowY) ih = ih.nxt;                 
        // Prepend the new subtree.  
        return new IYL(minY, i, ih);                                       
    }         

    /**
     * Normalize the x-coordinate, so that the root node is at x == 0.
     */
    void normalizeX(WrappedTree wt) {
        double rootX = wt.x();
        moveRight(wt, -rootX);
    }

    public void moveRight(WrappedTree wt, double move) {
        wt.x(wt.x() + move);
        for (WrappedTree child : wt.children) {
            moveRight(child, move);
        }
    }
}
