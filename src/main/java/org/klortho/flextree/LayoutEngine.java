package org.klortho.flextree;

/**
 * The extended Reingold-Tilford algorithm as described in the paper
 * "Drawing Non-layered Tidy Trees in Linear Time" by Atze van der Ploeg
 * Accepted for publication in Software: Practice and Experience, to Appear.
 * 
 * This code is in the public domain, use it any way you wish. A reference to the paper is 
 * appreciated!
 * 
 * This LayoutEngine sets the x and y coordinates for every node in the tree such that:
 * - minimum x and minimum y are both 0.
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
	
	// This is a sentinal value for separation and spacing, meaning not set
	public static final TreeRelation NULL_TREE_RELATION = new TreeRelation() {
		public double s(Tree a, Tree b) { return 0.0; }
	};

	// Separation
	public static final TreeRelation defaultSeparation = new TreeRelation() {
		public double s(Tree a, Tree b) {
			return a.parent == b.parent ? 0 : 1;
		}
	};
	TreeRelation separation = defaultSeparation;
	
	// Spacing
	public static final TreeRelation defaultSpacing = NULL_TREE_RELATION;
	TreeRelation spacing = defaultSpacing;
	
	// Size
	public static final double[] NULL_SIZE = new double[] {0.0, 0.0};
	public static final double[] defaultSize = new double[] {1.0, 1.0};
	double[] size = defaultSize;

	// NodeSizeFixed
	public static final double[] defaultNodeSizeFixed = NULL_SIZE;
	double[] nodeSizeFixed = defaultNodeSizeFixed;

	// NodeSizeFunction - returns an array [x_size, y_size]
	public interface NodeSizeFunction {
		abstract double[] ns(Tree t);
	}
	public static final NodeSizeFunction NULL_NODE_SIZE_FUNCTION = 
		new NodeSizeFunction() {
		    public double[] ns(Tree t) { return new double[2]; }
        };
    public static final NodeSizeFunction defaultNodeSizeFunction = NULL_NODE_SIZE_FUNCTION;
    NodeSizeFunction nodeSizeFunction = defaultNodeSizeFunction;
    
    // This node size function is defined for convenience -- it gets the node size from
    // x_size and y_size attributes on the Tree node itself.
	public static final NodeSizeFunction nodeSizeFromTree = new NodeSizeFunction() {
		public double[] ns(Tree t) {
			return new double[] {t.x_size, t.y_size};
		}
	};

    

	
	public static class Builder {
		public LayoutEngine build() {
			return new LayoutEngine(this);
		}
		public Builder setSeparation(TreeRelation s) {
			separation = s;
			spacing = NULL_TREE_RELATION;
			return this;
		}
		public Builder setSpacing(TreeRelation s) {
			spacing = s;
			separation = NULL_TREE_RELATION;
			return this;
		}
		public Builder setSize(double[] s) {
			size = s;
			nodeSizeFixed = NULL_SIZE;
			nodeSizeFunction = NULL_NODE_SIZE_FUNCTION;
			return this;
		}
		public Builder setNodeSizeFixed(double[] nsf) {
			nodeSizeFixed = nsf;
			size = NULL_SIZE;
			nodeSizeFunction = NULL_NODE_SIZE_FUNCTION;
			return this;
		}
		public Builder setNodeSizeFunction(NodeSizeFunction nsf) {
			nodeSizeFunction = nsf;
			size = NULL_SIZE;
			nodeSizeFixed = NULL_SIZE;
			return this;
		}
		
		private TreeRelation separation = defaultSeparation;
		private TreeRelation spacing = defaultSpacing;
		private double[] size = defaultSize;
		private double[] nodeSizeFixed = defaultNodeSizeFixed;
		private NodeSizeFunction nodeSizeFunction = defaultNodeSizeFunction;
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
	}
	
	/**
	 * Does the layout, setting the following attributes on each Tree node:
	 * parent - the parent node, or null for the root.
	 * depth - the depth of the node, starting at 0 for the root.
	 * x - the computed x-coordinate of the node position.
	 * y - the computed y-coordinate of the node position.
	 */
	public void layout(Tree t) { 
		WrappedTree wt = new WrappedTree(t);
        zerothWalk(wt, 0);
		firstWalk(wt); 
		secondWalk(wt, 0);
		normalizeX(wt);
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
			if (size != NULL_SIZE) {
				this.x_size = 1;
				this.y_size = 1;
			}
			else if (nodeSizeFixed != NULL_SIZE) {
				this.x_size = nodeSizeFixed[0];
				this.y_size = nodeSizeFixed[1];
			}
			else {  // use nodeSizeFunction
    			double[] nodeSize = nodeSizeFunction.ns(t);
    			this.x_size = nodeSize[0];
    			this.y_size = nodeSize[1];
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

	void firstWalk(WrappedTree t) {
        if (t.num_children == 0) { 
		    setExtremes(t); 
		    return; 
        }
        firstWalk(t.children[0]);
        
        // Create siblings in contour minimal vertical coordinate and index list.
	    IYL ih =  updateIYL(bottom(t.children[0].el), 0, null);
	    
	    for (int i = 1; i < t.num_children; i++) {
	        firstWalk(t.children[i]);
	        
	        // Store lowest vertical coordinate while extreme nodes still point in 
	        // current subtree.
	        double minY = bottom(t.children[i].er);                                
	        seperate(t, i, ih);
	        ih = updateIYL(minY, i, ih);                                     
	    }
        positionRoot(t);
        setExtremes(t);
	}
		  
	void setExtremes(WrappedTree t) {
	    if (t.num_children == 0) {
	        t.el = t; 
	        t.er = t;
	        t.msel = t.mser = 0;
	    }
	    else {
	        t.el = t.children[0].el; 
	        t.msel = t.children[0].msel;
	        t.er = t.children[t.num_children - 1].er; 
	        t.mser = t.children[t.num_children - 1].mser;
	    }
	}
	  
	void seperate(WrappedTree t, int i, IYL ih) {
	    // Right contour node of left siblings and its sum of modfiers.  
	    WrappedTree sr = t.children[i-1]; 
	    double mssr = sr.mod;
	   
	    // Left contour node of current subtree and its sum of modfiers.  
	    WrappedTree cl = t.children[i]; 
	    double mscl = cl.mod;
	   
	    while (sr != null && cl != null) {
		    if (bottom(sr) > ih.lowY) ih = ih.nxt;
		  
		    // How far to the left of the right side of sr is the left side of cl?  
		    double dist = (mssr + sr.prelim + sr.x_size()) - (mscl + cl.prelim);
		    if (dist > 0) {
		        mscl += dist;
		        moveSubtree(t, i, ih.index, dist);
		    }
		    double sy = bottom(sr), 
			       cy = bottom(cl);
		  
		    // Advance highest node(s) and sum(s) of modifiers  
		    if (sy <= cy) {                                                    
		        sr = nextRightContour(sr);
		        if (sr!=null) mssr += sr.mod;
		    }                                                               
		    if (sy >= cy) {                                           
		        cl = nextLeftContour(cl);
		        if (cl != null) mscl += cl.mod;
		    }                                                              
	    }

        // Set threads and update extreme nodes.  
	    // In the first case, the current subtree must be taller than the left siblings.  
	    if (sr == null && cl != null) setLeftThread(t, i, cl, mscl);
	    
	    // In this case, the left siblings must be taller than the current subtree.  
	    else if (sr != null && cl == null) setRightThread(t, i, sr, mssr);
	}

	void moveSubtree(WrappedTree t, int i, int si, double dist) {
	    // Move subtree by changing mod.  
	    t.children[i].mod += dist; 
	    t.children[i].msel += dist; 
	    t.children[i].mser += dist;
	    distributeExtra(t, i, si, dist);                                  
	}
	  
	WrappedTree nextLeftContour(WrappedTree t) {
		return t.num_children == 0 ? t.tl : t.children[0];
	}
	
	WrappedTree nextRightContour(WrappedTree t) {
		return t.num_children == 0 ? t.tr : t.children[t.num_children - 1];
	}
	
	double bottom(WrappedTree t) { 
		return t.y() + t.y_size(); 
	}
	  
	void setLeftThread(WrappedTree t, int i, WrappedTree cl, double modsumcl) {
	    WrappedTree li = t.children[0].el;
	    li.tl = cl;
	   
	    // Change mod so that the sum of modifier after following thread is correct.  
	    double diff = (modsumcl - cl.mod) - t.children[0].msel;
	    li.mod += diff; 
	   
	    // Change preliminary x coordinate so that the node does not move.  
	    li.prelim -= diff;
	   
	    // Update extreme node and its sum of modifiers.  
	    t.children[0].el = t.children[i].el; 
	    t.children[0].msel = t.children[i].msel;
	}
	  
	// Symmetrical to setLeftThread.  
	void setRightThread(WrappedTree t, int i, WrappedTree sr, double modsumsr) {
	    WrappedTree ri = t.children[i].er;
	    ri.tr = sr;
	    double diff = (modsumsr - sr.mod) - t.children[i].mser;
	    ri.mod += diff; 
	    ri.prelim -= diff;
	    t.children[i].er = t.children[i - 1].er; 
	    t.children[i].mser = t.children[i - 1].mser;
	}

	void positionRoot(WrappedTree wt) {
	    // Position root between children, taking into account their mod.  
	    wt.prelim = ( wt.children[0].prelim + 
	 		          wt.children[0].mod + 
 			          wt.children[wt.num_children - 1].mod + 
                      wt.children[wt.num_children - 1].prelim + 
                      wt.children[wt.num_children - 1].x_size() ) / 2 
			        - wt.x_size() / 2;
	}

	void secondWalk(WrappedTree t, double modsum) {
	    modsum += t.mod;
	    // Set absolute (non-relative) horizontal coordinate.  
	    t.x(t.prelim + modsum);
	    addChildSpacing(t);                                               
	    for (int i = 0; i < t.num_children; i++) secondWalk(t.children[i], modsum);
	}

	void distributeExtra(WrappedTree t, int i, int si, double dist) {           
	    // Are there intermediate children?
	    if (si != i - 1) {                                                    
	        double nr = i - si;                                            
	        t.children[si + 1].shift += dist / nr;                                     
	        t.children[i].shift -= dist / nr;                                         
	        t.children[i].change -= dist - dist / nr;                                 
	    }                                                                 
	}                                                                    
	 
	// Process change and shift to add intermediate spacing to mod.  
	void addChildSpacing(WrappedTree t) {
	    double d = 0, modsumdelta = 0;                                    
	    for (int i = 0; i < t.num_children; i++) {                                  
	        d += t.children[i].shift;                                               
	        modsumdelta += d + t.children[i].change;                                
	        t.children[i].mod += modsumdelta;                                       
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

	double getMinX(WrappedTree wt) {
		double minX = wt.x();
		for (WrappedTree child : wt.children) {
			minX = Math.min(getMinX(child), minX);
		}
		return minX;
	}
	
	public void moveRight(WrappedTree wt, double move) {
		wt.x(wt.x() + move);
		for (WrappedTree child : wt.children) {
			moveRight(child, move);
		}
	}
}
