package org.klortho.flextree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.klortho.flextree.RenderSWT.KeyHandler;

public class Main {
	static RandomTreeGenerator gen;
	static Tree t;
	static LayoutEngine engine;

	public static void main(String argv[]) {
		try {
			engine = LayoutEngine.builder()
					     .setNodeSizeFunction(LayoutEngine.nodeSizeFromTree)
					     .build();
			gen = new RandomTreeGenerator(50, 20, 100, 20, 100, (int) Math.random() * 1000);
			
			// Two ways to make a tree:
			//t = gen.randomTree();
	        t = getTreeFromFile("before.json");
			engine.layout(t);
			PrintWriter out = new PrintWriter("after.json");
			out.println(t.toJson());
			out.close();
	
			// The following function is used to handle when the user presses the `z` key in
			// the tree display. It generates a new random tree and re-renders.
			KeyHandler z_handler = new KeyHandler() {
				public void execute(RenderSWT r) {
					Tree t = getTreeFromFile("before.json");
					engine.layout(t);
					r.rerender(t);
				}
			};
			
			RenderSWT.render(t, z_handler);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	static Tree getTreeFromFile(String name) {
		Tree t = Tree.NULL;
		try {
			t = Tree.fromJson(new File(name));
		}
		catch(IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		return t;
	}
}
