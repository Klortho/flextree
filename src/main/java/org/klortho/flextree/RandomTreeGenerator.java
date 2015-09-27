package org.klortho.flextree;

import java.util.Random;


public class RandomTreeGenerator implements TreeGenerator {
	Random rand;
	public int numNodes;
	double minWidth, maxWidth;
	double minHeight, maxHeight;

	/**
	 * Convenience static method to generate one random tree.
	 */
	public static Tree makeTree(int numNodes, double minWidth,  double maxWidth, double minHeight, 
			double maxHeight, long seed) 
	{
		RandomTreeGenerator g = new RandomTreeGenerator(numNodes, minWidth, 
				maxWidth, minHeight, maxHeight, seed);
		return g.makeTree();
	}
	
	/**
	 * Constructor. Specify the min and max of the widths and heights of the nodes,
	 * and the number of nodes. Additionally, specify a random number seed.
	 */
	public RandomTreeGenerator(int numNodes,
			double minWidth,  double maxWidth, double minHeight, 
			double maxHeight, long seed) 
	{
		rand = new Random(seed);
		this.numNodes = numNodes;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}

	/**
	 * Return a random tree with a set number of nodes. Each node has a random 
	 * width and height, and is placed randomly into the tree.
	 */
	public Tree makeTree() {
		Tree root = randomNode();
		for (int i = 0 ; i < numNodes - 1 ; i++) {
			randExpand(root, randomNode());
		}
		return root;		
	}

	private Tree randomNode() {
		return new Tree(getRandomInRange(minWidth, maxWidth),
			            getRandomInRange(minHeight, maxHeight));		
	}

	// Insert the new node into a random place within this subtree
	private void randExpand(Tree start, Tree newNode) {
		int i = rand.nextInt(start.children.size() + 1);
		if (i == start.children.size()) {
			start.children.add(newNode);
		} 
		else {
			randExpand(start.children.get(i), newNode);
		}
	}

	private double getRandomInRange(double start, double end) {
		double r = rand.nextDouble();
		return Math.rint((start + r * (end - start)) * 2) / 2;
	}
}