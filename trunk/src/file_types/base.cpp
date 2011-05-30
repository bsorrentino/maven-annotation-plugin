#include <boost/filesystem/fstream.hpp>
#include <boost/make_shared.hpp>

#include "base.h"
#include "../kleisli.h"
#include "../filesystem.h"
#include "../magic_file.h"

namespace file_type {

base::base(const fs::path& p): _path(p), _size(fs::file_size(p)) {
    const char* s = magic::mime_type(p.c_str());
    _mime = s ? s : "";
}

boost::shared_ptr<base> base::try_file(const fs::path& file) {
    return boost::make_shared<base>(file);
}

boost::shared_ptr<base> base::compare(const boost::shared_ptr<base>& a) const {
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

inline comparison_result base::precompare(const boost::shared_ptr<base>& a) const {
    return _size < a->_size ? less : _size == a->_size ? equal : greater;
}

bool base::check_type(const std::vector<std::string>& types) const {
    bool res;
    if (!_mime.empty()) {
        res = std::find(types.begin(), types.end(), _mime) != types.end();
    } else {
        std::string ext = boost::to_lower_copy(_path.extension().string());
        res = std::find(types.begin(), types.end(), ext) != types.end();
    }
    return res;
}

}
