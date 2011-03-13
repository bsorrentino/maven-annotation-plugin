#ifndef _FILESYSTEM_H_
#define _FILESYSTEM_H_

#include <iterator>
#include <boost/filesystem.hpp>
#include <boost/filesystem/fstream.hpp>

#include "logger.h"
#include "functor_iterator.h"

namespace fs {

using namespace boost::filesystem;

template<typename Funct>
class recursive {
    Funct _funct;
public:
    typedef typename Funct::value_type value_type;
    recursive(const Funct& funct): _funct(funct) {}
    template<typename T>
    void operator()(const T& value) {
        if (!exists(value)) {
            logger::std_stream() << value << " does not exists" << std::endl;
        } else
        if (is_directory(value)) {
            remove_copy_if(recursive_directory_iterator(value), recursive_directory_iterator(),
                functor_iterator<Funct>(_funct),
                std::not1(std::ptr_fun((bool(*)(const path&))(&is_regular_file))));
        } else
        if (is_regular_file(value)) {
            _funct(value);
        } else {
            logger::std_stream() << value << " neither directory nor regular file" << std::endl;
        }
    }
};

}

#endif
