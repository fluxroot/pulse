Pulse Chess
===========

Copyright 2013-2024 Phokham Nonava
https://www.fluxchess.com


Introduction
------------

Pulse Chess is a simple and accessible chess engine designed to help developers learn about building their own chess
engines. It's a great starting point for those who want to understand the fundamentals of chess engine programming.

### Key Features

- **UCI Compatible**: Supports the Universal Chess Interface (UCI) protocol, making it easy to integrate with various
  chess GUIs.
- **0x88 Board Representation**: Utilizes the 0x88 board representation for efficient move generation and board
  management.
- **Material and Mobility Evaluation**: Implements basic evaluation functions focusing on material and mobility,
  providing a clear example of how to assess positions.
- **Pseudo-Legal Move Generator**: Generates pseudo-legal moves, offering insight into the process of move generation
  and validation.
- **Basic Search with Alpha-Beta Pruning and Quiescent Search**: Incorporates a basic search algorithm enhanced with
  alpha-beta pruning and quiescent search to improve decision-making and performance.

Pulse Chess is ideal for novice programmers and experienced developers alike, offering a hands-on approach to
understanding chess engines.

Implementations
---------------

Pulse Chess is currently available in Java and C++. However, I am working on additional engines in different languages
that all have the same feature set.

| Language      | Features    | Platforms             | Requirements                |
|---------------|-------------|-----------------------|-----------------------------|
| Java          | Complete    | Linux, Windows, macOS | Java 17                     |
| C++           | Complete    | Linux, Windows        | C++17                       |
| Kotlin/Native | In Progress | Linux, Windows, macOS | Kotlin Multiplatform 2.0.21 |
| Go            | In Progress | Linux, Windows        | Go 1.23.4                   |

Build them
----------

First clone the repository using `git clone https://github.com/fluxroot/pulse.git`.

### Java

```shell
./gradlew :pulse-java:build
ls pulse-java/build/distributions/pulse-java-*.zip
```

### C++

```shell
# Linux
cmake -B build -DCMAKE_BUILD_TYPE=Release
cmake --build build --config Release --target package
(cd build; ctest -C Release)
ls build/pulse-cpp-linux-*.tar.gz
```

```shell
# Windows
cmake -B build
cmake --build build --config Release --target package
cd build; ctest -C Release; cd ..
ls build/pulse-cpp-windows-*.zip
```

### Kotlin/Native

```shell
# Linux
./gradlew :pulse-kotlin:linuxDistTar
ls pulse-kotlin/build/distributions/pulse-kotlin-linux-*.tar.gz
```

```shell
# Windows
./gradlew :pulse-kotlin:windowsDistZip
ls pulse-kotlin/build/distributions/pulse-kotlin-windows-*.zip
```

```shell
# macOS
./gradlew :pulse-kotlin:macosDistTar
ls pulse-kotlin/build/distributions/pulse-kotlin-macos-*.tar.gz
```

### Go

```shell
# Linux
cd pulse-go
go build -v ./...
# No distribution archive yet
```

```shell
# Windows
cd pulse-go
go build -v ./...
# No distribution archive yet
```

Acknowledgments
---------------

The Pulse Chess logo was created by Silvian Sylwyka. Thanks a lot!

License
-------

Pulse Chess is released under the MIT License.
