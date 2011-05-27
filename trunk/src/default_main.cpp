#include "comparator.h"
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
class accumulator: public arr<boost::shared_ptr<T>, boost::shared_ptr<T> > {
    std::vector< boost::shared_ptr<T> > _values;
public:
    void next(const boost::shared_ptr<T>& t) {
        _values.push_back(t);
        /* if (_values.size() > 1) {
            make_pair(_values.begin(), _values.end())
            >>= sink< boost::shared_ptr<T> >::continuation();
        } */
    }
    void stop() {
        if (_values.size() > 1) {
            make_pair(_values.begin(), _values.end())
            >>= sink< boost::shared_ptr<T> >::continuation();
        }
        _values.clear();
    }
    boost::shared_ptr< end< boost::shared_ptr<T> > > clone() const {
        return boost::make_shared< accumulator<T> >(*this);
    }
};

void default_main() {
    struct : end< boost::shared_ptr<file_type::base> > {
        void next(const boost::shared_ptr<file_type::base>& t) { std::cout << t->path().string() << "\n"; }
        void stop() { std::cout << "\n"; }
    } output;
    
    make_pair(program_options::input_files().begin(), program_options::input_files().end())
    >>= fs::recursive()
    >>= (program_options::extensions().empty() ? The< arr<fs::path, fs::path> >() : The<elem_filter>(program_options::extensions()))
    >>= file_typer_match_first()
    >>= clusterization()
    >>= comparator<file_type::base, file_type::base>()
    >>= accumulator<file_type::base>()
    >>= output;
}
