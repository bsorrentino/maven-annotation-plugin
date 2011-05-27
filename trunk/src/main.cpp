#include "logger.h"
#include "program_options.h"
#include "magic_file.h"

void default_main();

int main(int argc, char* argv[]) {
    stderr_logger std_logger(argv[0]);
    logger::set_std(&std_logger);
    
    program_options::initialize(argc, argv);
    
    magic::initialize();
    
    default_main();
    
    magic::destroy();
    
    return 0;
}
