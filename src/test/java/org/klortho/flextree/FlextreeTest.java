package org.klortho.flextree;

import java.io.File;
import java.io.PrintStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.klortho.flextree.*;


/**
 * Unit test for simple App.
 */
public class FlextreeTest 
    extends TestCase
{
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
     * Test the layout algorithm against five pseudo-random trees
     */
    public void testApp()
    {
        try {
            for (int test_num = 1; test_num <= 5; ++test_num) {
                Tree tree = Tree.fromJson(getFile("before-" + test_num + ".json"));
                LayoutEngine.layout(tree);

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
            fail(e.getMessage());
        }

        assertTrue( true );
    }

    private File getFile(String name) {
        return new File(classLoader.getResource(name).getFile());
    }
}
