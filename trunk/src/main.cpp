#include "logger.h"
#include "program_options.h"
#include "filesystem.h"
#include "pairing.h"
#include "comparator.h"
#include "kleisli.h"
#include "file_type.h"
#include "clusterization.h"

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

int main(int argc, char* argv[]) {
    stderr_logger std_logger(argv[0]);
    logger::set_std(&std_logger);
    
    program_options po(argc, argv);
    
    make_pair(po.input_files().begin(), po.input_files().end())
    >>= The<fs::recursive>()
    >>= po.extensions().empty() ? The< arr<fs::path, fs::path> >() : The<elem_filter>(po.extensions())
    >>= The<file_type_ext>()
//    >>= The<clusterization>()
    >>= The<pairing<typed_file> >()
    >>= The<comparator>()
    >>= The<iterator_end, compare_result>(std::ostream_iterator<compare_result>(std::cout, "\n"));
    
    return 0;
}
