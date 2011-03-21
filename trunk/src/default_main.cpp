#include <boost/shared_ptr.hpp>

#include "clusterization.h"
#include "program_options.h"
#include "filesystem.h"
#include "kleisli.h"
#include "file_types.h"
#include "file_typer.h"
#include "file_types/base.h"

using namespace category::kleisli;

class elem_filter: public arr<fs::path, fs::path> {
    const std::vector<std::string>& _elems;
public:
    elem_filter(const std::vector<std::string>& elems): _elems(elems) { }
    void next(const fs::path& value) {
        if (find(_elems.begin(), _elems.end(), value.extension()) != _elems.end()) {
            pass(value);
        }
    }
};

template<typename T>
class comparator: public category::kleisli::arr<boost::shared_ptr<T>, file_type::compare_result> {
    std::vector< boost::shared_ptr<T> > _values;
public:
    void next(const boost::shared_ptr<T>& t) {
        typedef typename std::vector<boost::shared_ptr<T> >::iterator iterator;
        for (iterator it = _values.begin(); it != _values.end(); ++it) {
            (*it)->compare(*t, sink<file_type::compare_result>::continuation());
        }
        _values.push_back(t);
    }
    boost::shared_ptr< end< boost::shared_ptr<T> > > clone() const {
        return boost::make_shared< comparator<T> >(*this);
    }
};

void default_main(const program_options& po) {
    make_pair(po.input_files().begin(), po.input_files().end())
    >>= The<fs::recursive>()
    >>= (po.extensions().empty() ? The< arr<fs::path, fs::path> >() : The<elem_filter>(po.extensions()))
    >>= The<file_typer_match_first>()
    >>= clusterization<file_type::base>()
    >>= The<comparator<file_type::base> >()
    >>= The<iterator_end, file_type::compare_result>
        (std::ostream_iterator<file_type::compare_result>(std::cout, "\n"));
}
