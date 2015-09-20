Code accompanying the paper "Drawing Non-layered Tidy Trees in Linear Time"


# Compiling and running

```
mvn exec:java
```


```
mvn package
java -cp target/flextree-1.0-SNAPSHOT.jar org.klortho.flextree.Tester
```



# To do

* Figure out how Measure works
    * Creates a GenerateTrees object
    * The GenerateTrees object generates random trees
    * I think that Marshall does the wrapping, and calls the layout algorithm.

* Generate a unit test that checks the five test cases
    * Need to be able to read a tree from json

* To do to the Java code, before porting
    * [c] Get rid of hgap, vgap, addGap, etc.
    * The y coordinate should be computed as part of the layout
    * Change the name of the Paper class to Layout
    * Merge Marshall -> Paper (Layout)
    * Change Marshall's convert method into "wrap", inside Paper (Layout). It should
      be called by the layout() method.

# Porting to JavaScript



