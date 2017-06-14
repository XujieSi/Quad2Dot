# Quad2Dot
Display Java Quad IR in the dot graph format.
This is a simple excercise of playing with Quad IR.

## Quad
Quad is a Java [Intermediate Representation](https://en.wikipedia.org/wiki/Intermediate_representation) (IR) used in [Joeq](https://suif.stanford.edu/~courses/cs243/joeq/) compiler system developed by the [SUIF Group](https://suif.stanford.edu/) from Stanford University.


## Setup
Unfortunately, Java 6 is highly recommended, as Quad is a fairly out-of-dated IR and many new features in Java 7/8 are not supported.
You may still give a try with Java 7/8.

Compile: `ant`

Run test: `ant run`

Generated dot graphs will be in the directory: `$(Quad2Dot)/dot_out/`
