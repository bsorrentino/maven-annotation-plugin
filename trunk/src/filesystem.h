#ifndef _FILESYSTEM_H_
#define _FILESYSTEM_H_

#include <iterator>
#include <boost/filesystem.hpp>
#include <boost/filesystem/fstream.hpp>

#include "logger.h"
#include "functor_iterator.h"
#include "sequence.h"

namespace fs {

using namespace boost::filesystem;

class recursive: public seq<std::string, fs::path> {
public:
    void operator()(const fs::path& p) { put(p); }
    void operator()(const std::string& value) {
        if (!exists(value)) {
            logger::std_stream() << value << " does not exists" << std::endl;
        } else
        if (is_directory(value)) {
            remove_copy_if(recursive_directory_iterator(value), recursive_directory_iterator(),
                functor_iterator<recursive, fs::path>(*this),
                std::not1(std::ptr_fun((bool(*)(const path&))(&is_regular_file))));
        } else
        if (is_regular_file(value)) {
            put(value);
        } else {
            logger::std_stream() << value << " neither directory nor regular file" << std::endl;
        }
    }
};

}

#endif
