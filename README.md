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

* [c] Implement my unit test again, that takes canned input from JSON files, and compares
  the layout results with after-trees, also from JSON.
    * [c] Also test the result to make sure there are no overlaps.

* Get rid of addGap in the SWT test -- instead, subtract from width/height when drawing
  the boxes.




* [c] For each of the "after" trees in the test: view them in SWT, and visually check them.

* Make a couple more test cases: from the papers, and the trouble one from dtd-diagram


* My main program will have these features:
    * Input: 
        * Generate a random tree, with parameters specified, or
        * Get input from JSON
    * Output:
        * JSON
        * SWT
        * SVG


* Move the TreeElement overlap() method into a unit test, and use it there.

* See if the before-x, after-x that I get, now, are the same as what I got before.

* Change TestInterface such that it can display an already layed-out tree, that
  it gets from JSON.

* Compare my svg rendering with this.

* Get the SWT display to show the results of laying out src/test/resources/before-1.json
  from the master branch.


