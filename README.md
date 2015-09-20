Code accompanying the paper "Drawing Non-layered Tidy Trees in Linear Time"


# Compiling and running the Measure app

This worked for me:

```
javac -sourcepath src -d build/classes src/treelayout/measure/Measure.java
java -cp build/classes treelayout.measure.Measure
```


# SWT application

Download Eclipse SWT from 
[here](https://www.eclipse.org/swt/), copy it to the lib subdirectory,
and unzip it.

I wasn't able to get this to compile and run.  I was able to compile it with

```
javac -sourcepath src -cp lib/swt.jar -d build/classes \
  src/treelayout/swt/TestInterface.java 
```

But then, when I tried to run it with

```
java -cp build/classes treelayout.swt.TestInterface
```

I got the error:

```
Error: Could not find or load main class treelayout.swt.TestInterface
```
