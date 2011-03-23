#include <boost/filesystem/fstream.hpp>
#include <boost/make_shared.hpp>
#include <cstring>
#include <cassert>

#include "base.h"
#include "../kleisli.h"
#include "../filesystem.h"

namespace file_type {

base::base(const fs::path& p): _path(p), size(fs::file_size(p)) { }

boost::shared_ptr<base> base::try_file(const fs::path& file) {
    return boost::make_shared<base>(file);
}

boost::shared_ptr<base> base::compare(const boost::shared_ptr<base>& a) const {
    if (fs::file_size(_path) != fs::file_size(a->_path)) {
        return boost::shared_ptr<base>();
    }
    const int buf_size = 4096;
    char buf1[buf_size], buf2[buf_size];
    fs::ifstream file1(_path), file2(a->_path);
    while (true) {
        file1.read(buf1, buf_size);
        file2.read(buf2, buf_size);
        if (file1.gcount() != file2.gcount() || file1.eof() != file2.eof() || memcmp(buf1, buf2, file1.gcount()) != 0) {
            return boost::shared_ptr<base>();
        }
        if (file1.eof() && file2.eof()) {
            return a;
        }
    }
}

}
