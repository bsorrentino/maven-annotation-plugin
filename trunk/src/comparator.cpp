#include <cstring>
#include <iostream>
#include <boost/filesystem/fstream.hpp>
#include <cassert>

#include "comparator.h"

void comparator::next(const typed_file& filename1, const typed_file& filename2) {
    if (fs::file_size(filename1.path()) != fs::file_size(filename2.path())) {
        return;
    }
    const int buf_size = 4096;
    char buf1[buf_size], buf2[buf_size];
    fs::ifstream file1(filename1.path()), file2(filename2.path());
    while (true) {
        file1.read(buf1, buf_size);
        file2.read(buf2, buf_size);
        assert(file1.gcount() == file2.gcount());
        if (file1.gcount() == file2.gcount()) {
            if (std::memcmp(buf1, buf2, file1.gcount()) != 0) {
                return;
            }
            assert(file1.eof() == file2.eof());
            if (file1.eof() && file2.eof()) {
                (*this)(compare_result(filename1.path(), filename2.path()));
                return;
            } else
            if (!file1.eof() && !file2.eof()) {
                continue;
            }
        }
        return;
    }
}

std::ostream& operator<<(std::ostream& o, const compare_result& r) {
    o << "files " << r.file1() << " and " << r.file2() << " are equal";
    return o;
}
