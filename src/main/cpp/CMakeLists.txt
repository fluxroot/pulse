cmake_minimum_required(VERSION 3.10)
project(main)

add_library(core STATIC
        bitboard.cpp
        castling.cpp
        color.cpp
        evaluation.cpp
        notation.cpp
        file.cpp
        move.cpp
        movegenerator.cpp
        movelist.cpp
        perft.cpp
        piece.cpp
        piecetype.cpp
        position.cpp
        pulse.cpp
        rank.cpp
        search.cpp
        square.cpp
        value.cpp
        )

add_executable(pulse main.cpp)
set_target_properties(pulse PROPERTIES OUTPUT_NAME "pulse-cpp-${PLATFORM_SUFFIX}-${pulse_VERSION}")

target_link_libraries(pulse core Threads::Threads)

install(TARGETS pulse DESTINATION .)
