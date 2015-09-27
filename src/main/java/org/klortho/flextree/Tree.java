package org.klortho.flextree;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonIgnoreProperties({ "boundingBox", "minX", "depth", "parent" })
@JsonPropertyOrder({ "x", "y", "x_size", "y_size", "children" })
public final class Tree {
	
	public Vector<Tree> children;
	
	// Set by the layout engine:
	public Tree parent;
	public int depth;
	public double x, y;

	// Note that not every Tree uses x_size and y_size. They are only used
	// when laying out the tree, and the layout engine is using the
	// defaultNodeSizeFunction.  If you set setNodeSizes to true on the layout 
	// engine, then it will set these values to those that it used during the 
	// layout.
	public double x_size, y_size;

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
        this.x_size = 1.0;
        this.y_size = 1.0;
        this.children = new Vector<Tree>();
        this.parent = null;
        this.x = 0;
        this.y = 0;
    }

    /**
     * This constructor is used for testing, as it provides a nice clean way of creating
     * the hierarchical test tree.
     */
    public Tree(double x_size, double y_size, Tree ... children) {
		this.x_size = x_size;
		this.y_size = y_size;
		this.children = new Vector<Tree>();
		this.children.addAll(Arrays.asList(children));
        this.parent = null;
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


	/**
	 * Get the total number of nodes in this tree.
	 */
	public int size() {
		int n = 1;
		for (Tree node : children) {
			n += node.size();
		}
		return n;
	}
	
	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	/**
	 * Get a list of all of the nodes in the tree, in depth-first order.
	 */
	public ArrayList<Tree> allNodes() {
		ArrayList<Tree> nodes = new ArrayList<Tree>();
		nodes.add(this);
		for (Tree kid : children) {
			nodes.addAll(kid.allNodes());
		}
		return nodes;
	}
	
	public int getDepth() {
		int d = 1;
		for (Tree child : children) {
			d = Math.max(d, child.getDepth() + 1);
		}
		return d;
	}
	
	/**
	 * Print out the Tree in Java format (result can be pasted into Java code
	 * as a constructor).
	 */
	public void print() {
		print(0);
	}

	private void print(int indent) {
		String istr = "";
		for (int i = 0; i < indent; ++i) istr += "  ";
		System.out.printf(istr + "new Tree(%f, %f", x_size, y_size);
		for (Tree child : children) {
			System.out.printf(",\n");
			child.print(indent + 1);
		}
		if (children.size() > 0) System.out.print("\n" + istr);
		System.out.print(")");
	}

	/**
	 * Compare two already-layed-out trees in terms of the positions of 
	 * their nodes. Note that it doesn't compare sizes of the nodes, since not
	 * every Tree uses x_size and y_size. 
	 * Returns true if they are equal, false if not.
	 */
    public boolean deepEquals(Tree other) {
		// dummy PrintStream -- output will not be used.
		PrintStream ps = new PrintStream(new ByteArrayOutputStream());
		return deepEquals(other, ps);
    }

    /**
     * Same thing, but if there's a mismatch, then information about it will be written
     * to the PrintStream.
     */
    public boolean deepEquals(Tree other, PrintStream ps) {
        if (x != other.x ||
            y != other.y ||
            children.size() != other.children.size()) //return false;
        {
        	ps.println("mismatch:\n" +
            		"x: " + x + " <=> " + other.x + "\n" +
            		"y: " + y + " <=> " + other.y + "\n" +
            		"number of children: " + children.size() + " <=> " + other.children.size() + "\n"
            );
        	return false;
        }

        for (int i = 0; i < children.size(); ++i) {
            if (!children.get(i).deepEquals(other.children.get(i), ps)) return false;
        }

        return true;
    }

}
