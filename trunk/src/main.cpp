#include "logger.h"
#include "program_options.h"
#include "filesystem.h"
#include "binary_functor.h"
#include "comparer.h"

int main(int argc, char* argv[]) {
    stderr_logger std_logger(argv[0]);
    logger::set_std(&std_logger);
    
    program_options po(argc, argv);
    
    typedef std::ostream_iterator<comparer::result_type> out_iter;
    typedef binary_functor<fs::path, out_iter, comparer> bin_funct;
    for_each(po.input_files().begin(), po.input_files().end(),
        fs::recursive<bin_funct>(bin_funct(out_iter(std::cout), comparer())));
}
