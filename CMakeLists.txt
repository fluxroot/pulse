cmake_minimum_required(VERSION 2.8)
project(pulse)

set(pulse_VERSION_MAJOR 1)
set(pulse_VERSION_MINOR 6)
set(pulse_VERSION_PATCH 1)
set(CPACK_PACKAGE_VERSION_MAJOR 1)
set(CPACK_PACKAGE_VERSION_MINOR 6)
set(CPACK_PACKAGE_VERSION_PATCH 1)
set(pulse_VERSION "${pulse_VERSION_MAJOR}.${pulse_VERSION_MINOR}.${pulse_VERSION_PATCH}")

set(CPACK_GENERATOR ZIP)
set(CPACK_PACKAGE_FILE_NAME "${CMAKE_PROJECT_NAME}-${pulse_VERSION}-cpp")
include(CPack)

enable_testing()

if(MSVC)
  set(LIBRARY_PREFIX "")
  set(LIBRARY_SUFFIX .lib)
else()
  set(LIBRARY_PREFIX lib)
  set(LIBRARY_SUFFIX .a)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
  set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -static")
endif()

add_subdirectory(src/main/cpp)
add_subdirectory(src/test/cpp)

install(FILES README.md LICENSE CHANGES.md src/dist/logo.bmp DESTINATION .)
