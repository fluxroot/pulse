Release Notes - Pulse - Version 1.6.1
-------------------------------------

* [PULSE-72] - Switch to Mingw-w64 as main compiler for C++ Edition
* [PULSE-73] - Fix filenames for artifacts

Release Notes - Pulse - Version 1.6.0
-------------------------------------

* [PULSE-66] - Add Google Guava as Maven dependency
* [PULSE-67] - Use Google Guava Preconditions
* [PULSE-70] - Use Google Guava Collection Utilities
* [PULSE-56] - Change threading logic
* [PULSE-57] - Refactor Castling to use bits instead of array
* [PULSE-58] - Merge position pieces into one array
* [PULSE-59] - Generify MoveList
* [PULSE-60] - Use Hamcrest for Unit Testing
* [PULSE-62] - Add @NotNull/@Nullable annotations and remove assertions
* [PULSE-63] - Switch from Cobertura to JaCoCo
* [PULSE-64] - Statically Import Members where possible
* [PULSE-65] - Use Google Guava for non-critical code
* [PULSE-68] - Remove skeleton branch
* [PULSE-69] - Refactoring 1.6
* [PULSE-71] - Remove default target in switch statement where possible
* [PULSE-53] - Add Main class as starting point
* [PULSE-54] - Remove unused board constructor from Chess 960 id
* [PULSE-55] - Decouple protocol from internal models

Release Notes - Pulse - Version 1.5
-----------------------------------

* [PULSE-41] - Add skeleton files for Pulse C++ Edition
* [PULSE-42] - Add Pulse C++ Edition
* [PULSE-46] - Add perft as command line argument
* [PULSE-47] - Add Tempo
* [PULSE-45] - Refactor Java code to match C++ code base
* [PULSE-51] - Do move legality check in search
* [PULSE-43] - Remove VersionInfo for simplicity
* [PULSE-44] - Remove Configuration for simplicity
* [PULSE-48] - Remove tapered eval for simplicity
* [PULSE-49] - Remove castling evaluation for simplicity
* [PULSE-50] - Remove staged move generator
* [PULSE-52] - Remove check extensions

Release Notes - Pulse - Version 1.4.0
-------------------------------------

* [PULSE-31] - Change to MIT license
* [PULSE-34] - Using Maven as build tool
* [PULSE-30] - Change logo file format to bmp
* [PULSE-39] - Add check extension
* [PULSE-38] - Rewrite in check testing
* [PULSE-33] - Remove IntelliJ IDEA settings
* [PULSE-24] - Remove unused MASK
* [PULSE-28] - Remove unused methods and refactor tests
* [PULSE-19] - Remove testing and add integration source set
* [PULSE-25] - Split Castling into Castling and Type
* [PULSE-27] - Refactor Piece constants
* [PULSE-26] - Refactor NOTYPE to NOMOVETYPE
* [PULSE-32] - Make Zobrist a static utility class
* [PULSE-35] - Rewrite move conversion from JCPI
* [PULSE-29] - Replace assertions with exceptions
* [PULSE-22] - Do not throw exception in isValid()
* [PULSE-21] - Use long instead of int for number of nodes
* [PULSE-20] - Fix Color in setFullMoveNumber()
* [PULSE-23] - Fix insufficient material

Release Notes - Pulse - Version 1.3.0
-------------------------------------

* Refactor variables and methods to use common namings
* Cleanup code to improve readability
* Build release and debug binaries
* Extract PV from search
* Add more comments
* Add Int* model classes from JCPI to show their implementation
* Add mobility evaluation
* Add hasInsufficientMaterial()
* Add incremental material evaluation
* Add pseudo-legal move generator
* Remove repetitionTable and replace with stack search
* Improve performance in isAttacked()
* Fix best move update on abort
* Fix MAX_PLY and CHECKMATE_THRESHOLD bound usage
* Fix time management with fixed time search

Release Notes - Pulse - Version 1.2.0
-------------------------------------

* Add more comments
* Cleanup code to improve readability
* Remove save/restore move list in Iterative Deepening
* Abort search if depth is equal to mate distance
* Add support for UCI seldepth
* Merge get*Generators() to improve readability

Release Notes - Pulse - Version 1.1.0
-------------------------------------

* Add more comments
* Cleanup code to improve readability
* Add logo created by Silvian Ruxy Sylwyka
* Create skeleton and master branch

Release Notes - Pulse - Version 1.0.0
-------------------------------------

* Initial release
