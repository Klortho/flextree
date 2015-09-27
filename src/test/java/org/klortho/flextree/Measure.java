package org.klortho.flextree;

/**
 * Test how long it takes to layout trees of various sizes. For each tree size
 * (numNodes) up to MAX_SIZE, stepping in intervals of INCREMENT, this runs
 * NUM_TESTS tests.
 */
public class Measure {

	public static int MAX_SIZE = 10000;
	public static int INCREMENT = 100;
	public static int NUM_TESTS = 200;
	public static long SEED = 42;
	static LayoutEngine engine;

	public static void main(String[] argv) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		engine = LayoutEngine.builder()
                     .setNodeSizeFunction(LayoutEngine.nodeSizeFromTree)
                     .build();

		for (int numNodes = 1; numNodes < MAX_SIZE; numNodes += INCREMENT) {
			RandomTreeGenerator gen = new RandomTreeGenerator(numNodes, 1, 10, 1, 10, 5000);
			runTests(gen, NUM_TESTS);
		}
	}

	static long timeLayout(Tree tree) {
		System.gc();
		long start = System.nanoTime();
		engine.layout(tree);
		long now = System.nanoTime();
		return now - start;
	}

	static long runTests(RandomTreeGenerator gen, int numTests) {
		for (int i = 0 ; i < numTests * gen.numNodes ; i++) {
			Tree t = gen.makeTree();
			long nanoseconds = timeLayout(t);
			System.out.printf("%d %d\n", gen.numNodes, nanoseconds);
		}
		return 0;
	}

	
}
