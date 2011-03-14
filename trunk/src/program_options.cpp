#include <boost/program_options/variables_map.hpp>
#include <boost/program_options/parsers.hpp>
#include <exception>

#include "program_options.h"

namespace po = boost::program_options;

program_options::program_options(int argc, char* argv[]) {
    const std::string usage = std::string("Usage: ") + argv[0] + " [OPTIONS] FILES\n";
    try {
        std::string extensions;
        po::options_description visible_options("Available options");
        visible_options.add_options()
            ("extensions", po::value(&extensions), "Search through files with <extensions>")
            ("help", "Print this message and exit")
        ;
        
        po::options_description command_line_options;
        command_line_options.add(visible_options).add_options()
            ("files", po::value(&_input_files))
        ;
        
        po::positional_options_description positional_options;
        positional_options.add("files", -1);
        
        po::variables_map vm;
        po::store(
            po::command_line_parser(argc, argv)
            .options(command_line_options)
            .positional(positional_options)
            .run(), vm);
        po::notify(vm);
        
        if (vm.count("help")) {
            std::cout << usage;
            std::cout << visible_options << "\n";
            exit(0);
        }
        
        if (!extensions.empty()) {
            for (size_t p = 0, p2 = 0; p2 != std::string::npos; p = p2 + 1) {
                p2 = extensions.find(',', p);
                std::string ext = extensions.substr(p, p2 != std::string::npos ? p2 - p : p2);
                if (ext.length() > 0 && ext[0] != '.') {
                    ext = "." + ext;
                }
                _extensions.push_back(ext);
            }
        }
    } catch(std::exception const& e) {
        logger::std(e.what());
        exit(1);
    }
}
