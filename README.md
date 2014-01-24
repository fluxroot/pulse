Pulse Chess
===========

Copyright 2013-2014 Flux Chess Project  
http://fluxchess.com

[![Build Status](https://travis-ci.org/fluxroot/pulse.png?branch=master)](https://travis-ci.org/fluxroot/pulse) [![Coverage Status](https://coveralls.io/repos/fluxroot/pulse/badge.png?branch=master)](https://coveralls.io/r/fluxroot/pulse?branch=master)


Introduction
------------
Pulse Chess is a simple chess engine with didactic intentions in mind. 
The source code should be easy to read, so that new developers can 
learn, how to build a chess engine. If you want to roll your own, just 
fork it and start coding! :) 

There are three branches available in the repository.

- *skeleton*  
The skeleton branch contains just the basic code to hook up with the 
Java Chess Protocol Interface. Start from here if you want to build your 
own chess engine. Create a all your classes and integrate them into the 
Search class. 


- *master*  
The master branch contains a very basic chess engine. It knows all the 
rules and plays chess, but lacks more sophisticated features. Start from 
the skeleton branch and try to beat the master branch. :) 


- *transpositiontable*  
The transpositiontable branch adds a very useful feature called 
Transposition Table. They could speed up your search if done right. 
However if implemented the wrong way they could also lead to wrong 
results. Look at this code if you have beaten the master branch. 


Features
--------
Only a couple of basic chess engine features are implemented to keep the 
source code clean and readable. Below is a list of the major building 
blocks. 

- *UCI compatible*  
Pulse Chess uses [JCPI] for implementing the UCI protocol. Basically all 
major features are supported including pondering. 

- *0x88 board representation*  
To keep things simple Pulse Chess uses a 0x88 board representation. In 
addition piece lists are kept in Bitboards. For generating the board 
hash Zobrist hashing is used. 

- *Only material evaluation*  
Currently only the material is used for calculating the evaluation 
function. However it should be quite easy to extend it with other
evaluation features. 

- *Using integers for type representation*  
Although Java is quite efficient and fast in memory management, it is
not fast enough for chess engines. Instead of using objects for 
important data structures, Pulse Chess uses integers instead. 

- *Legal move generator*  
To keep the source code clean and simple, a basic legal move generator 
is used, which generates all moves at once. 

- *Basic search*  
Pulse Chess uses a basic Alpha-beta pruning algorithm with iterative 
deepening. This allows us to use a very simple time management. In 
addition there's a basic Quiescent search to improve the game play. 


Build it
--------
Pulse Chess uses [Gradle] as build system. To build it from source, use 
the following steps. 

- get it  
`git clone https://github.com/fluxroot/pulse.git`

- build it  
`./gradlew clean dist`

- grab it  
`cp build/distributions/pulse-<version>.zip <installation directory>`


Acknowledgments
---------------
The Pulse Chess logo was created by Ruxy Sylwyka. Thanks a lot!


License
-------
Pulse Chess is released under version 3 of the [LGPL].


[JCPI]: https://github.com/fluxroot/jcpi
[Gradle]: http://gradle.org/
[LGPL]: http://www.gnu.org/copyleft/lgpl.html
