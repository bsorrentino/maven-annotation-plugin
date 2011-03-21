#ifndef _FILE_TYPE_TEXT_H_
#define _FILE_TYPE_TEXT_H_

#include "base.h"
#include "../type_list.h"

namespace file_type {

struct text: base {
    text(const fs::path& p): base(p) {}
    static boost::shared_ptr<text> try_file(const boost::shared_ptr<base>& file);
    void compare(const base& a, category::kleisli::end<compare_result>& cont) const;
};

}

#endif
