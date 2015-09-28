package org.klortho.flextree;
import java.util.Random;
import java.util.Stack;

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

import com.fasterxml.jackson.core.JsonProcessingException;


/**
 * This class is managed by RenderSWT -- you shouldn't need to access it directly.
 * 
 * Construct this with an already-layed out 
 * tree. You can update the display with a new tree at any time, by calling the render()
 * method.
 */

// FIXME: take out the methods that allow the user to set hgap, vgap, and zoom.

public class TreeSWT 
  extends Composite 
  implements SelectionListener, PaintListener, ControlListener , Listener, KeyListener
{
    RenderSWT parent;
    Tree t;
    WrappedTree wt;
    
    double xOffset, yOffset;
    double width, height;
    static int SEED = 43;
    Random rand = new Random(SEED);

    double hgap;
    double vgap;
    double zoom;
    double delta_x;

    KeyHandler z_handler; 
    
    public TreeSWT(Composite parent, Tree t, KeyHandler z_handler,
            double hgap, double vgap, double zoom) {
        super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        this.parent = (RenderSWT) parent;
        this.t = t;
        this.z_handler = z_handler;
        this.hgap = hgap;
        this.vgap = vgap;
        this.zoom = zoom;

        render();
        addPaintListener(this);
        getHorizontalBar().addSelectionListener(this);
        getVerticalBar().addSelectionListener(this);
        addControlListener(this);   
        addKeyListener(this);
        addListener(SWT.MouseVerticalWheel, this);
    }

    /**
     * Re-render, with a new tree.
     */
    public void rerender(Tree tree) {
        this.t = tree;
        render();
    }

    // This class wraps each node of the tree, storing the color that will be
    // used to render the rectangle. This also un-normalizes the x-coordinate,
    // such that the minimum one used by SWT is 0.
    class WrappedTree {
        Tree tree;
        RGB rgb;
        WrappedTree[] children;

        public WrappedTree(Tree tree) {
            this.tree = tree;
            rgb = new RGB((int) ((rand.nextDouble() * 150)), 
                          (int) ((rand.nextDouble() * 150)), 
                          (int) ((rand.nextDouble() * 150)));

            children = new WrappedTree[tree.children.size()];
            for (int i = 0 ; i < children.length ; i++) {
                children[i] = new WrappedTree(tree.children.get(i));
            }
        }
        public double x() { return tree.x + delta_x; }
        public double y() { return tree.y; }
        public double x_size() { return tree.x_size; }
        public double y_size() { return tree.y_size; }
        public double x_min() { return x() - x_size()/2; }
        public double x_max() { return x() + x_size()/2; }
    }
    
    private void autoZoom() {
        // Compute the averages, etc.
        double num_nodes = 0;
        double sum_x_size = 0;
        double sum_y_size = 0;
        double sum_area = 0;
        double min_x_size = t.x_size;
        double min_y_size = t.y_size;
        double min_x = 0 - t.x_size/2;
        Stack<Tree> toVisit = new Stack<Tree>();
        toVisit.add(t);
        while (toVisit.size() > 0) {
            Tree node = toVisit.pop();
            num_nodes++;
            sum_x_size += node.x_size;
            sum_y_size += node.y_size;
            sum_area += node.x_size * node.y_size;
            min_x_size = Math.min(min_x_size, node.x_size);
            min_y_size = Math.min(min_y_size, node.y_size);
            min_x = Math.min(min_x, node.x - node.x_size/2);
            toVisit.addAll(node.children);
        }
        // Adjust zoom so that the average area is 5000 sq pixels
        double ave_x_size = sum_x_size / num_nodes;
        double ave_y_size = sum_y_size / num_nodes;
        double ave_area = sum_area / num_nodes;
        zoom = Math.sqrt(5000 / ave_area);
        // The hgap should be 10% of the average x-size, or the min x-size, whichever
        // is less
        //hgap = Math.min(ave_x_size / 10, min_x_size);
        hgap = 0;
        vgap = Math.min(ave_y_size / 10, min_y_size);
        delta_x = -min_x;
    }

    /**
     * Render the Tree. This is called both from the constructor and from the
     * rerender() method.
     */
    private void render() {
        autoZoom();
        
        BoundingBox b = new BoundingBox(t);
        width = b.x_size();
        height = b.y_size();        
        wt = new WrappedTree(t);
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
    
    void paintTree(WrappedTree wt, GC gc, Rectangle r) {
        Color c = new Color(gc.getDevice(), wt.rgb);

        gc.setBackground(c);
        gc.fillRectangle(roundInt(zoom * (wt.x_min() + hgap / 2 - xOffset)), 
                         roundInt(zoom * (wt.y() + vgap / 2 - yOffset)), 
                         roundInt(zoom * (wt.x_size() - hgap)), 
                         roundInt(zoom * (wt.y_size() - vgap)));
        
        gc.setAlpha(255);
        gc.drawRectangle(roundInt(zoom * (wt.x_min() + hgap / 2 - xOffset)), 
                         roundInt(zoom * (wt.y() + vgap / 2 - yOffset)), 
                         roundInt(zoom * (wt.x_size() - hgap)), 
                         roundInt(zoom * (wt.y_size() - vgap)));

        c.dispose();

        if (wt.children.length > 0) {
            double endYRoot = wt.y() + wt.y_size() - vgap / 2 ;
            double rootMiddle = wt.x();
            double middleY = endYRoot + vgap / 2;
            gc.drawLine(roundInt(zoom * (rootMiddle - xOffset)), 
                        roundInt(zoom * (endYRoot - yOffset)), 
                        roundInt(zoom * (rootMiddle - xOffset)),
                        roundInt(zoom * (middleY - yOffset)) );
            WrappedTree firstKid = wt.children[0];
            double middleFirstKid =  firstKid.x();
            WrappedTree lastKid = wt.children[wt.children.length - 1];
            double middleLastKid = lastKid.x();
            gc.drawLine(roundInt(zoom * (middleFirstKid - xOffset)), 
                        roundInt(zoom * (middleY - yOffset)), 
                        roundInt(zoom * (rootMiddle - xOffset)),
                        roundInt(zoom * (middleY - yOffset)));
            gc.drawLine(roundInt(zoom * (middleFirstKid - xOffset)), 
                        roundInt(zoom * (middleY - yOffset)), 
                        roundInt(zoom * (middleLastKid - xOffset)),
                        roundInt(zoom * (middleY - yOffset)));
            
            for (WrappedTree kid : wt.children) {
                double middleKid = kid.x();
                paintTree(kid, gc, r);
                gc.drawLine(roundInt(zoom * (middleKid - xOffset)), 
                            roundInt(zoom * (middleY - yOffset)), 
                            roundInt(zoom * (middleKid - xOffset)), 
                            roundInt(zoom * (kid.y() + vgap / 2.0 - yOffset)));
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
        paintTree(wt, e.gc, r);
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
            z_handler.execute(parent);
        } 
        else if (e.keyCode == 'a') {
            render();
        }
        else if (e.keyCode == 'p') {
            t.print();
            System.out.printf("\n");
        }
        else if (e.keyCode == 'j') {
            try {
                System.out.print(t.toJson() + "\n");
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
        }
        setScrollBars();
        redraw();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
