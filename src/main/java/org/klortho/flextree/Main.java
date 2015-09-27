package org.klortho.flextree;

import java.io.PrintWriter;

import org.klortho.flextree.RenderSWT.KeyHandler;

public class Main {
	static Tree t;
	static LayoutEngine engine;

	static TreeGenerator treeGenerator;

	static RandomTreeGenerator gen =
		new RandomTreeGenerator(50, 20, 100, 20, 100, (int) Math.random() * 1000);

	
	public static void main(String argv[]) {
		try {
			
			if (argv.length == 0) {
				treeGenerator = gen;
			}
			else if (argv.length == 1) {
				treeGenerator = new FileTreeGenerator(argv[0]);
			}
			else if (argv[0].equals("--test")) {
				
			}
			engine = LayoutEngine.builder()
				     .setNodeSizeFunction(LayoutEngine.nodeSizeFromTree)
				     .build();
			
			// Two ways to make a tree:
			t = treeGenerator.makeTree();

			engine.layout(t);
			PrintWriter out = new PrintWriter("after.json");
			out.println(t.toJson());
			out.close();
	
			// The following function is used to handle when the user presses the `z` key in
			// the tree display. It generates a new random tree and re-renders.
			KeyHandler z_handler = new KeyHandler() {
				public void execute(RenderSWT r) {
					Tree t = treeGenerator.makeTree();
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


}
