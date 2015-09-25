package org.klortho.flextree;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class FlextreeTest extends TestCase {

    private ClassLoader classLoader;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public FlextreeTest( String testName )
    {
        super( testName );
        classLoader = getClass().getClassLoader();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( FlextreeTest.class );
    }
    
	/**
	 * Test to see if a tree has any overlaps among its nodes.
	 */
	public static boolean overlap(Tree tree) {
		ArrayList<Tree> nodes = tree.allNodes();
		for (int i = 0 ; i < nodes.size(); i++) {
			for (int j = 0 ; j < i ; j++) {
				if (nodeOverlaps(nodes.get(i), nodes.get(j))){
					System.out.printf("Overlap %d %d!!\n",i,j);
					return true;
				}
			}
		}
		return false;
	}

	private static boolean overlap(double xStart, double xEnd, 
			                       double xStart2, double xEnd2) 
	{
		return (xStart2 < xEnd && xEnd2 > xStart) ||
               (xStart < xEnd2 && xEnd > xStart2);
	}

	public static boolean nodeOverlaps(Tree a, Tree b) {
		return overlap(a.x, a.x + a.width, b.x , b.x + b.width) &&
			   overlap(a.y, a.y + a.height, b.y, b.y + b.height);
	}
	

	public static Tree sampleTree() {
		return new Tree(76.000000, 31.500000,
	      new Tree(67.500000, 59.500000,
  		    new Tree(73.500000, 10.000000,
		      new Tree(97.500000, 88.000000,
		        new Tree(38.000000, 28.500000,
		          new Tree(12.000000, 47.000000)
		        ),
		        new Tree(79.000000, 55.000000)
		      ),
		      new Tree(34.000000, 27.000000)
		    ),
		    new Tree(41.500000, 40.500000,
		      new Tree(86.500000, 10.500000,
		        new Tree(88.500000, 65.000000)
		      ),
		      new Tree(68.000000, 54.500000)
		    )
		  ),
		  new Tree(80.500000, 32.500000,
		    new Tree(26.000000, 38.000000,
		      new Tree(60.500000, 33.000000,
		        new Tree(32.500000, 95.500000,
		          new Tree(42.000000, 14.500000)
		        )
		      ),
		      new Tree(11.000000, 47.000000),
		      new Tree(38.000000, 57.500000)
		    ),
		    new Tree(80.000000, 99.000000),
		    new Tree(75.000000, 74.500000),
		    new Tree(38.500000, 47.000000)
		  ),
		  new Tree(98.500000, 89.000000,
		    new Tree(22.000000, 17.500000,
		      new Tree(45.500000, 79.000000),
		      new Tree(29.500000, 35.000000)
		    ),
		    new Tree(51.500000, 10.500000,
		      new Tree(87.000000, 80.500000)
		    ),
		    new Tree(86.500000, 85.500000)
		  ),
		  new Tree(77.000000, 76.000000,
		    new Tree(49.500000, 51.500000)
		  ),
		  new Tree(53.000000, 23.000000,
		    new Tree(75.000000, 71.000000),
		    new Tree(55.000000, 99.500000,
		      new Tree(62.500000, 63.500000)
		    )
		  ),
		  new Tree(11.000000, 53.500000,
		    new Tree(71.500000, 25.500000)
		  ),
		  new Tree(86.500000, 14.500000,
		    new Tree(61.500000, 18.500000,
		      new Tree(37.000000, 48.500000,
		        new Tree(40.000000, 32.500000)
		      )
		    )
		  ),
		  new Tree(27.500000, 84.000000,
		    new Tree(71.000000, 37.000000),
		    new Tree(94.500000, 58.000000)
		  ),
		  new Tree(37.000000, 38.000000,
		    new Tree(28.000000, 44.000000),
		    new Tree(12.000000, 47.000000,
		      new Tree(37.500000, 82.000000)
		    )
		  ),
		  new Tree(77.000000, 64.500000,
		    new Tree(13.500000, 68.500000)
		  )
		);
	}

	public void layoutAndCheckTree(Tree t) {
    	LayoutEngine tree = new LayoutEngine();
    	tree.layout(t);
    	assertFalse(overlap(t));
	}

	/**
	 * Test the sample tree
	 */
	public void testSampleTree() {
		Tree t = sampleTree();
		layoutAndCheckTree(t);
		// FIXME: rigorously test *all* nodes' results.
	}

    /** 
     * Generate a pseudo random tree, lay it out, and make sure there are no overlaps.
     */
    public void testRandomTree()
    {
    	int SEED = 43;
    	Tree t = RandomTreeGenerator.makeTree(50, 10, 100, 10, 100, SEED);
    	layoutAndCheckTree(t);
    }
    
    /**
     * Test the layout algorithm against five pseudo-random trees, read from JSON
     */
    public void testApp()
    {
        try {
            for (int test_num = 1; test_num <= 8; ++test_num) {
                Tree tree = Tree.fromJson(getFile("before-" + test_num + ".json"));
                tree.print();
        		layoutAndCheckTree(tree);

                String expected_name = "after-" + test_num + ".json";
                Tree expected = Tree.fromJson(getFile(expected_name));

                boolean success = tree.deepEquals(expected);
                if (!success) {
                    PrintStream after = new PrintStream("after.json");
                    after.print(tree.toJson());
                    after.close();                
                }
                assertTrue("Difference found in results for " + expected_name + 
                    ", results written to after.json",
                    success);
            }
        }
        catch(Exception e) {
        	System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        assertTrue( true );        
    }

    private File getFile(String name) {
    	System.out.println("name = " + name);
        return new File(classLoader.getResource(name).getFile());
    }

}
