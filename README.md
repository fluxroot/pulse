Pulse Chess
===========

Copyright (C) 2013-2014 Phokham Nonava  
http://fluxchess.com

[![Build Status](https://travis-ci.org/fluxroot/pulse.svg?branch=master)](https://travis-ci.org/fluxroot/pulse) [![Coverage Status](https://img.shields.io/coveralls/fluxroot/pulse.svg)](https://coveralls.io/r/fluxroot/pulse?branch=master)


Introduction
------------
Pulse Chess is a simple chess engine with didactic intentions in mind. 
The source code should be easy to read, so that new developers can 
learn, how to build a chess engine. If you want to roll your own, just 
fork it and start coding! :) 

Pulse Chess is available in Java and C++. Both editions have the same 
feature set. The Java Edition requires Java 7 for compilation and 
execution. The C++ Edition is written in C++11. It has been compiled 
successfully using g++ 4.9.1 and Visual C++ 2013. 

There are two branches available in the repository.

- **skeleton**  
The skeleton branch contains just the basic code to hook up with the 
Java Chess Protocol Interface (*Java Edition*). Start from here if you 
want to build your own chess engine. 

- **master**  
The master branch contains a very basic chess engine. It knows all the 
rules and plays chess, but lacks more sophisticated features. Start from 
the skeleton branch and try to beat the master branch. :) 


Features
--------
Only a couple of basic chess engine features are implemented to keep the 
source code clean and readable. Below is a list of the major building 
blocks. 

- **UCI compatible**  
*Java Edition*: Pulse Chess uses [JCPI] for implementing the UCI 
protocol. 

- **0x88 board representation**  
To keep things simple Pulse Chess uses a 0x88 board representation. In
addition piece lists are kept in Bitboards.

- **Only material and mobility evaluation**  
Currently only material and mobility (to add some variation) are used 
for calculating the evaluation function. However it should be quite easy 
to extend it with other evaluation features. 

- **Using integers for type representation**  
*Java Edition*: Although Java is quite efficient and fast in memory 
management, it is not fast enough for chess engines. Instead of using 
objects for important data structures, Pulse Chess uses integers to 
exploit the Java stack. 

- **Pseudo-legal move generator**  
To keep the source code clean and simple, a pseudo-legal move generator 
is used. This has the advantage to skip writing a complicated legal move 
checking method. 

- **Basic search**  
Pulse Chess uses a basic Alpha-beta pruning algorithm with iterative 
deepening. This allows us to use a very simple time management. In 
addition there's a basic Quiescent search to improve the game play. 


Build it
--------
Pulse Chess uses [Maven] for the Java Edition and [CMake] for the C++ 
Edition as build systems. To build it from source, use the following 
steps. 

- get it  
    `git clone https://github.com/fluxroot/pulse.git`

### Java Edition

- build it  
    `mvn package`

- grab it  
    `cp target/pulse-<version>.zip <installation directory>`

### C++ Edition

- build it  

        mkdir build
        cd build
        cmake -DCMAKE_BUILD_TYPE=Release .. && make && make test && make package

    For MSYS environments try specifying the generator:  
    `cmake -DCMAKE_BUILD_TYPE=Release -G "MSYS Makefiles" .. && make && make test && make package`

    For Visual Studio do the following:  
    `cmake -G "Visual Studio 12 2013" .. && cmake --build . --config Release && ctest && cpack -C Release`

- grab it  
    `cp build/pulse-<version>.zip <installation directory>`


Acknowledgments
---------------
The Pulse Chess logo was created by Silvian Sylwyka. Thanks a lot!


License
-------
Pulse Chess is released under the MIT License.


[JCPI]: https://github.com/fluxroot/jcpi
[Maven]: http://maven.apache.org/
[CMake]: http://cmake.org/
