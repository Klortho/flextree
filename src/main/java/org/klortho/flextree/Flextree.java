package org.klortho.flextree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Flextree extends Composite {

	TreeElement a, b;
	Flextree(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout(SWT.VERTICAL));
		a = new TreeElement(this);
	}

	public static void main(String argv[]) {
		final Display display = new Display ();
		final Shell shell = new Shell (display, SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout ());
		shell.setSize (1000, 800);
		new Flextree(shell);
		shell.pack();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (display != null && !display.readAndDispatch ())
				display.sleep ();
		}
		display.dispose();
	}
}
