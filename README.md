Code accompanying the paper "Drawing Non-layered Tidy Trees in Linear Time"

# Compiling and running

* Download and install Eclipse.
* Download the SWT binaries from [here](https://www.eclipse.org/swt/), 
  for whatever platform you are on.
* In Eclipse, selected File -> Import, then "Existing project into workspace", then
  selected "archive file", and selected the .zip file you just downloaded.
* Clone this repository, then, in Eclipse, File -> Import -> "Existing project into
  workspace", then select this repository's root directory. Make sure you de-select
  "Copy projects into workspace".
* Then, you should be able to right-click on Flextree.java (under src/main/java),
  and select Run as -> Java application.


# To do

* Make the interface more D3-like:
    * Implement a nodeSize first. Set it either with [x, y] (fixed size)
      or a function. The default function should be to get the size from
      the width and height of the tree nodes.
        * Can I change width -> x_size, and height -> y_size?

    * [c] Set 'parent' on every node. What to use for "null" for the root?
    * [c] Also set 'depth'

    * The sizing can be specified in one of three ways:

          size    nodeSize   =>  variable_node_size  scale
          [x,y]   null       =>  false               [1,1]
          null    [x,y]      =>  false               [x,y]
          null    function   =>  true                nodeSize(root_)

    * Implement (and test) a separation function and spacing


