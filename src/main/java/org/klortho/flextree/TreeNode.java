package org.klortho.flextree;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonIgnoreProperties({ "boundingBox", "minX", "depth" })
public final class TreeNode {
    public double width, height;
    public double x, y;
    public Vector<TreeNode> children;

    public static ObjectMapper json_mapper;
    static {
        json_mapper = new ObjectMapper();
        json_mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public TreeNode() {
        this.width = 1.0;
        this.height = 1.0;
        this.children = new Vector<TreeNode>();
        this.x = 0;
        this.y = 0;
    }
    
    public TreeNode(double width, double height, TreeNode ... children) {
        this.width = width;
        this.height = height;
        this.children = new Vector<TreeNode>();
        this.children.addAll(Arrays.asList(children));
    }

    // Create a tree from a JSON file
    public static TreeNode fromJson(File json) 
      throws IOException
    {
        return json_mapper.readValue(json, TreeNode.class);
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


    public String toJson() 
      throws JsonProcessingException
    {
        return json_mapper.writeValueAsString(this);
    }

    public boolean deepEquals(TreeNode other) {
        if (width != other.width ||
            height != other.height ||
            x != other.x ||
            y != other.y ||
            children.size() != other.children.size()) return false;

        for (int i = 0; i < children.size(); ++i) {
            if (!children.get(i).deepEquals(other.children.get(i))) return false;
        }

        return true;
    }
}
