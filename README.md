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
* Right click on this flextree project, and select Properties. Under "Java Build 
  Path", check "org.eclipse.swt". This includes the SWT library as a dependency.



# To do

* Implement my unit test again, that takes canned input from JSON files, and compares
  the layout results with after-trees, also from JSON.
    * Also test the result to make sure there are no overlaps.

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


