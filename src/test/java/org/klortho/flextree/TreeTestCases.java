package org.klortho.flextree;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * This class reads in a bunch of data about test cases from the test-cases/tests.json
 * file, and provides methods to access the data.
 */
public class TreeTestCases {
    
    public List<TreeTestCase> cases;
    
    public static final String testCaseDir = "test-cases";
    public static ClassLoader classLoader;
    public static ObjectMapper json_mapper;
    static {
        json_mapper = new ObjectMapper();
        json_mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /*
     * The bulk of our tests will be read from the tests.json file. 
     * This inner class holds
     * the data from one of the objects in the list from that file.
     */
    @JsonIgnoreProperties({ "treeData", "expectedName", "expected" })
    public static class TreeTestCase {
        public String name;
        public String description;
        public String tree;
        public String sizing;
        public String gap;
        
        public TreeTestCase() {};
        
        public Tree getTreeData() throws IOException {
            return Tree.fromJson(getFile(testCaseDir + "/" + tree));
        }
        public String getExpectedName() {
            return name + ".expected.json";
        }
        public Tree getExpected() throws IOException {
            return Tree.fromJson(
                getFile(testCaseDir + "/" + getExpectedName()));
        }
    }

    public TreeTestCases() 
      throws JsonParseException, JsonMappingException, IOException 
    {
        classLoader = getClass().getClassLoader();
        cases = json_mapper.readValue(
                getFile(testCaseDir + "/tests.json"),
                new TypeReference<List<TreeTestCase>>() { } );
        //System.out.println(json_mapper.writeValueAsString(cases));
    }

    public TreeTestCase getTestCase(String name) {
        for (TreeTestCase tc : cases) {
            if (tc.name.equals(name)) return tc;
        }
        return null;
    }
    public static File getFile(String name) {
        return new File(classLoader.getResource(name).getFile());
    }
}
