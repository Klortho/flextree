package org.klortho.flextree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.klortho.flextree.RenderSWT.KeyHandler;
import org.klortho.flextree.TreeTestCases.TreeTestCase;

/**
 * This renders a tree in SWT, so you can check it out.
 * Usage:
 * - With no arguments, it generates a random tree
 * - <file> - lays out and then renders the tree in that JSON file
 * - --nolayout <file> - skips laying it out, and renders it
 * - --test <test-name> - lays out that test tree, and renders it
 * - --expected <test-name> - renders the expected tree (without laying it out)
 */
public class Render {
    static Tree t;
    static LayoutEngine engine;
    static TreeGenerator treeGenerator;
    static TreeTestCases testCases;
    static TreeTestCase testCase = null;
    static boolean doLayout = true;
    
    public static void main(String argv[]) {
        try {
             testCases = new TreeTestCases();
            
            if (argv.length == 0) {
                treeGenerator = 
                    new RandomTreeGenerator(50, 20, 100, 20, 100, 
                            (int) Math.random() * 1000);
            }
            else if (argv.length == 1) {
                treeGenerator = new FileTreeGenerator(argv[0]);
            }
            else if (argv[0].equals("--nolayout")) {
                treeGenerator = new FileTreeGenerator(argv[1]);
                doLayout = false;
            }
            else if (argv[0].equals("--test")) {
                String test_name = argv[1];
                testCase = testCases.getTestCase(test_name);
                if (testCase == null) {
                    System.err.println("No test found with that name");
                    System.exit(1);
                }               
                treeGenerator = new TreeGenerator() {
                    public Tree makeTree() {
                        try {
                            return testCase.getTreeData();
                        }
                        catch (IOException e) {
                            return null;
                        }
                    }
                };
            }
            else if (argv[0].equals("--expected")) {
                String test_name = argv[1];
                testCase = testCases.getTestCase(test_name);
                if (testCase == null) {
                    System.err.println("No test found with that name");
                    System.exit(1);
                }
                treeGenerator = new TreeGenerator() {
                    public Tree makeTree() {
                        try {
                            return testCase.getExpected();
                        }
                        catch (IOException e) {
                            return null;
                        }
                    }
                };
                doLayout = false;
            }

            t = treeGenerator.makeTree();
            if (doLayout) {
                System.out.println("Doing layout");
                LayoutEngine engine;
                
                if (testCase == null) {
                    engine = LayoutEngine.builder()
                            .setSetNodeSizes(true)
                            .setNodeSizeFunction(LayoutEngine.nodeSizeFromTree)
                            .build();
                }
                else {
                    engine = testCase.getLayoutEngine();
                }
                engine.layout(t);
            }
            
            System.out.println("Writing layed out tree data to 'after.json'.");
            PrintWriter out = new PrintWriter("after.json");
            out.println(t.toJson());
            out.close();
    
            // The following function is used to handle when the user presses the `z` key in
            // the tree display. It re-generates the tree, and re-renders.
            KeyHandler z_handler = new KeyHandler() {
                public void execute(RenderSWT r) {
                    Tree t = treeGenerator.makeTree();
                    if (doLayout) engine.layout(t);
                    r.rerender(t);
                }
            };
            
            RenderSWT.render(t, z_handler);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
