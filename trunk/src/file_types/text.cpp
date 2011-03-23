#include <boost/filesystem/fstream.hpp>
#include <boost/make_shared.hpp>

#include "text.h"
#include "../kleisli.h"

namespace file_type {

boost::shared_ptr<text> text::try_file(const boost::shared_ptr<base>& file) {
    static const fs::path exts[] = { ".txt" };
    static const fs::path* exts_end = exts + sizeof(exts)/sizeof(fs::path);
    if (find(exts, exts_end, file->path().extension()) != exts_end) {
        return boost::make_shared<text>(file->path());
    }
    return boost::shared_ptr<text>();
}

boost::shared_ptr<base> text::compare(const boost::shared_ptr<base>& a) const {
    // const int buf_size = 4096;
    // char buf1[buf_size], buf2[buf_size];
    // fs::ifstream file1(_path), file2(a->_path);
    return a;
}

}
