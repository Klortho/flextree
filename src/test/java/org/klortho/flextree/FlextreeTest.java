package org.klortho.flextree;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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

	
	// Convenience method that's shared by a couple of tests. This creates a
	// layout engine that uses nodeSizeFromTree, that gets the node sizes from
	// the x_size and y_size attributes of each tree node.
	public void layoutAndCheckTree(Tree t) {
		LayoutEngine engine = LayoutEngine.builder()
			                      .setNodeSizeFunction(LayoutEngine.nodeSizeFromTree)
			                      .build();
    	engine.layout(t);
    	checkOverlap(t);
	}

	// This is used for logging messages related to one particular (sub)test.
	public static class StringPrintStream {
		public PrintStream ps;
		public ByteArrayOutputStream os;
		public StringPrintStream() {
			os = new ByteArrayOutputStream();
			ps = new PrintStream(os);
		}
		public String toString() {
			return os.toString();
		}
	}

	public static void checkOverlap(Tree t) {
		checkOverlap(t, new StringPrintStream());
	}
	
	public static void checkOverlap(Tree t, StringPrintStream out) {
		boolean has = t.hasOverlappingNodes(out.ps);
		if (has) {
			String msg = out.toString();
			System.out.println(msg);
            fail(msg);
		}
	}

	/**
	 * Test the sample tree
	 */
	public void testSampleTree() {
		Tree t = sampleTree();
		layoutAndCheckTree(t);
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
    
    /*
     * The bulk of our tests will be read from the tests.json file. This class holds
     * the data from one of the objects in the list from that file.
     */
    public static class TestCase {
    	public String name;
    	public String description;
    	public String tree;
    	public String sizing;
    	public String gap;
    	public TestCase() {}

        public static ObjectMapper json_mapper;
        static {
            json_mapper = new ObjectMapper();
            json_mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
    }

    /**
     * Test the layout algorithm against the collection of
     * tests in test/resources/tests.json
     */
    public void testApp()
    {
        try {
        	List<TestCase> test_cases = TestCase.json_mapper.readValue(
        			getFile("test-cases/tests.json"),
        			new TypeReference<List<TestCase>>() { } );
        	//System.out.println("num test_cases = " + test_cases.size());
        	
        	for (TestCase test_case : test_cases) {
        		StringPrintStream out = new StringPrintStream();
        		out.ps.print("Test " + test_case.name + ": ");

                Tree tree = Tree.fromJson(getFile("test-cases/" + test_case.tree));
                //tree.print();
                LayoutEngine.Builder b = LayoutEngine.builder();

                if (test_case.sizing.equals("node-size-function")) {
    		        b.setNodeSizeFunction(LayoutEngine.nodeSizeFromTree);
                }
                else if (test_case.sizing.equals("node-size-fixed")) {
                    b.setNodeSizeFixed(new double[] {50, 50});
                }
                else {
                	// FIXME: need to implement this
                	continue;
                }
   		        LayoutEngine engine = b.build();
                
    			engine.layout(tree);
    			checkOverlap(tree, out);

                String expected_name = test_case.name + ".expected.json";
                Tree expected = Tree.fromJson(getFile("test-cases/" + expected_name));

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
            fail(e.getMessage());
        }
        assertTrue( true );        
    }

    private File getFile(String name) {
    	//System.out.println("name = " + name);
        return new File(classLoader.getResource(name).getFile());
    }

}
