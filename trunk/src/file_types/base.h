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
    
    template<typename It1, typename It2>
    bool check_type(It1 mimes, It1 mimes_end, It2 exts, It2 exts_end) const {
        bool res;
        if (!_mime.empty()) {
            res = std::find(mimes, mimes_end, _mime) != mimes_end;
        } else {
            std::string ext = boost::to_lower_copy(_path.extension().string());
            res = std::find(exts, exts_end, ext) != exts_end;
        }
        return res;
    }
};

}

#endif
