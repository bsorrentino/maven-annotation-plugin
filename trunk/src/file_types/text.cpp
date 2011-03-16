#include <boost/filesystem/fstream.hpp>

#include "text.h"
#include "../kleisli.h"

namespace file_type {

bool text::try_file(const base& file, text* res) {
    static const fs::path exts[] = { ".txt" };
    static const fs::path* exts_end = exts + sizeof(exts)/sizeof(fs::path);
    if (find(exts, exts_end, file.path().extension()) != exts_end) {
        res->set_path(file.path());
        return true;
    }
    return false;
}

void text::compare(const base& a, category::kleisli::arr<base, compare_result>& cont) const {
    if (fs::file_size(path()) != fs::file_size(a.path())) {
        return;
    }
    cont(compare_result(path(), a.path()));
}

}
