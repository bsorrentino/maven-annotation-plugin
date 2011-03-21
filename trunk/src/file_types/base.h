#ifndef _FILE_TYPE_BASE_H_
#define _FILE_TYPE_BASE_H_

#include <boost/shared_ptr.hpp>

#include "../filesystem.h"
#include "../type_list.h"
#include "../kleisli.h"

namespace file_type {

class compare_result {
    const fs::path& _file1;
    const fs::path& _file2;
public:
    compare_result(const fs::path& f1, const fs::path& f2): _file1(f1), _file2(f2) {}
    const fs::path& file1() const { return _file1; }
    const fs::path& file2() const { return _file2; }
};

std::ostream& operator<<(std::ostream& o, const compare_result& r);

class base {
    fs::path _path;
public:
    base(const fs::path& p): _path(p) {}
    const fs::path& path() const { return _path; }
    static boost::shared_ptr<base> try_file(const fs::path& file);
    virtual void compare(const base& a, category::kleisli::end<compare_result>& cont) const;
};

}

#endif
