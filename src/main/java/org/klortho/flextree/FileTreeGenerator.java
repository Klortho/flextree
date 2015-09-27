package org.klortho.flextree;

import java.io.File;
import java.io.IOException;

/**
 * Reads a tree from a JSON file
 */
public class FileTreeGenerator implements TreeGenerator {
	String filename;
	
	public FileTreeGenerator(String filename) {
		this.filename = filename;
	}
	
	public Tree makeTree() {
		Tree t = null;
		try {
			t = Tree.fromJson(new File(filename));
		}
		catch(IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		return t;
	}
}
