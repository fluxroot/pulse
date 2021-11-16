cmake_minimum_required(VERSION 3.10)
project(test)

configure_file(CMakeLists.txt.in googletest-download/CMakeLists.txt)
execute_process(COMMAND ${CMAKE_COMMAND} -G "${CMAKE_GENERATOR}" .
        RESULT_VARIABLE result
        WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/googletest-download)
if (result)
    message(FATAL_ERROR "CMake step for googletest failed: ${result}")
endif ()
execute_process(COMMAND ${CMAKE_COMMAND} --build .
        RESULT_VARIABLE result
        WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/googletest-download)
if (result)
    message(FATAL_ERROR "Build step for googletest failed: ${result}")
endif ()

set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)

add_subdirectory(${CMAKE_CURRENT_BINARY_DIR}/googletest-src
        ${CMAKE_CURRENT_BINARY_DIR}/googletest-build
        EXCLUDE_FROM_ALL)

if (CMAKE_VERSION VERSION_LESS 2.8.11)
    include_directories("${gtest_SOURCE_DIR}/include")
endif ()

include_directories(${main_SOURCE_DIR})

add_executable(unittest
        bitboardtest.cpp
        model/castlingtest.cpp
        model/castlingtypetest.cpp
        model/colortest.cpp
        evaluationtest.cpp
        notationtest.cpp
        model/filetest.cpp
        movegeneratortest.cpp
        movelisttest.cpp
        model/movetest.cpp
        model/piecetest.cpp
        model/piecetypetest.cpp
        positiontest.cpp
        model/ranktest.cpp
        model/squaretest.cpp
        )

target_link_libraries(unittest core gtest_main)

add_test(unittest unittest --gtest_output=xml:test-results/)
