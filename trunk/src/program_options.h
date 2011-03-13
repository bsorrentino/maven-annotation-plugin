#ifndef _PROGRAM_OPTIONS_H_
#define _PROGRAM_OPTIONS_H_

#include <vector>
#include <iostream>

#include "logger.h"

class program_options {
    std::vector<std::string> _input_files;
public:
    program_options(int argc, char* argv[]);
    const std::vector<std::string>& input_files() const { return _input_files; }
};

#endif
