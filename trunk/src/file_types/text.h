#ifndef _FILE_TYPE_TEXT_H_
#define _FILE_TYPE_TEXT_H_

#include "base.h"
#include "../type_list.h"

namespace file_type {

struct text: base {
    text() {}
    text(const fs::path& p): base(p) {}
    //TODO: smart ptrs
    static bool try_file(const base& file, text* res);
    void compare(const base& a, category::kleisli::arr<base, compare_result>& cont) const;
    //TODO: smart ptrs
    virtual base* clone() const { return new text(path()); }
};

}

#endif
