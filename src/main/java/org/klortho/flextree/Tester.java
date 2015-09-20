package org.klortho.flextree;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * This program generates a large random tree, prints it out to before.json.
 * It then runs it through the layout, and prints the results to after.json.
 * Twiddle the SEED, otherwise you'll get the same tree every time.
 */
public class Tester {

    public static long SEED = 46;
    
    public static void main(String[] argv){
        try {

            GenerateTrees gen = new GenerateTrees(200, 1, 10, 1, 10, SEED);

            TreeNode tree = gen.rand();

            PrintStream before = new PrintStream("before.json");
            before.print(tree.toJson());
            before.close();

            Object converted = LayoutEngine.convert(tree);
            LayoutEngine.runOnConverted(converted);
            LayoutEngine.convertBack(converted, tree);

            PrintStream after = new PrintStream("after.json");
            after.print(tree.toJson());
            after.close();
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
