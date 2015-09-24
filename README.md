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

* Move the layout stuff out of the TreeSWT class -- this should only render 
  already layed-out trees.



* See if the before-x, after-x that I get, now, are the same as what I got before.

* Change TestInterface such that it can display an already layed-out tree, that
  it gets from JSON.

* Compare my svg rendering with this.

* Get the SWT display to show the results of laying out src/test/resources/before-1.json
  from the master branch.


