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
 */

public class LayoutEngine {
	// Set default separation
	static Separation separation = new Separation() {
		public double s(Tree a, Tree b) {
			//return a.parent == b.parent ? 0 : 1;
			return 1;
		}
	};
	
	public static void layout(Tree t) { 
		WrappedTree wt = new WrappedTree(t);
        zerothWalk(wt, 0);
		firstWalk(wt); 
		secondWalk(wt, 0);
		normalizeX(wt);
	}

	// Interface to use for the separation
	public interface Separation {
		abstract double s(Tree a, Tree b);
	}

	public static void layout(Tree t, Separation s) {
		separation = s;
		layout(t);
	}

	private static class WrappedTree {
	    Tree t;
	    WrappedTree[] children; 
	    int num_children;     // Array of children and number of children. 

	    double prelim, mod, shift, change;
	    WrappedTree tl, tr;          // Left and right thread.                        
	    WrappedTree el, er;          // Extreme left and right nodes. 
	    double msel, mser;    // Sum of modifiers at the extreme nodes. 
	   
		public WrappedTree(Tree t) {
			this.t = t;

		    children = new WrappedTree[t.children.size()];
		    num_children = children.length;
		    for (int i = 0 ; i < children.length ; i++) {
                children[i] = new WrappedTree(t.children.get(i));
	        }
		}	    
	    
        public double width() {
            return t.width;
        }
        public double height() {
            return t.height;
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
	static void zerothWalk(WrappedTree wt, double initial) {
		wt.y(initial);
		wt.depth(0);
		zerothWalk(wt);
	}
	
    static void zerothWalk(WrappedTree wt) {
        double kid_y = wt.y() + wt.height();
        int kid_depth = wt.depth() + 1;
        for (int i = 0; i < wt.num_children; ++i) {
        	WrappedTree kid = wt.children[i];
            kid.y(kid_y);
            kid.parent(wt.t);
            kid.depth(kid_depth);
            zerothWalk(wt.children[i]);
        }
    }

	static void firstWalk(WrappedTree t) {
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
		  
	static void setExtremes(WrappedTree t) {
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
	  
	static void seperate(WrappedTree t, int i, IYL ih) {
	    // Right contour node of left siblings and its sum of modfiers.  
	    WrappedTree sr = t.children[i-1]; 
	    double mssr = sr.mod;
	   
	    // Left contour node of current subtree and its sum of modfiers.  
	    WrappedTree cl = t.children[i]; 
	    double mscl = cl.mod;
	   
	    while (sr != null && cl != null) {
		    if (bottom(sr) > ih.lowY) ih = ih.nxt;
		  
		    // How far to the left of the right side of sr is the left side of cl?  
		    double dist = (mssr + sr.prelim + sr.width()) - (mscl + cl.prelim);
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

	static void moveSubtree(WrappedTree t, int i, int si, double dist) {
	    // Move subtree by changing mod.  
	    t.children[i].mod += dist; 
	    t.children[i].msel += dist; 
	    t.children[i].mser += dist;
	    distributeExtra(t, i, si, dist);                                  
	}
	  
	static WrappedTree nextLeftContour(WrappedTree t) {
		return t.num_children == 0 ? t.tl : t.children[0];
	}
	
	static WrappedTree nextRightContour(WrappedTree t) {
		return t.num_children == 0 ? t.tr : t.children[t.num_children - 1];
	}
	
	static double bottom(WrappedTree t) { 
		return t.y() + t.height(); 
	}
	  
	static void setLeftThread(WrappedTree t, int i, WrappedTree cl, double modsumcl) {
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
	static void setRightThread(WrappedTree t, int i, WrappedTree sr, double modsumsr) {
	    WrappedTree ri = t.children[i].er;
	    ri.tr = sr;
	    double diff = (modsumsr - sr.mod) - t.children[i].mser;
	    ri.mod += diff; 
	    ri.prelim -= diff;
	    t.children[i].er = t.children[i - 1].er; 
	    t.children[i].mser = t.children[i - 1].mser;
	}

	static void positionRoot(WrappedTree wt) {
	    // Position root between children, taking into account their mod.  
	    wt.prelim = ( wt.children[0].prelim + 
	 		          wt.children[0].mod + 
 			          wt.children[wt.num_children - 1].mod + 
                      wt.children[wt.num_children - 1].prelim + 
                      wt.children[wt.num_children - 1].width() ) / 2 
			        - wt.width() / 2;
	}

	static void secondWalk(WrappedTree t, double modsum) {
	    modsum += t.mod;
	    // Set absolute (non-relative) horizontal coordinate.  
	    t.x(t.prelim + modsum);
	    addChildSpacing(t);                                               
	    for (int i = 0; i < t.num_children; i++) secondWalk(t.children[i], modsum);
	}

	static void distributeExtra(WrappedTree t, int i, int si, double dist) {           
	    // Are there intermediate children?
	    if (si != i - 1) {                                                    
	        double nr = i - si;                                            
	        t.children[si + 1].shift += dist / nr;                                     
	        t.children[i].shift -= dist / nr;                                         
	        t.children[i].change -= dist - dist / nr;                                 
	    }                                                                 
	}                                                                    
	 
	// Process change and shift to add intermediate spacing to mod.  
	static void addChildSpacing(WrappedTree t) {
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
	  
	static IYL updateIYL(double minY, int i, IYL ih) {                         
	    // Remove siblings that are hidden by the new subtree.  
	    while (ih != null && minY >= ih.lowY) ih = ih.nxt;                 
	    // Prepend the new subtree.  
	    return new IYL(minY, i, ih);                                       
	}         

	/**
	 * Normalize the x-coordinate, so that the minimum x is 0.
	 */
	static void normalizeX(WrappedTree wt) {
		double minX = getMinX(wt);
		moveRight(wt, -minX);
	}

	static double getMinX(WrappedTree wt) {
		double minX = wt.x();
		for (WrappedTree child : wt.children) {
			minX = Math.min(getMinX(child), minX);
		}
		return minX;
	}
	
	static public void moveRight(WrappedTree wt, double move) {
		wt.x(wt.x() + move);
		for (WrappedTree child : wt.children) {
			moveRight(child, move);
		}
	}
}
