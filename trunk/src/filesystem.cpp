#include <iterator>

#include "filesystem.h"
#include "logger.h"
#include "functor_iterator.h"

namespace fs {

void recursive::next(const std::string& value) {
    if (!exists(value)) {
        logger::std_stream() << value << " does not exists" << std::endl;
    } else
    if (is_directory(value)) {
        remove_copy_if(recursive_directory_iterator(value), recursive_directory_iterator(),
            functor_iterator<recursive, fs::path>(*this),
            std::not1(std::ptr_fun((bool(*)(const path&))(&is_regular_file))));
    } else
    if (is_regular_file(value)) {
        (*this)(value);
    } else {
        logger::std_stream() << value << " neither directory nor regular file" << std::endl;
    }
}

}
