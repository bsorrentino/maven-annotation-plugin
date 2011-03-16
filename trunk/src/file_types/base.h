#ifndef _FILE_TYPE_BASE_H_
#define _FILE_TYPE_BASE_H_

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
protected:
    void set_path(const fs::path& p) { _path = p; }
public:
    base() {}
    base(const fs::path& p): _path(p) {}
    const fs::path& path() const { return _path; }
    //TODO: smart ptrs
    static bool try_file(const fs::path& file, base* res);
    virtual void compare(const base& a, category::kleisli::arr<base, compare_result>& cont) const;
    //TODO: smart ptrs
    virtual base* clone() const { return new base(_path); }
};

}

#endif
