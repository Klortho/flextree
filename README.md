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


* Finish working on the RenderSWT/TreeSWT interface:
    * All the API should be moved to RenderSWT, including what's needed inside
      z-handler.
    * Need also to define hgap, vgap, and zoom there.
    * hgap, vgap, and zoom should, by default, be determined dynamically, if
      it is easy.



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


