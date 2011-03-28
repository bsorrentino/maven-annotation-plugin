#include "filesystem.h"
#include "logger.h"

namespace fs {

void recursive::next(const std::string& value) {
    if (!exists(value)) {
        logger::std_stream() << value << " does not exists" << std::endl;
    } else
    if (is_directory(value)) {
        struct : category::kleisli::arr<fs::path, fs::path> {
            void next(const fs::path& value) { if (is_regular_file(value)) pass(value); }
        } filter;
        boost::system::error_code e;
        std::make_pair(recursive_directory_iterator(value, e), recursive_directory_iterator())
        >>= filter >>= continuation();
        if (e) {
            logger::std_stream() << value << ": " << e.message() << "\n";
        }
    } else
    if (is_regular_file(value)) {
        pass(value);
    } else {
        logger::std_stream() << value << " neither directory nor regular file" << std::endl;
    }
}

}
