package org.klortho.flextree;

/* The extended Reingold-Tilford algorithm as described in the paper
 * "Drawing Non-layered Tidy Trees in Linear Time" by Atze van der Ploeg
 * Accepted for publication in Software: Practice and Experience, to Appear.
 * 
 * This code is in the public domain, use it any way you wish. A reference to the paper is 
 * appreciated!
 */

public class LayoutEngine {
    public static class WrappedTree {
        Tree t;
        double prelim, mod, shift, change;
        WrappedTree tl, tr;  // Left and right thread.                 
        WrappedTree el, er;  // Extreme left and right nodes.
        double msel, mser;   // Sum of modifiers at the extreme nodes.
        WrappedTree[] c;     // Array of children
        int cs;              // Number of children.
       
        WrappedTree(Tree t, WrappedTree... c) {
            this.t = t;
            this.c = c; 
            this.cs = c.length;
        }

        public double width() {
            return t.width;
        }
        public double height() {
            return t.height;
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

    static void layout(Tree t) { 
        WrappedTree wrapped = wrapTree(t);

        // "Walk zero" sets the y-coordinates, 
        // which depend only on the y_size of the ancestor nodes.
        wrapped.y(0);
        zerothWalk(wrapped);

        firstWalk(wrapped); 
        secondWalk(wrapped, 0); 
    }

    static WrappedTree wrapTree(Tree n) {
        if (n == null) return null;
        WrappedTree[] children = new WrappedTree[n.children.size()];
        for (int i = 0 ; i < children.length ; i++) {
            children[i] = (WrappedTree) wrapTree(n.children.get(i));
        }
        return new WrappedTree(n, children);
    }


    // Recursively set the y coordinate of the children, based on
    // the y coordinate of the parent, and its height
    static void zerothWalk(WrappedTree t) {
        double c_y = t.y() + t.height();
        for (int i = 0; i < t.cs; ++i) {
            t.c[i].y(c_y);
            zerothWalk(t.c[i]);
        }
    }

    static void firstWalk(WrappedTree t) {
        if (t.cs == 0) { 
            setExtremes(t); 
            return;
        }
        firstWalk(t.c[0]);

        // Create siblings in contour minimal vertical coordinate and index list.
        IYL ih = updateIYL(bottom(t.c[0].el), 0, null);                    
        for (int i = 1; i < t.cs; i++) {
            firstWalk(t.c[i]);

            // Store lowest vertical coordinate while extreme nodes still point in 
            // current subtree.
            double minY = bottom(t.c[i].er);                                
            seperate(t, i, ih);
            ih = updateIYL(minY, i, ih);                                     
        }
        positionRoot(t);
        setExtremes(t);
    }
      
    static void setExtremes(WrappedTree t) {
        if (t.cs == 0) {
            t.el = t; 
            t.er = t;
            t.msel = t.mser = 0;
        } 
        else {
            t.el = t.c[0].el; 
            t.msel = t.c[0].msel;
            t.er = t.c[t.cs-1].er; 
            t.mser = t.c[t.cs-1].mser;
        }
    }
      
    static void seperate(WrappedTree t, int i,  IYL ih) {
        // Right contour node of left siblings and its sum of modfiers. 
        WrappedTree sr = t.c[i-1]; 
        double mssr = sr.mod;

        // Left contour node of current subtree and its sum of modfiers.
        WrappedTree cl = t.c[i]; 
        double mscl = cl.mod;

        while (sr != null && cl != null) {
            if (bottom(sr) > ih.lowY) ih = ih.nxt;                                

            // How far to the left of the right side of sr is the left side of cl?}
            double dist = (mssr + sr.prelim + sr.width()) - (mscl + cl.prelim);
            if (dist > 0){
               mscl += dist;
               moveSubtree(t, i, ih.index, dist);
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
        if (sr == null && cl != null) setLeftThread(t, i, cl, mscl);

        // In this case, the left siblings must be taller than the current subtree.
        else if (sr != null && cl == null) setRightThread(t, i, sr, mssr);
    }

    static void moveSubtree(WrappedTree t, int i, int si, double dist) {
        // Move subtree by changing mod.
        t.c[i].mod += dist; 
        t.c[i].msel += dist; 
        t.c[i].mser += dist;
        distributeExtra(t, i, si, dist);                                  
    }
      
    static WrappedTree nextLeftContour(WrappedTree t) {
        return t.cs == 0 ? t.tl : t.c[0];
    }

    static WrappedTree nextRightContour(WrappedTree t) {
        return t.cs == 0 ? t.tr : t.c[t.cs-1];
    }

    static double bottom(WrappedTree t) { 
        return t.y() + t.height();  
    }
      
    static void setLeftThread(WrappedTree t, int i, WrappedTree cl, double modsumcl) {
       WrappedTree li = t.c[0].el;
       li.tl = cl;
       // Change mod so that the sum of modifier after following thread is correct.
       double diff = (modsumcl - cl.mod) - t.c[0].msel;
       li.mod += diff; 
       // Change preliminary x coordinate so that the node does not move.
       li.prelim -= diff;
       // Update extreme node and its sum of modifiers.
       t.c[0].el = t.c[i].el; 
       t.c[0].msel = t.c[i].msel;
    }
      
    // Symmetrical to setLeftThread.
    static void setRightThread(WrappedTree t, int i, WrappedTree sr, double modsumsr) {
       WrappedTree ri = t.c[i].er;
       ri.tr = sr;
       double diff = (modsumsr - sr.mod) - t.c[i].mser;
       ri.mod += diff; 
       ri.prelim -= diff;
       t.c[i].er = t.c[i-1].er; 
       t.c[i].mser = t.c[i-1].mser;
    }

    static void positionRoot(WrappedTree t) {
       // Position root between children, taking into account their mod.
       t.prelim = (t.c[0].prelim + t.c[0].mod + t.c[t.cs-1].mod + 
                   t.c[t.cs-1].prelim + t.c[t.cs-1].width())/2 - t.width()/2;
    }
      
    static void secondWalk(WrappedTree t, double modsum) {
        modsum += t.mod;
        // Set absolute (non-relative) horizontal coordinate.
        t.x(t.prelim + modsum);
        addChildSpacing(t);                                               
        for (int i = 0 ; i < t.cs ; i++) secondWalk(t.c[i], modsum);
    }

    static void distributeExtra(WrappedTree t, int i, int si, double dist) {           
        // Are there intermediate children?
        if (si != i - 1) {                                                    
            double nr = i - si;                                            
            t.c[si + 1].shift += dist / nr;
            t.c[i].shift -= dist / nr;
            t.c[i].change -= dist - dist / nr;
        }
    }
     
    // Process change and shift to add intermediate spacing to mod.
    static void addChildSpacing(WrappedTree t) {
        double d = 0, modsumdelta = 0;
        for (int i = 0; i < t.cs; i++) {
            d += t.c[i].shift;                                               
            modsumdelta += d + t.c[i].change;                                
            t.c[i].mod += modsumdelta;                                       
        }
    }

    // A linked list of the indexes of left siblings and their lowest vertical coordinate. 
    static class IYL {                                                          
        double lowY; int index; IYL nxt;                                 
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
}
