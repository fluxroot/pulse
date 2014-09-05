project(main)

add_library(core STATIC
  bitboard.cpp
  board.cpp
  castling.cpp
  castlingtype.cpp
  color.cpp
  evaluation.cpp
  notation.cpp
  file.cpp
  move.cpp
  movegenerator.cpp
  movelist.cpp
  movetype.cpp
  perft.cpp
  piece.cpp
  piecetype.cpp
  rank.cpp
  search.cpp
  square.cpp
  uci.cpp
)

add_executable(pulse main.cpp)
set_target_properties(pulse PROPERTIES OUTPUT_NAME "pulse-${pulse_VERSION}")

target_link_libraries(pulse core)

install(TARGETS pulse DESTINATION .)
