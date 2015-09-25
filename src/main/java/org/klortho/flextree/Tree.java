package org.klortho.flextree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonIgnoreProperties({ "boundingBox", "minX", "depth", "parent" })
public final class Tree {
	public double width, height;
	public Vector<Tree> children;
	
	// Set by the layout engine:
	public Tree parent;
	public int depth;
	public double x, y;

    public static ObjectMapper json_mapper;
    static {
        json_mapper = new ObjectMapper();
        json_mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Sentinel value used for the parent of the root
    public static Tree NULL = new Tree();
    
    /**
     * Default constructor is needed by Jackson, to enable reading in
     * objects from JSON.
     */
    public Tree() {
        this.width = 1.0;
        this.height = 1.0;
        this.children = new Vector<Tree>();
        this.parent = NULL;
        this.x = 0;
        this.y = 0;
    }

    public Tree(double width, double height, Tree ... children) {
		this.width = width;
		this.height = height;
		this.children = new Vector<Tree>();
		this.children.addAll(Arrays.asList(children));
        this.parent = NULL;
	}

    /**
     *  Create a tree from a JSON file
     */
    public static Tree fromJson(File json) 
      throws IOException
    {
        return json_mapper.readValue(json, Tree.class);
    }

    /**
     * Serialize to JSON
     */
    public String toJson() 
      throws JsonProcessingException
    {
        return json_mapper.writeValueAsString(this);
    }


	public BoundingBox getBoundingBox() {
		BoundingBox result = new BoundingBox(0, 0);
		getBoundingBox(this, result);
		return result;
	}
	
	private static void getBoundingBox(Tree tree, BoundingBox b) {
		b.width = Math.max(b.width, tree.x + tree.width);
		b.height = Math.max(b.height, tree.y + tree.height);
		for (Tree child : tree.children) {
			getBoundingBox(child, b);
		}
	}
	
	/**
	 * Get the total number of nodes in this tree.
	 */
	public int size() {
		int res = 1;
		for (Tree node : children) {
			res += node.size();
		}
		return res;
	}
	
	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	public ArrayList<Tree> allNodes() {
		ArrayList<Tree> nodes = new ArrayList<Tree>();
		nodes.add(this);
		for (Tree kid : children) {
			nodes.addAll(kid.allNodes());
		}
		return nodes;
	}
	
	public int getDepth() {
		int res = 1;
		for (Tree child : children) {
			res = Math.max(res, child.getDepth() + 1);
		}
		return res;
	}
	
	/**
	 * Print out the Tree in Java format.
	 */
	public void print() {
		print(0);
	}

	private void print(int indent) {
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
