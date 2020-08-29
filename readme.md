# Quickstart

This is a tiny "quickstart" repository for Functional Africa training.

To get started you will need a terminal, an editor, Fury and `git`.

To install Fury on Linux or Mac OS, run:
```sh
curl -Ls fury.build | sh
```
or follow the more detailed [instructions](https://propensive.com/opensource/fury/install) on the Fury website.

Once Fury is installed, to start work on the project, run:
```sh
fury layer clone -l functionalafrica/quickstart
cd quickstart
fury repo checkout -r quickstart
```

The build can be run simply by running `fury` from the project directory.

If everything worked as expected, you should see some compilation output, and the words `Hello World`.

## Alternative

You can also run the "Hello World" application, without installing Fury, by cloning this repository and running,
```
./fury
```

## Last Resort Solution

If Fury is not working, try installing the Scala 3 compiler using the instructions on the
[Dotty website](https://dotty.epfl.ch/). It should be possible to run a compilation using,
```
mkdir -p bin
dotc -d bin src/*.scala
```
