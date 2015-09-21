package org.klortho.flextree;

import java.util.Random;

/**
 * Tree generator. 
 */

public class GenerateTrees {

    /**
     * Constructor:
     *   nr - the number of nodes that will be in each tree
     *   min..., max... - constraints for the sizes of each node
     */ 
    public GenerateTrees(int nr,
            double minWidth,  double maxWidth, double minHeight, 
            double maxHeight, long seed){
        rand = new Random(seed);
        this.nr = nr;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    /**
     * Returns a random tree with a set number of nodes.
     */
    public Tree rand(){
        return randomTree(nr);
    }

    Random rand;
    public int nr;
    double minWidth, maxWidth;
    double minHeight, maxHeight;
    
    public Tree randomTree(int nr) {
        Tree root = randNode();
        for (int i = 0 ; i < nr - 1 ; i++){
            root.randExpand(randNode(), rand);
        }
        return root;
    }
    
    private Tree randNode() {
        return new Tree( getRandomInRange(minWidth, maxWidth),
                             getRandomInRange(minHeight, maxHeight));
    }
    
    private int getRandomInRange(int start, int end){
        double r = rand.nextDouble();
        return start + (int)Math.rint(r * (end - start));
    }
    
    private double getRandomInRange(double start, double end){
        double r = rand.nextDouble();
        return Math.rint((start + r * (end - start)) * 2)/2;
    }
}