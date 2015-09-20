package org.klortho.flextree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;


public final class TreeNode {

    // input
    public double width, height;
    public Vector<TreeNode> children;

    // output
    public double x, y;
    
    public TreeNode(double width, double height, TreeNode ... children) {
        this.width = width;
        this.height = height;
        this.children = new Vector<TreeNode>();
        this.children.addAll(Arrays.asList(children));
    }
    
    public BoundingBox getBoundingBox(){
        BoundingBox result = new BoundingBox(0, 0);
        getBoundingBox(this,result);
        return result;
    }
    
    private static void getBoundingBox(TreeNode tree,BoundingBox b) {
        b.width = Math.max(b.width,tree.x + tree.width);
        b.height = Math.max(b.height,tree.y + tree.height);
        for(TreeNode child : tree.children){
            getBoundingBox(child, b);
        }
    }
    
    public void moveRight(double move){
        x += move;
        for(TreeNode child : children){
            child.moveRight(move);
        }
    }
    
    public void normalizeX(){
        double minX = getMinX();
        moveRight(-minX);
    }
    
    public double getMinX(){
        double res = x;
        for(TreeNode child : children){
            res = Math.min(child.getMinX(),res);
        }
        return res;
    }
    
    public int size(){
        int res = 1;
        for(TreeNode node : children){
            res += node.size();
        }
        return res;
    }
    
    public boolean hasChildren(){
        return children.size() > 0;
    }
    
    final static double tolerance = 0.0;
    
    private boolean overlap(double xStart, double xEnd, double xStart2, double xEnd2){
        return ( xStart2 + tolerance < xEnd - tolerance  && 
                 xEnd2 - tolerance > xStart + tolerance ) ||
               ( xStart + tolerance < xEnd2 - tolerance && 
                 xEnd - tolerance > xStart2 + tolerance );
    }

    public boolean overlapsWith(TreeNode other) {
        return overlap(x, x + width, other.x , other.x + other.width)
                && overlap(y, y + height, other.y, other.y + other.height);
        
    }
    
    public void allNodes(ArrayList<TreeNode> nodes) {
        nodes.add(this);
        for (TreeNode node : children) {
            node.allNodes(nodes);
        }
    }
    
    public int getDepth() {
        int res = 1;
        for (TreeNode child : children){
            res = Math.max(res, child.getDepth() + 1);
        }
        return res;
    }
    
    public void addSize(double hsize,double vsize){
        this.width+=hsize;
        this.height+=vsize;
        for(TreeNode child : children){
            child.addSize(hsize,vsize);
        }
    }
    
    public void layer() {
        layer(0);
    }
    
    public void layer(double d) {
        y = d;
        d += height;
        for (TreeNode child : children) {
            child.layer(d);
        }
    }
    
    public void randExpand(TreeNode t, Random r) {
        t.y += height;
        int i = r.nextInt(children.size() + 1);
        if (i == children.size()) {
            addKid(t);
        } 
        else {
            children.get(i).randExpand(t, r);
        }
    }
    
    public void addKid(TreeNode t){
        children.add(t);
    }


    public void print() {
        System.out.printf("new TreeNode(%f, %f %f, %f ", x, y, width, height);
        for (TreeNode child : children) {
            System.out.printf(", ");
            child.print();
        }
        System.out.printf(")");        
    }
    
    public void printJson(PrintStream out, int indent) {
        String indentString = "";
        for (int i = 0; i < indent; ++i) {
            indentString += "  ";
        }
        out.print(
            indentString + "{\n" +
            indentString + "  \"width\": " + width + ",\n" +
            indentString + "  \"height\": " + height + ",\n" +
            indentString + "  \"x\": " + x + ",\n" +
            indentString + "  \"y\": " + y);
        if (children.size() > 0) {
            out.print(",\n" + 
                indentString + "  \"children\": [\n");
            for (int i = 0; i < children.size(); ++i) {
                children.get(i).printJson(out, indent + 2);
                if (i != children.size() - 1) {
                    out.print(",");
                }
                out.print("\n");
            }
            out.print(indentString + "  ]\n");
        }
        else {
            out.print("\n");
        }
        out.print(indentString + "}");
    }
}
