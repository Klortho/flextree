package org.klortho.flextree;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonIgnoreProperties({ "boundingBox", "minX", "depth", "parent" })
public final class Tree {
	// Note that not every Tree uses x_size and y_size. They are only used
	// when laying out the tree, and the layout engine is using the
	// defaultNodeSizeFunction
	public double x_size, y_size;
	
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
        this.x_size = 1.0;
        this.y_size = 1.0;
        this.children = new Vector<Tree>();
        this.parent = NULL;
        this.x = 0;
        this.y = 0;
    }

    public Tree(double x_size, double y_size, Tree ... children) {
		this.x_size = x_size;
		this.y_size = y_size;
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
		return new BoundingBox(this);
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

    public boolean deepEquals(Tree other, PrintStream ps) {
        if (//x_size != other.x_size ||
            //y_size != other.y_size ||
            x != other.x ||
            y != other.y ||
            children.size() != other.children.size()) //return false;
        {
        	ps.println("mismatch:\n" +
            		//"x_size: " + x_size + " <=> " + other.x_size + "\n" +
            		//"y_size: " + y_size + " <=> " + other.y_size + "\n" +
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

	/**
	 * Test to see if the tree has any overlaps among its nodes. Returns true if there
	 * is any overlap.
	 */
	public boolean hasOverlappingNodes() {
		// dummy PrintStream -- output will not be used.
		PrintStream ps = new PrintStream(new ByteArrayOutputStream());
		return hasOverlappingNodes(ps);
	}
	
	/**
	 * Same as hasOverlappingNodes(), but if an overlapping node is encountered, a message
	 * about it is written to the PrintStream.
	 */
	public boolean hasOverlappingNodes(PrintStream ps) {
		ArrayList<Tree> nodes = this.allNodes();
		for (int i = 0 ; i < nodes.size(); i++) {
			for (int j = 0 ; j < i ; j++) {
				if (nodeOverlaps(nodes.get(i), nodes.get(j))) {
					ps.printf("Overlap %d %d!!\n", i, j);
					return true;
				}
			}
		}
		return false;
	}

	private static boolean overlap(double xStart, double xEnd, 
                                   double xStart2, double xEnd2) 
	{
        return (xStart2 < xEnd && xEnd2 > xStart) ||
               (xStart < xEnd2 && xEnd > xStart2);
	}
	
	private static boolean nodeOverlaps(Tree a, Tree b) {
        return overlap(a.x, a.x + a.x_size, b.x , b.x + b.x_size) &&
               overlap(a.y, a.y + a.y_size, b.y, b.y + b.y_size);
	}


}
