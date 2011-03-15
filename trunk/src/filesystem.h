#ifndef _FILESYSTEM_H_
#define _FILESYSTEM_H_

#include <boost/filesystem.hpp>

#include "kleisli.h"

namespace fs {

using namespace boost::filesystem;

struct recursive: category::kleisli::arr<std::string, fs::path> {
    void next(const std::string& value);
};

}

#endif
