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
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public FlextreeTest( String testName )
    {
        super( testName );
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
        TreeNode tree;
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassLoader classLoader = getClass().getClassLoader();
            File before = new File(classLoader.getResource("before-1.json").getFile());

            tree = mapper.readValue(before, TreeNode.class);

            Marshall m = new Marshall();
            Object converted = m.convert(tree);
            m.runOnConverted(converted);
            m.convertBack(converted, tree);

            PrintStream after = new PrintStream("after.json");
            tree.printJson(after, 0);
            after.close();
        }
        catch(Exception e) {
            fail(e.getMessage());
        }

        assertTrue( true );
    }
}
