LIBS = boost_program_options boost_filesystem boost_system
CFLAGS = -O2 -Wall -pedantic `Magick++-config --cppflags`
LINKER_FLAGS = `Magick++-config --libs`
PROJECT_NAME = pn
SRC_DIR = src
SRCS = $(wildcard $(SRC_DIR)/*.cpp $(SRC_DIR)/*/*.cpp)
OBJS = $(patsubst $(SRC_DIR)/%.cpp,%.o,$(SRCS))

builds = debug release
debug_flags = -ggdb
release_flags = -DNDEBUG

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
