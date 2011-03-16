#include <iterator>

#include "filesystem.h"
#include "logger.h"

template<class It, class F>
void for_each_regular(It p, It q, F & f)
{
    for( ; p != q; ++p)
    {
        if(!is_regular_file(*p))
            f(*p);
    }
}

namespace fs {

void recursive::next(const std::string& value) {
    if (!exists(value)) {
        logger::std_stream() << value << " does not exists" << std::endl;
    } else
    if (is_directory(value)) {
        for_each_regular(recursive_directory_iterator(value), recursive_directory_iterator(), *this);
    } else
    if (is_regular_file(value)) {
        (*this)(value);
    } else {
        logger::std_stream() << value << " neither directory nor regular file" << std::endl;
    }
}

}
