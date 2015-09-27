package org.klortho.flextree;

import java.io.IOException;
import java.io.PrintStream;

import org.klortho.flextree.TreeTestCases.TreeTestCase;

public class Renormalize {

	public static void main(String[] args) {
		try {
    	    TreeTestCases testCases = new TreeTestCases();
    	    for (int i = 0; i < 11; ++i) {
    	    	TreeTestCase tc = testCases.cases.get(i);
    	    	Tree expected = tc.getExpected();
    	    	//double rootX = expected.x;
    	    	//moveX(expected, -rootX);
                PrintStream after = new PrintStream(tc.name + ".expected.json");
                after.print(expected.toJson());
                after.close();                
    	    }
		}
		catch(IOException e) {
			System.err.println("Failed: " + e.getMessage());
		}
	}
	
	public static void moveX(Tree t, double dx) {
		t.x = t.x + dx;
		for (Tree k : t.children) {
			moveX(k, dx);
		}
	}

}
