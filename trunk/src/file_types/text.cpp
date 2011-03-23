#include <boost/filesystem/fstream.hpp>
#include <boost/make_shared.hpp>
#include <string.h>
#include <ctype.h>

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

static int clean_str(char* s, int size) {
    int c = 0;
    for (int i = 0; i < size; ++i) {
        if (isalnum(s[i])) {
            s[c++] = s[i];
        }
    }
    return c;
}

boost::shared_ptr<base> text::compare(const boost::shared_ptr<base>& a) const {
    const int buf_size = 4096;
    char buf1[buf_size], buf2[buf_size];
    char *cuf1 = buf1, *cuf2 = buf2;
    fs::ifstream file1(path()), file2(a->path()), *file;
    while (true) {
        file1.read(cuf1, buf_size - (cuf1 - buf1));
        file2.read(cuf2, buf_size - (cuf2 - buf2));
        int r1 = clean_str(cuf1, file1.gcount());
        int r2 = clean_str(cuf2, file2.gcount());
        if (memcmp(cuf1, cuf2, r1 < r2 ? r1 : r2) != 0) {
            return boost::shared_ptr<base>();
        }
        if (file1.eof() && file2.eof()) {
            return a;
        }
        if (r1 <= r2) {
            cuf1 = buf1;
            cuf2 = buf2 + r2 - r1;
        } else {
            cuf1 = buf1 + r1 - r2;
            cuf2 = buf2;
        }
        if (file1.eof()) {
            if (cuf1 != buf1) {
                return boost::shared_ptr<base>();
            }
            file = &file2;
            break;
        }
        if (file2.eof()) {
            if (cuf2 != buf2) {
                return boost::shared_ptr<base>();
            }
            file = &file1;
            break;
        }
    }
    while (true) {
        file->read(buf1, buf_size);
        int r = clean_str(buf1, file->gcount());
        if (r > 0) {
            return boost::shared_ptr<base>();
        }
        if (file->eof()) {
            return a;
        }
    }
}

}
