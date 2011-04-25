LIBS = boost_program_options boost_filesystem boost_system magic
CFLAGS = -Wall `Magick++-config --cppflags`
LINKER_FLAGS = `Magick++-config --libs`
PROJECT_NAME = pn
SRC_DIR = src
SRCS = $(wildcard $(SRC_DIR)/*.cpp $(SRC_DIR)/*/*.cpp)
OBJS = $(patsubst $(SRC_DIR)/%.cpp,%.o,$(SRCS))

builds = release debug valgrind
debug_flags = -ggdb -O2
release_flags = -DNDEBUG -O2
valgrind_flags = -g -O0

.PHONY: $(builds) clean
$(builds): %: %-makedirs %/$(PROJECT_NAME)
$(addsuffix -makedirs,$(builds)): %-makedirs:
	-@mkdir -p $(dir $(subst $(SRC_DIR),$*,$(SRCS))) $(dir $(subst $(SRC_DIR),.depend,$(SRCS)))
clean:
	-@rm -rf $(builds) .depend

-include $(patsubst $(SRC_DIR)/%.cpp,.depend/%.dep,$(SRCS))

$(addsuffix /%.o,$(builds)): $(SRC_DIR)/%.cpp
	g++ $< -c -o $@ $(CFLAGS) $($(@D)_flags) -MMD -MT $@ -MF .depend/$*.dep
$(addsuffix /$(PROJECT_NAME),$(builds)): %$(PROJECT_NAME): $(addprefix %,$(OBJS))
	g++ $^ -o $@ $(LINKER_FLAGS) $(addprefix -l,$(LIBS))

%:
	$(info target $@ not found)
