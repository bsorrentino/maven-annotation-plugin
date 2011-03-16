#ifndef _FILE_TYPE_IMG_H_
#define _FILE_TYPE_IMG_H_

#include "base.h"
#include "../type_list.h"

namespace file_type {

struct img: base {
    img() {}
    img(const fs::path& p): base(p) {}
    static bool try_file(const base& file, img* res);
};

}

#endif
