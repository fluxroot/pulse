project(test)

if(MSVC)
  set(GTEST_ARGS "-Dgtest_force_shared_crt:BOOL=ON")
else()
  set(GTEST_ARGS "")
endif()

include(ExternalProject)
ExternalProject_Add(
  googletest
  URL https://googletest.googlecode.com/files/gtest-1.7.0.zip
  URL_HASH SHA1=f85f6d2481e2c6c4a18539e391aa4ea8ab0394af
  INSTALL_COMMAND ""
  CMAKE_ARGS ${GTEST_ARGS}
)
ExternalProject_Get_Property(googletest SOURCE_DIR)
ExternalProject_Get_Property(googletest BINARY_DIR)

include_directories(
  ${main_SOURCE_DIR}
  ${SOURCE_DIR}/include
)

add_library(gtest UNKNOWN IMPORTED)
set_target_properties(gtest PROPERTIES IMPORTED_LOCATION ${BINARY_DIR}/${CMAKE_CFG_INTDIR}/${LIBRARY_PREFIX}gtest${LIBRARY_SUFFIX})
add_dependencies(gtest googletest)

add_library(gtest_main UNKNOWN IMPORTED)
set_target_properties(gtest_main PROPERTIES IMPORTED_LOCATION ${BINARY_DIR}/${CMAKE_CFG_INTDIR}/${LIBRARY_PREFIX}gtest_main${LIBRARY_SUFFIX})
add_dependencies(gtest_main googletest)

add_executable(unittest
  bitboardtest.cpp
  castlingtypetest.cpp
  colortest.cpp
  evaluationtest.cpp
  notationtest.cpp
  filetest.cpp
  movegeneratortest.cpp
  movelisttest.cpp
  movetest.cpp
  piecetest.cpp
  piecetypetest.cpp
  positiontest.cpp
  ranktest.cpp
  squaretest.cpp
)

target_link_libraries(unittest core gtest gtest_main)

add_test(unittest unittest)
