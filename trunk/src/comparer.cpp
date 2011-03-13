#include <cstring>
#include <iostream>

#include "comparer.h"

comparer::result_type comparer::operator()(const fs::path& filename1, const fs::path& filename2) const {
    const int buf_size = 4096;
    char buf1[buf_size], buf2[buf_size];
    if (fs::file_size(filename1) != fs::file_size(filename2)) {
        return result_type();
    }
    fs::ifstream file1(filename1), file2(filename2);
    while (true) {
        file1.read(buf1, buf_size);
        file2.read(buf2, buf_size);
        if (file1.gcount() == file2.gcount()) {
            if (std::memcmp(buf1, buf2, file1.gcount()) == 0) {
                if (file1.eof() && file2.eof()) {
                    return result_type(filename1, filename2);
                } else
                if (!file1.eof() && !file2.eof()) {
                    continue;
                }
            }
        }
        return result_type();
    }
}

std::ostream& operator<<(std::ostream& o, const comparer::result_type& r) {
    if (!r.file1.empty() && !r.file2.empty()) {
        o << "files " << r.file1 << " and " << r.file2 << " are equal\n";
    }
    return o;
}
