#include "logger.h"
#include "program_options.h"
#include "filesystem.h"
#include "binary_functor.h"
#include "comparer.h"
#include "sequence.h"

class HasExtension {
    const std::vector<std::string>& _extensions;
public:
    HasExtension(const std::vector<std::string>& extensions): _extensions(extensions) { }
    bool operator()(const fs::path& file) const {
        return find(_extensions.begin(), _extensions.end(), file.extension()) != _extensions.end();
    }
};

int main(int argc, char* argv[]) {
    stderr_logger std_logger(argv[0]);
    logger::set_std(&std_logger);
    
    program_options po(argc, argv);
    
    make_pair(po.input_files().begin(), po.input_files().end())
    >>= fs::recursive()
    >>= po.extensions().empty()
        ? (seq<fs::path, fs::path>&) The<id, fs::path>()
        : The<filter, fs::path>(HasExtension(po.extensions()))
    // >>= clusterize()
    // >>= comparator()
    >>= The<iterator_end, fs::path>(std::ostream_iterator<fs::path>(std::cout, "\n"));
    
    return 0;
}
