package treelayout;

import java.util.Arrays;

import treelayout.GenerateTrees;
import treelayout.TreeNode;

import treelayout.algorithm.Marshall;

public class Tester {

	public static long SEED = 42;
	
	public static void main(String[] argv){
		GenerateTrees gen = new GenerateTrees(200, 1, 10, 1, 10, SEED);

		TreeNode tree = gen.rand();
		Marshall m = new Marshall();
		Object converted = m.convert(tree);
		m.runOnConverted(converted);
		System.out.printf("done\n");
	}
	
}
