package org.klortho.flextree;

import java.io.File;

import org.klortho.flextree.LayoutEngine.Separation;
import org.klortho.flextree.RenderSWT.KeyHandler;

public class Main {
	static RandomTreeGenerator gen;
	static Tree t;

	public static void main(String argv[]) {
		gen = new RandomTreeGenerator(50, 20, 100, 20, 100, (int) Math.random() * 1000);
		
		// Two ways to make a tree:
		//t = gen.randomTree();
		try {
            t = Tree.fromJson(new File("src/test/resources/before-6.json"));
		} catch(Exception e) {}
		layout(t);

		// The following function is used to handle when the user presses the `z` key in
		// the tree display. It generates a new random tree and re-renders.
		KeyHandler z_handler = new KeyHandler() {
			public void execute(RenderSWT r) {
				Tree t = gen.randomTree();
				layout(t);
				r.rerender(t);
			}
		};
		
		RenderSWT.render(t, z_handler);
	}
	
	static void layout(Tree t) {
		Separation separation = new Separation() {
			public double s(Tree a, Tree b) {
				//return a.parent == b.parent ? 0 : 1;
				return 1;
			}
		};
		LayoutEngine.layout(t, separation);
	}
}
