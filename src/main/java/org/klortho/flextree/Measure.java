package org.klortho.flextree;

public class Measure {

	public static int MAX_SIZE = 10000;
	public static int INCREMENT = 100;
	public static int NR_TESTS = 200;
	public static int NR_WARMUP = 100;
	public static long SEED = 42;
	
	static long timeLayout(Tree tree){
		System.gc();
		Marshall m = new Marshall();
		Object converted = m.convert(tree);
		long start = System.nanoTime();
		m.runOnConverted(converted);
		long now = System.nanoTime();
		return now - start;
	}

	static long runTests(RandomTreeGenerator gen,  int nrTests) {
		for(int i = 0 ; i < nrTests * gen.numNodes ; i++){
			Tree tree = gen.randomTree();
			long res = timeLayout(tree);
			System.out.printf("%d %d\n",gen.numNodes,res);
		}
		return 0;
	}
	
	static void measureArbitrarilySized(){
		for (int i = 1; i < MAX_SIZE; i+= INCREMENT){
			RandomTreeGenerator gen = new RandomTreeGenerator(i, 1, 10,1, 10, 5000);
			runTests(gen, NR_TESTS);
		}
	}
	
	public static void main(String[] argv) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		measureArbitrarilySized();		
	}
}
