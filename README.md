Code accompanying the paper "Drawing Non-layered Tidy Trees in Linear Time"

# Compiling and running

* Download and install Eclipse.
* Download the SWT binaries from http://www.eclipse.org/downloads -> select
  Eclipse release 4.5, then SWT binary and source, for whatever platform you are on.
* In Eclipse, selected File -> Import, then "Existing project into workspace", then
  selected "archive file", and selected the .zip file you just downloaded.




# To do

* Implement my unit test again, that takes canned input from JSON files, and compares
  the layout results with after-trees, also from JSON.

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


