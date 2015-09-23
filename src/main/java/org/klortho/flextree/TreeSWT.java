package org.klortho.flextree;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.klortho.flextree.RenderSWT.KeyHandler;


/**
 * This class is managed by RenderSWT -- you shouldn't need to access it directly.
 */

public class TreeSWT 
  extends Composite 
  implements SelectionListener, PaintListener, ControlListener , Listener, KeyListener
{
	Tree tree;
	
	double xOffset, yOffset;
	double width, height;
	static int SEED = 43;
	Random rand = new Random(SEED);

	static final double HGAP_DEFAULT = 10.0;
	static final double VGAP_DEFAULT = 10.0;
	static final double ZOOM_DEFAULT = 1.0;

	double hgap = HGAP_DEFAULT;
	double vgap = VGAP_DEFAULT;
	double zoom = ZOOM_DEFAULT;

	KeyHandler z_handler; 
	
	public TreeSWT(Composite parent, Tree tree, KeyHandler z_handler) {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		this.z_handler = z_handler;
		init(tree);
	}
	
	private void init(Tree tree) {
		this.tree = tree;
		addPaintListener(this);
		getHorizontalBar().addSelectionListener(this);
		getVerticalBar().addSelectionListener(this);
		addControlListener(this);	
		addKeyListener(this);
		addListener(SWT.MouseVerticalWheel, this);
		render();
	}
	
	/**
	 * Render the Tree that was passed in from the constructor.
	 */
	public void render() {
		// FIXME: what does this do?
		tree.layer();
		LayoutEngine.layout(tree);
		// FIXME: what does this do?
		tree.normalizeX();
		BoundingBox b = tree.getBoundingBox();
		width = b.width;
		height = b.height;
	}

	/**
	 * Render a new Tree, with default values for hgap, vgap, and zoom.
	 */
	public void render(Tree tree) {
		// Here are the default values for hgap, vgap, and zoom:
		render(tree, HGAP_DEFAULT, VGAP_DEFAULT, ZOOM_DEFAULT);
	}

	/**
	 * Render a new Tree, with different values for the gaps and the zoom.
	 */
	public void render(Tree tree, double hgap, double vgap, double zoom) {
		this.tree = tree;
		this.hgap = hgap;
		this.vgap = vgap;
		this.zoom = zoom;
		render();
	}


	public void setScrollBars() {
		Rectangle r = getClientArea();
		double w = r.width / zoom;
		if (w < width) {
			getHorizontalBar().setVisible(true);
			getHorizontalBar().setMinimum(0);
			getHorizontalBar().setMaximum((int) width);
			getHorizontalBar().setThumb((int) (r.width / zoom));
			getHorizontalBar().setIncrement(50);
			getHorizontalBar().setPageIncrement((int) (r.width / zoom));
		} 
		else {
			getHorizontalBar().setVisible(false);
		}
		double h = r.height / zoom;
		if (h < height) {
			getVerticalBar().setVisible(true);
			getVerticalBar().setMinimum(0);
			getVerticalBar().setMaximum((int) height);
			getVerticalBar().setThumb((int) (r.height/zoom));
			getVerticalBar().setIncrement(50);
			getVerticalBar().setPageIncrement((int) (r.height/zoom));
		} 
		else {
			getVerticalBar().setVisible(false);
		}
	}
	
	static int roundInt(double b) {
		return (int) (b + 0.5);
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		xOffset = getHorizontalBar().getSelection();
		yOffset = getVerticalBar().getSelection();
		redraw();
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {	
	}
	
	void paintTree(Tree root, GC gc, Rectangle r) {
		Color c = new Color(gc.getDevice(), 
				new RGB((int) ((rand.nextDouble() * 150)), 
						(int) ((rand.nextDouble() * 150)), 
						(int) ((rand.nextDouble() * 150))));
		gc.setBackground(c);
		gc.fillRectangle(roundInt(zoom * (root.x + hgap / 2 - xOffset)), 
				         roundInt(zoom * (root.y + vgap / 2 - yOffset)), 
				         roundInt(zoom * (root.width - hgap)), 
				         roundInt(zoom * (root.height - vgap)));
		gc.setAlpha(255);
		gc.drawRectangle(roundInt(zoom * (root.x + hgap / 2 - xOffset)), 
				         roundInt(zoom * (root.y + vgap / 2 - yOffset)), 
				         roundInt(zoom * (root.width - hgap)), 
				         roundInt(zoom * (root.height - vgap)));
		c.dispose();

		if (root.children.size() > 0) {
			double endYRoot = root.y + root.height - vgap / 2 ;
			double rootMiddle = root.x + root.width / 2.0;
			double middleY = endYRoot + vgap / 2;
			gc.drawLine(roundInt(zoom * (rootMiddle - xOffset)), 
					    roundInt(zoom * (endYRoot - yOffset)), 
					    roundInt(zoom * (rootMiddle - xOffset)),
					    roundInt(zoom * (middleY - yOffset)) );
			Tree firstKid = root.children.get(0);
			double middleFirstKid =  firstKid.x + firstKid.width/2.0;
			Tree lastKid = root.children.get(root.children.size()-1);
			double middleLastKid = lastKid.x + lastKid.width/2.0;
			gc.drawLine(roundInt(zoom * (middleFirstKid - xOffset)), 
					    roundInt(zoom * (middleY - yOffset)), 
					    roundInt(zoom * (rootMiddle - xOffset)),
					    roundInt(zoom * (middleY - yOffset)));
			gc.drawLine(roundInt(zoom * (middleFirstKid - xOffset)), 
					    roundInt(zoom * (middleY - yOffset)), 
					    roundInt(zoom * (middleLastKid - xOffset)),
					    roundInt(zoom * (middleY - yOffset)));
			
			for (Tree kid : root.children) {
				double middleKid = kid.x + kid.width / 2.0;
				paintTree(kid, gc, r);
				gc.drawLine(roundInt(zoom * (middleKid - xOffset)), 
						    roundInt(zoom * (middleY - yOffset)), 
						    roundInt(zoom * (middleKid - xOffset)), 
						    roundInt(zoom * (kid.y + vgap / 2.0 - yOffset)));
			}
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.setAdvanced(true);
		Rectangle r = getClientArea();
		e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		e.gc.fillRectangle(r);
		e.gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		paintTree(tree, e.gc, r);
	}

	@Override
	public void controlMoved(ControlEvent e) {
	}

	@Override
	public void controlResized(ControlEvent e) {
		setScrollBars();
	}

	@Override
	public void handleEvent(Event event) {
		Rectangle r = getClientArea();
		
		if ((event.stateMask & SWT.CONTROL) != 0){
			event.doit= false;
			double locX = xOffset + event.x / zoom;
			double locY = yOffset + event.y / zoom;
			if (event.count > 0){
				zoom *= 1.05;
			} 
			else {
				zoom /= 1.05;
			}
			xOffset = Math.min(Math.max(0, locX - event.x / zoom), width - r.width /zoom);
			yOffset = Math.min(Math.max(0, locY - event.y / zoom), height - r.height /zoom);
			getHorizontalBar().setSelection((int) xOffset);
			getVerticalBar().setSelection((int) yOffset);
			setScrollBars();
			
			redraw();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode == 'z') {
			z_handler.execute(this);
		} 
		else if (e.keyCode == 'a') {
			render();
		} 
		else if (e.keyCode == 'p') {
			tree.print();
			System.out.printf("\n");
		}
		setScrollBars();
		redraw();
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
