cmake_minimum_required(VERSION 3.10)
project(pulse VERSION 2.0.0)

if (CMAKE_SYSTEM_NAME STREQUAL Windows)
    set(PLATFORM_SUFFIX windows)
elseif (CMAKE_SYSTEM_NAME STREQUAL Linux)
    set(PLATFORM_SUFFIX linux)
else ()
    message(FATAL_ERROR "Unknown platform ${CMAKE_SYSTEM_NAME}")
endif ()

set(CPACK_GENERATOR ZIP)
set(CPACK_PACKAGE_FILE_NAME "${CMAKE_PROJECT_NAME}-cpp-${PLATFORM_SUFFIX}-${pulse_VERSION}")
include(CPack)

enable_testing()

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

set(THREADS_PREFER_PTHREAD_FLAG ON)
find_package(Threads REQUIRED)

add_subdirectory(src/main/cpp)
add_subdirectory(src/test/cpp)

install(FILES README.md LICENSE CHANGES.md src/dist/logo.bmp DESTINATION .)
