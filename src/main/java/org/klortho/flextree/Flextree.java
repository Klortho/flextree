package org.klortho.flextree;

import java.io.File;

import org.klortho.flextree.RenderSWT.KeyHandler;

public class Flextree {
	static RandomTreeGenerator gen;
	static Tree tree;

	public static void main(String argv[]) {
		gen = new RandomTreeGenerator(50, 20, 100, 20, 100, (int) Math.random() * 1000);
		
		// Two ways to make a tree:
		//tree = gen.randomTree();
		try {
            tree = Tree.fromJson(new File("src/test/resources/before-6.json"));
		} catch(Exception e) {}

		
		KeyHandler z_handler = new KeyHandler() {
			public void execute(TreeSWT swt) { 
				swt.tree = gen.randomTree();
				swt.render();
			}
		};
		RenderSWT.render(tree, z_handler);
	}
}
