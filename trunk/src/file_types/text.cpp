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
    if (fs::file_size(path()) != fs::file_size(a->path())) {
        return boost::shared_ptr<base>();
    }
    return a;
}

}
