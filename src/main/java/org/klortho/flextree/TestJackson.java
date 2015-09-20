package org.klortho.flextree;

import java.io.File;
import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJackson {
    
    public static void main(String[] argv) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TreeNode tree = mapper.readValue(new File("tree.json"), TreeNode.class);
            System.out.println("Read tree.json; results are:\n");
            tree.printJson(System.out);
        }
        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
