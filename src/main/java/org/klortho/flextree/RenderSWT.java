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
	TreeSWT treeSWT;

	// This is the interface to use for the function to handle the 'z' key.
	public interface KeyHandler {
		abstract void execute(TreeSWT swt);
	}

	// By default, the 'z' key does nothing.
	static KeyHandler default_z_handler = new KeyHandler() {
		public void execute(TreeSWT swt) {}
	};

	private RenderSWT(Composite parent, Tree tree, KeyHandler z_handler) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout(SWT.VERTICAL));
		treeSWT = new TreeSWT(this, tree, z_handler);
	}

	/**
	 * This creates a new SWT window, and blocks.
	 */
	public static void render(Tree tree) {
		render(tree, default_z_handler);
	}

	/**
	 * This does the same thing, but allows you to pass in a function to handle the
	 * z keypress, for example, to generate a new random Tree.
	 */
	public static void render(Tree tree, KeyHandler z_handler) {
		final Display display = new Display();
		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout());
		shell.setSize(1000, 800);
		new RenderSWT(shell, tree, z_handler);
		shell.pack();
		shell.open();
		while (!shell.isDisposed ()) {
			if (display != null && !display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
