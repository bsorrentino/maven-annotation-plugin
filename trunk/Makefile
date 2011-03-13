SRC_DIR = src
PROJECT_NAME = pn
SRCS = $(wildcard $(SRC_DIR)/*.cpp)
LIBS = boost_program_options boost_filesystem boost_system
CFLAGS = -O2 -Wall -pedantic

.PHONY: first debug release makedirs clean
first: debug
debug:
	@make makedirs DIR=debug
	@make debug/$(PROJECT_NAME) DIR=debug FLAGS=-ggdb
release:
	@make makedirs DIR=release
	@make release/$(PROJECT_NAME) DIR=release FLAGS=-DNDEBUG
makedirs:
	-@mkdir -p $(dir $(subst $(SRC_DIR),$(DIR),$(SRCS))) $(dir $(subst $(SRC_DIR),.depend,$(SRCS)))
clean:
	-@rm -rf debug release $(DIR) .depend

-include $(patsubst $(SRC_DIR)/%.cpp, .depend/%.dep, $(SRCS))

$(DIR)/$(PROJECT_NAME): $(patsubst $(SRC_DIR)/%.cpp, $(DIR)/%.o, $(SRCS))
	g++ $^ -o $@ $(patsubst %,-l%,$(LIBS))
$(DIR)/%.o: $(SRC_DIR)/%.cpp
	g++ $< -c $(CFLAGS) -o $@ $(FLAGS) -MMD -MT $@ -MF .depend/$*.dep
