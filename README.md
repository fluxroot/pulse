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

There are two branches available in the repository.

- *skeleton*  
The skeleton branch contains just the basic code to hook up with the 
Java Chess Protocol Interface. Start from here if you want to build your 
own chess engine. Create all your classes and integrate them into the 
Search class. 

- *master*  
The master branch contains a very basic chess engine. It knows all the 
rules and plays chess, but lacks more sophisticated features. Start from 
the skeleton branch and try to beat the master branch. :) 


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

- *Only material and mobility evaluation*  
Currently only material and mobility (to add some variation) are used 
for calculating the evaluation function. However it should be quite easy 
to extend it with other evaluation features. 

- *Using integers for type representation*  
Although Java is quite efficient and fast in memory management, it is
not fast enough for chess engines. Instead of using objects for 
important data structures, Pulse Chess uses integers instead. 

- *Staged, pseudo-legal move generator*  
To keep the source code clean and simple, a staged, pseudo-legal move 
generator is used. This has the advantage to skip writing a complicated 
legal move checking method. 

- *Basic search*  
Pulse Chess uses a basic Alpha-beta pruning algorithm with iterative 
deepening. This allows us to use a very simple time management. In 
addition there's a basic Quiescent search to improve the game play. 


Build it
--------
Pulse Chess uses [Maven] as build system. To build it from source, use
the following steps. 

- get it  
`git clone https://github.com/fluxroot/pulse.git`

- build it  
`mvn clean package`

- grab it  
`cp target/pulse-<version>.zip <installation directory>`


Acknowledgments
---------------
The Pulse Chess logo was created by Silvian Sylwyka. Thanks a lot!


License
-------
Pulse Chess is released under the MIT License.


[JCPI]: https://github.com/fluxroot/jcpi
[Maven]: http://maven.apache.org/
