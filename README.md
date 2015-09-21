Code accompanying the paper "Drawing Non-layered Tidy Trees in Linear Time"


# Compiling and running

Here are some things you can do:

Run the tests:

```
mvn clean test
```

Run the main Flextree program, that looks for a JSON file called before.json,
lays out the nodes, and writes the results to after.json:

```
mvn clean compile exec:java
```

Produces an executable Jar, that includes all dependencies:

```
mvn clean compile assembly:single
```

You can execute the Jar with

```
java -jar target/flextree-jar-with-dependencies.jar
```



# To do

* Create a D3 visualization page that lets me see the layed out trees
* Create a test case for the one that failed in dtd-diagram.
* Port to JavaScript
* Then port to D3
