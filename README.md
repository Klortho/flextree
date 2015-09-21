Code accompanying the paper "Drawing Non-layered Tidy Trees in Linear Time"


# Compiling and running

```
mvn compile
mvn exec:java
mvn test
```




# To do




* To do to the Java code, before porting
    * [c] Change the name of the Paper class to LayoutEngine
    * [c] Merge Marshall -> Paper (LayoutEngine)
    * [c] Change Marshall's convert method into "wrap", inside Paper (Layout). It should
      be called by the layout() method.
    * [c] Change the name of Tree -> WrappedTree
    * [c] Change TreeNode -> Tree
    * The y coordinate should be computed as part of the layout

# Porting to JavaScript



