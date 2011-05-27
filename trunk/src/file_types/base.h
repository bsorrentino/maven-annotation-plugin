#ifndef _FILE_TYPE_BASE_H_
#define _FILE_TYPE_BASE_H_

#include <boost/shared_ptr.hpp>
#include <boost/algorithm/string.hpp>
#include <vector>
#include <string>

#include "../filesystem.h"
#include "../type_list.h"
#include "../kleisli.h"

namespace file_type {

enum comparison_result
  { less
  , greater
  , equal
  };

class base {
    fs::path _path;
    unsigned int _size;
    std::string _mime;
public:
    base(const fs::path& p);
    const fs::path& path() const { return _path; }
    const std::string& mime() const { return _mime; }
    static boost::shared_ptr<base> try_file(const fs::path& file);
    virtual boost::shared_ptr<base> compare(const boost::shared_ptr<base>& a) const;
    virtual comparison_result precompare(const boost::shared_ptr<base>& a) const;
    
    bool check_type(const std::vector<std::string>& types) const;
};

}

#endif
