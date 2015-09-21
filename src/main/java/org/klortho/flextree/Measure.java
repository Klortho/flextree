package org.klortho.flextree;

import java.util.Arrays;

public class Measure {

    public static int MAX_SIZE = 10000;
    public static int INCREMENT = 100;
    public static int NR_TESTS = 200;
    public static int NR_WARMUP = 100;
    public static long SEED = 42;
    
    static long timeLayout(Tree tree){
        System.gc();
        long start = System.nanoTime();
        LayoutEngine.layout(tree);
        long now = System.nanoTime();
        return now - start;
    }

    static long runTests(GenerateTrees gen,  int nrTests) {
        for (int i = 0 ; i < nrTests * gen.nr ; i++){
            Tree tree = gen.rand();
            long res = timeLayout(tree);
            System.out.printf("%d %d\n",gen.nr,res);
        }
        return 0;
    }
    
    public static void main(String[] argv){
        for (int i = 1; i < MAX_SIZE; i+= INCREMENT) {
            GenerateTrees gen = new GenerateTrees(i, 1, 10, 1, 10, 5000);
            runTests(gen, NR_TESTS);
        }
    }
    
}
