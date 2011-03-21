#include <boost/filesystem/fstream.hpp>
#include <boost/make_shared.hpp>
#include <cstring>
#include <cassert>

#include "base.h"
#include "../kleisli.h"
#include "../filesystem.h"

namespace file_type {

boost::shared_ptr<base> base::try_file(const fs::path& file) {
    return boost::make_shared<base>(file);
}

void base::compare(const base& a, category::kleisli::end<compare_result>& cont) const {
    if (fs::file_size(_path) != fs::file_size(a._path)) {
        return;
    }
    const int buf_size = 4096;
    char buf1[buf_size], buf2[buf_size];
    fs::ifstream file1(_path), file2(a._path);
    while (true) {
        file1.read(buf1, buf_size);
        file2.read(buf2, buf_size);
        assert(file1.gcount() == file2.gcount());
        if (file1.gcount() == file2.gcount()) {
            if (memcmp(buf1, buf2, file1.gcount()) != 0) {
                return;
            }
            assert(file1.eof() == file2.eof());
            if (file1.eof() && file2.eof()) {
                cont.next(compare_result(_path, a._path));
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

}
