#ifndef _COMPARATOR_H_
#define _COMPARATOR_H_

/*
#include "filesystem.h"
#include "kleisli.h"
#include "file_type.h"

class compare_result {
    const fs::path& _file1;
    const fs::path& _file2;
public:
    compare_result(const fs::path& f1, const fs::path& f2): _file1(f1), _file2(f2) {}
    const fs::path& file1() const { return _file1; }
    const fs::path& file2() const { return _file2; }
};

std::ostream& operator<<(std::ostream& o, const compare_result& r);

struct comparator: category::kleisli::arr< std::pair<typed_file, typed_file>, compare_result> {
    void next(const typed_file& file1, const typed_file& file2);
};
*/

#endif
