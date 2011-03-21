#include "logger.h"
#include "program_options.h"

void default_main(const program_options& po);

int main(int argc, char* argv[]) {
    stderr_logger std_logger(argv[0]);
    logger::set_std(&std_logger);
    
    program_options po(argc, argv);
    
    default_main(po);
    
    return 0;
}
