package treelayout;

import java.io.PrintStream;
import java.util.Arrays;

public class Tester {

    public static long SEED = 46;
    
    public static void main(String[] argv){
        GenerateTrees gen = new GenerateTrees(200, 1, 10, 1, 10, SEED);

        TreeNode tree = gen.rand();

        try {
            PrintStream before = new PrintStream("before.json");
            tree.printJson(before, 0);
            before.close();
        }
        catch(Exception e) {}

        Marshall m = new Marshall();
        Object converted = m.convert(tree);
        m.runOnConverted(converted);
        m.convertBack(converted, tree);

        try {
            PrintStream after = new PrintStream("after.json");
            tree.printJson(after, 0);
            after.close();
        }
        catch(Exception e) {}
    }
}
