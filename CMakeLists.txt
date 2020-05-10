cmake_minimum_required(VERSION 3.10)
project(pulse)

set(pulse_VERSION_MAJOR 2)
set(pulse_VERSION_MINOR 0)
set(pulse_VERSION_PATCH 0)
set(CPACK_PACKAGE_VERSION_MAJOR 2)
set(CPACK_PACKAGE_VERSION_MINOR 0)
set(CPACK_PACKAGE_VERSION_PATCH 0)
set(pulse_VERSION "${pulse_VERSION_MAJOR}.${pulse_VERSION_MINOR}.${pulse_VERSION_PATCH}")

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

if (MSVC)
    set(LIBRARY_PREFIX "")
    set(LIBRARY_SUFFIX .lib)
else ()
    set(LIBRARY_PREFIX lib)
    set(LIBRARY_SUFFIX .a)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread")
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -pthread")
endif ()

add_subdirectory(src/main/cpp)
add_subdirectory(src/test/cpp)

install(FILES README.md LICENSE CHANGES.md src/dist/logo.bmp DESTINATION .)
