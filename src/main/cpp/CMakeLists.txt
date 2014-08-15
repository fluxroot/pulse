project(main)

add_library(core STATIC
  board.cpp
  pulse.cpp
)

add_executable(pulse main.cpp)
set_target_properties(pulse PROPERTIES OUTPUT_NAME "pulse-${pulse_VERSION_MAJOR}.${pulse_VERSION_MINOR}")

target_link_libraries(pulse core)

install(TARGETS pulse DESTINATION .)
