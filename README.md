Code accompanying the paper [Drawing Non-layered Tidy Trees in Linear 
Time](http://oai.cwi.nl/oai/asset/21856/21856B.pdf).

This was adapted into the [D3 flextree plugin](https://github.com/Klortho/d3-flextree).

Thanks go to A.J. van der Ploeg, for making his code available on GitHub!




# Compiling and running

* Clone this repository, and also Klortho/d3-flextree.
* Create a softlink from this repo's src/test/resources/test-cases to
  d3-flextree's test/cases directory.
* Download and install Eclipse.
* Download the SWT binaries from [here](https://www.eclipse.org/swt/), 
  for whatever platform you are on.
* In Eclipse, selected File -> Import, then "Existing project into workspace", then
  selected "archive file", and selected the .zip file you just downloaded.
* Clone this repository, then, in Eclipse, File -> Import -> "Existing project into
  workspace", then select this repository's root directory. Make sure you de-select
  "Copy projects into workspace".

Then, you have the following executables and tests, all of which are under
src/test/java:

* RenderMain - renders a tree in SWT. See the comments in that file for command-line
  arguments
* PerformanceCheck
* UnitTests - JUnit tests
