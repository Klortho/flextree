package org.klortho.flextree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonIgnoreProperties({ "boundingBox", "minX", "depth", "hgap", "vgap" })
public final class Tree {
	// input
	public double width, height;
	public Vector<Tree> children;
	public double hgap, vgap;
	// output
	public double x, y;

    public static ObjectMapper json_mapper;
    static {
        json_mapper = new ObjectMapper();
        json_mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Default constructor is needed by Jackson, to enable reading in
     * objects from JSON.
     */
    public Tree() {
        this.width = 1.0;
        this.height = 1.0;
        this.children = new Vector<Tree>();
        this.x = 0;
        this.y = 0;
    }

    public Tree(double width, double height, Tree ... children) {
		this.width = width;
		this.height = height;
		this.children = new Vector<Tree>();
		this.children.addAll(Arrays.asList(children));
	}

    // Create a tree from a JSON file
    public static Tree fromJson(File json) 
      throws IOException
    {
        return json_mapper.readValue(json, Tree.class);
    }

    public String toJson() 
      throws JsonProcessingException
    {
        return json_mapper.writeValueAsString(this);
    }


	public BoundingBox getBoundingBox(){
		BoundingBox result = new BoundingBox(0, 0);
		getBoundingBox(this,result);
		return result;
	}
	
	private static void getBoundingBox(Tree tree,BoundingBox b) {
		b.width = Math.max(b.width,tree.x + tree.width);
		b.height = Math.max(b.height,tree.y + tree.height);
		for(Tree child : tree.children){
			getBoundingBox(child, b);
		}
	}
	
	public void moveRight(double move){
		x += move;
		for(Tree child : children){
			child.moveRight(move);
		}
	}
	
	public void normalizeX(){
		double minX = getMinX();
		moveRight(-minX);
	}
	
	public double getMinX(){
		double res = x;
		for(Tree child : children){
			res = Math.min(child.getMinX(),res);
		}
		return res;
	}
	
	public int size(){
		int res = 1;
		for(Tree node : children){
			res += node.size();
		}
		return res;
	}
	
	public boolean hasChildren(){
		return children.size() > 0;
	}
	
	public void allNodes(ArrayList<Tree> nodes) {
		nodes.add(this);
		for(Tree node : children){
			node.allNodes(nodes);
		}
	}
	
	public int getDepth() {
		int res = 1;
		for (Tree child : children) {
			res = Math.max(res, child.getDepth() + 1);
		}
		return res;
	}
	
	public void addGap(double hgap, double vgap) {
		this.hgap += hgap;
		this.vgap += vgap;
		this.width += 2 * hgap;
		this.height += 2 * vgap;
		for (Tree child : children) {
			child.addGap(hgap, vgap);
		}
	}
	
	public void addSize(double hsize, double vsize) {
		this.width+=hsize;
		this.height+=vsize;
		for(Tree child : children){
			child.addSize(hsize, vsize);
		}
	}
	
	public void addGapPerDepth(int gapPerDepth, int depth,int maxDepth){
		this.hgap += (maxDepth-depth)*gapPerDepth;
		this.width+=2* (maxDepth-depth)*gapPerDepth;
		for(Tree child : children){
			child.addGapPerDepth(gapPerDepth,depth+1,maxDepth);
		}
	}
	
	public void print() {
		print(0);
	}

	private void print(int indent) {
		//System.out.print("[" + indent + "]");
		String istr = "";
		for (int i = 0; i < indent; ++i) istr += "  ";
		System.out.printf(istr + "new Tree(%f, %f", width, height);
		for (Tree child : children) {
			System.out.printf(",\n");
			child.print(indent + 1);
		}
		if (children.size() > 0) System.out.print("\n" + istr);
		System.out.print(")");
	}

	public void mul(double w, double h){
		width *= w;
		height *= h;
		for(Tree child : children){
			child.mul(w, h);
		}
	}
	
	// FIXME: these two methods set the y-coordinate. They should be done automatically
	// as part of the layout.
	public void layer() {
		layer(0);
	}
	
	public void layer(double d){
		y = d;
		d+=height;
		for(Tree child : children){
			child.layer(d);
		}
	}
	
	public void randExpand(Tree t, Random r) {
		t.y += height;
		int i = r.nextInt(children.size() + 1);
		if (i == children.size()){
			addKid(t);
		} 
		else {
			children.get(i).randExpand( t, r);
		}
	}
	
	public void addKid(Tree t){
		children.add(t);
	}

	/**
	 * Compare two trees in terms of the size and positions of their nodes
	 */
    public boolean deepEquals(Tree other) {
        if (width != other.width ||
            height != other.height ||
            x != other.x ||
            y != other.y ||
            children.size() != other.children.size()) //return false;
        {
        	System.out.println("mismatch:\n" +
            		"width: " + width + " <=> " + other.width + "\n" +
            		"height: " + height + " <=> " + other.height + "\n" +
            		"x: " + x + " <=> " + other.x + "\n" +
            		"y: " + y + " <=> " + other.y + "\n"
            );
        	return false;
        }

        for (int i = 0; i < children.size(); ++i) {
            if (!children.get(i).deepEquals(other.children.get(i))) return false;
        }

        return true;
    }
}