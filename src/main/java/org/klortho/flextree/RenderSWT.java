package org.klortho.flextree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This displays a Tree in SWT.
 * Note that when the tree window has the focus, you can execute a couple of functions by
 * pressing the following keys:
 *   - a - re-render
 *   - p - print the tree to stdout
 *   - z - (if enabled externally) generate a new random tree and render it.
 */

public class RenderSWT extends Composite {
	public static final double HGAP_DEFAULT = 10.0;
	public static final double VGAP_DEFAULT = 10.0;
	public static final double ZOOM_DEFAULT = 1.0;

	public double hgap;
	public double vgap;
	public double zoom;

	TreeSWT treeSWT;

	// This is the interface to use for the function to handle the 'z' key.
	public interface KeyHandler {
		abstract void execute(RenderSWT swt);
	}

	// By default, the 'z' key does nothing.
	static KeyHandler default_z_handler = new KeyHandler() {
		public void execute(RenderSWT swt) {}
	};

	/**
	 * This creates a new SWT window, and blocks. The render() methods are static
	 * factory methods, because we have to create our parent Display before instantiating
	 * this RenderSWT object.
	 */
	public static RenderSWT render(Tree tree) {
		return render(tree, default_z_handler, HGAP_DEFAULT, VGAP_DEFAULT, ZOOM_DEFAULT);
	}

	/**
	 * Render a tree with specific values for horizontal and vertical gaps between node
	 * boxes, and zoom level.
	 */
	public static RenderSWT render(Tree tree, double hgap, double vgap, double zoom) {
		return render(tree, default_z_handler, hgap, vgap, zoom);
	}

	/**
	 * Render a tree, passing in a function to handle the
	 * z keypress, for example, to generate a new random Tree.
	 */
	public static RenderSWT render(Tree tree, KeyHandler z_handler) {
		return render(tree, z_handler, HGAP_DEFAULT, VGAP_DEFAULT, ZOOM_DEFAULT);
	}

	/**
	 * This does the same thing, but allows you to pass in a function to handle the
	 * z keypress, for example, to generate a new random Tree.
	 */
	public static RenderSWT render(Tree tree, KeyHandler z_handler,
			double hgap, double vgap, double zoom) {
		final Display display = new Display();
		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout());
		shell.setSize(1000, 800);
		RenderSWT r = new RenderSWT(shell, tree, z_handler);
		shell.pack();
		shell.open();
		while (!shell.isDisposed ()) {
			if (display != null && !display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		return r;
	}
	
	/**
	 * Rerender a new tree in the same display.
	 */
	public void rerender(Tree tree) {
		treeSWT.render(tree);
	}

	// Constructor is private -- instances must be created by one of the 
	private RenderSWT(Composite parent, Tree tree, KeyHandler z_handler) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout(SWT.VERTICAL));
		treeSWT = new TreeSWT(this, tree, z_handler);
	}


}
