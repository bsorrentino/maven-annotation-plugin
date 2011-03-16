#include "logger.h"
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
            (*this)(value);
        }
    }
};

template<typename T>
class comparator: public category::kleisli::arr<T, file_type::compare_result> {
    //TODO: smart ptrs
    std::vector<T*> values;
public:
    void next(const T& t) {
        for (typename std::vector<T*>::iterator it = values.begin(); it != values.end(); ++it) {
            (*it)->compare(t, *this);
        }
        values.push_back(t.clone());
    }
};

int main(int argc, char* argv[]) {
    stderr_logger std_logger(argv[0]);
    logger::set_std(&std_logger);
    
    program_options po(argc, argv);
    
    make_pair(po.input_files().begin(), po.input_files().end())
    >>= The<fs::recursive>()
    >>= po.extensions().empty() ? The< arr<fs::path, fs::path> >() : The<elem_filter>(po.extensions())
    >>= The<file_typer<match_first> >()
//    >>= The<clusterization<file_type::base> >()
    >>= The<comparator<file_type::base> >()
    >>= The<iterator_end, file_type::compare_result>
        (std::ostream_iterator<file_type::compare_result>(std::cout, "\n"));
    
    return 0;
}
