package org.klortho.flextree;

import java.io.File;
import java.io.PrintStream;

/**
 * Simple program illustrating how to use the library - reads a tree from a JSON file
 * named "before.json" in the run directory, lays it out, and then prints it out as
 * JSON to "after.json".
 */
public class Flextree {
    public static void main(String[] argv) {
        try {
            // Read it in from JSON
            File before = new File("before.json");
            Tree tree = Tree.fromJson(before);
            LayoutEngine.layout(tree);

            PrintStream after = new PrintStream("after.json");
            after.print(tree.toJson());
            after.close();
        }
        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
