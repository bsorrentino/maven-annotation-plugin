#ifndef _FILE_TYPE_H_
#define _FILE_TYPE_H_

#include "filesystem.h"
#include "kleisli.h"

class typed_file {
    fs::path _path;
public:
    typed_file(const fs::path& p): _path(p) {}
    const fs::path& path() const { return _path; }
};

class file_type_ext: public category::kleisli::arr<fs::path, typed_file> {
public:
    void next(const fs::path& p) {
        (*this)(typed_file(p));
    }
};

#endif
