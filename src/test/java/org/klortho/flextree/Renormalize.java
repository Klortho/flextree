package org.klortho.flextree;

import java.io.IOException;
import java.io.PrintStream;

import org.klortho.flextree.TreeTestCases.TreeTestCase;

public class Renormalize {

	public static void main(String[] args) {
		try {
    	    TreeTestCases testCases = new TreeTestCases();
    	    for (int i = 0; i < 8; ++i) {
    	    	TreeTestCase tc = testCases.cases.get(i);
    	    	Tree expected = tc.getExpected();
    	    	double root_adj = -expected.x_size / 2;
    	    	adjX(expected, root_adj);
                PrintStream after = new PrintStream(tc.name + ".expected.json");
                after.print(expected.toJson());
                after.close();                
    	    }
		}
		catch(IOException e) {
			System.err.println("Failed: " + e.getMessage());
		}
	}
	
	public static void adjX(Tree t, double root_adj) {
		t.x = t.x + t.x_size/2 + root_adj;
		for (Tree k : t.children) {
			adjX(k, root_adj);
		}
	}

}
