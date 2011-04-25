#ifndef _FILE_TYPE_TEXT_H_
#define _FILE_TYPE_TEXT_H_

#include <stdint.h>
#include <vector>

#include "base.h"
#include "../type_list.h"

namespace file_type {

struct text: base {
    text(const fs::path& p);
    static boost::shared_ptr<text> try_file(const boost::shared_ptr<base>& file);
    boost::shared_ptr<base> compare(const boost::shared_ptr<base>& a) const;
    comparison_result precompare(const boost::shared_ptr<base>& a) const { return equal; }
private:
    static const unsigned int WORDS_COUNT = 10;
    std::vector<uint32_t> _hashes;
    std::string _words[WORDS_COUNT];
};

}

#endif
