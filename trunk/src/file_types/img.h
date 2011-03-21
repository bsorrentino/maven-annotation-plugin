#ifndef _FILE_TYPE_IMG_H_
#define _FILE_TYPE_IMG_H_

#include <boost/shared_ptr.hpp>

#include "base.h"
#include "../type_list.h"

namespace file_type {

struct img: base {
    img(const fs::path& p): base(p) {}
    static boost::shared_ptr<img> try_file(const boost::shared_ptr<base>& file);
};

}

#endif
