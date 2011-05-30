#include <boost/program_options/variables_map.hpp>
#include <boost/program_options/parsers.hpp>
#include <exception>
#include <stdlib.h>

#include "program_options.h"
#include "filesystem.h"
#include "../config.h"
#include <stdio.h>

namespace po = boost::program_options;

std::vector<std::string> program_options::_input_files;
std::vector<std::string> program_options::_extensions;
std::vector<std::string> program_options::_text_formats;
std::vector<std::string> program_options::_image_formats;
float program_options::_image_threshold = 0.005;
unsigned int program_options::_text_words_count = 10
    , program_options::_image_bucket_count = 4
    , program_options::_image_max_diff = 200000
    , program_options::_image_img_size = 128
    , program_options::_text_threshold = 2
;
bool program_options::_image_precise = false;

static void parse_list(const std::string& list, std::vector<std::string>* res) {
    if (!list.empty()) {
        for (size_t p = 0, p2 = 0; p2 != std::string::npos; p = p2 + 1) {
            p2 = list.find(',', p);
            std::string ext = list.substr(p, p2 != std::string::npos ? p2 - p : p2);
            size_t i = 0;
            for (; i < ext.size(); ++i) {
                if (ext[i] != ' ') {
                    break;
                }
            }
            for (size_t j = i; j < ext.size(); ++j) {
                ext[j - i] = ext[j];
            }
            for (i = ext.size() - i - 1; i >= 0; --i) {
                if (ext[i] != ' ') {
                    break;
                }
            }
            ext.resize(i + 1);
            if (ext.length() > 0 && ext[0] != '.' && ext.find('/') == std::string::npos) {
                ext = "." + ext;
            }
            res->push_back(ext);
        }
    }
}

static void parse_config_file(po::options_description config_file_options, po::variables_map vm, std::string config_file) {
    fs::path file_name;
    if (vm.count("config")) {
        file_name = config_file;
    } else {
#ifndef FILE_CONF
        return;
#endif
        char* home = getenv("HOME");
        file_name = home ? fs::path(home) / "." PACKAGE_NAME / FILE_CONF : "";
        if (!exists(file_name)) {
#ifndef DATA_DIR
            return;
#endif
            file_name = fs::path(DATA_DIR) / PACKAGE / FILE_CONF;
            if (!exists(file_name)) {
                return;
            }
        }
    }
    po::store(po::parse_config_file<char>(file_name.c_str(), config_file_options), vm);
    po::notify(vm);
}

void program_options::initialize(int argc, char* argv[]) {
    _text_formats.push_back("text/plain");
    _text_formats.push_back(".txt");
    _image_formats.push_back("image/jpg");
    _image_formats.push_back(".jpg");
    _image_formats.push_back("image/png");
    _image_formats.push_back(".png");
    
    const std::string usage = std::string("Usage: ") + argv[0] + " [OPTIONS] FILES\n";
    try {
        std::string extensions, text_formats, image_formats, config_file;
        int image_img_size = _image_img_size
            , image_max_diff = _image_max_diff
            , image_bucket_count = _image_bucket_count
            , text_words_count = _text_words_count
            , text_threshold = _text_threshold
        ;
        
        po::options_description visible_options("Available options");
        visible_options.add_options()
            ("extensions", po::value(&extensions), "Search through files with <extensions>")
            ("config,c", po::value(&config_file), "Path to config file")
            ("help", "Print this message and exit")
        ;
        po::options_description config_file_options;
        config_file_options.add_options()
            ("text.formats", po::value(&text_formats))
            ("text.threshold", po::value(&text_threshold))
            ("text.words_count", po::value(&text_words_count))
            ("image.formats", po::value(&image_formats))
            ("image.bucket_count", po::value(&image_bucket_count))
            ("image.threshold", po::value(&_image_threshold))
            ("image.precise", po::value(&_image_precise))
            ("image.max_diff", po::value(&image_max_diff))
            ("image.img_size", po::value(&image_img_size))
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
        parse_config_file(config_file_options, vm, config_file);
        
        parse_list(extensions, &_extensions);
        parse_list(text_formats, &_text_formats);
        parse_list(image_formats, &_image_formats);
        
        if (image_img_size <= 0) {
            logger::std("image.img_size should be positive");
        } else {
            _image_img_size = image_img_size;
        }
        if (image_max_diff < 0) {
            logger::std("image.max_diff should be nonnegative");
        } else {
            _image_max_diff = image_max_diff;
        }
        if (_image_threshold < 0) {
            logger::std("image.threshold should be nonnegative");
            _image_threshold = 0.005;
        }
        if (image_bucket_count <= 0 || image_bucket_count > 65536) {
            logger::std("image.bucket_count should be positive and less than or equal to 65536");
        } else {
            _image_bucket_count = image_bucket_count;
        }
        if (text_words_count <= 0) {
            logger::std("text.words_count should be positive");
        } else {
            _text_words_count = text_words_count;
        }
        if (text_threshold < 0 || text_threshold > text_words_count * 2) {
            logger::std_stream() << "text.threshold should be nonnegative and less than or equal to " << 2 * _text_words_count << std::endl;
        } else {
            _text_threshold = text_threshold;
        }
        printf("%d\n", _text_threshold);
    } catch(std::exception const& e) {
        logger::std(e.what());
        exit(1);
    }
}
