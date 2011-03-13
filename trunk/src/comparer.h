#ifndef _COMPARER_H_
#define _COMPARER_H_

#include <iostream>

#include "filesystem.h"

class comparer {
public:
    struct result_type {
        fs::path file1;
        fs::path file2;
        result_type() {}
        result_type(const fs::path& f1, const fs::path& f2): file1(f1), file2(f2) {}
    };
    typedef fs::path first_argument_type;
    typedef fs::path second_argument_type;
    result_type operator()(const fs::path& file1, const fs::path& file2) const;
};

std::ostream& operator<<(std::ostream& o, const comparer::result_type& r);

#endif
