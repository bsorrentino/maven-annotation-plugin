#ifndef _PROGRAM_OPTIONS_H_
#define _PROGRAM_OPTIONS_H_

#include <vector>

#include "logger.h"

class program_options {
    static std::vector<std::string> _input_files;
    static std::vector<std::string> _extensions;
    static std::vector<std::string> _text_formats;
    static std::vector<std::string> _image_formats;
    static float _image_threshold;
    static unsigned int _text_threshold
        , _text_words_count
        , _image_bucket_count
        , _image_max_diff
        , _image_img_size;
    static bool _image_precise;
public:
    static void initialize(int argc, char* argv[]);
    static const std::vector<std::string>& input_files() { return _input_files; }
    static const std::vector<std::string>& extensions() { return _extensions; }
    static const std::vector<std::string>& text_formats() { return _text_formats; }
    static const std::vector<std::string>& image_formats() { return _image_formats; }
    static float image_threshold() { return _image_threshold; }
    static unsigned int text_threshold() { return _text_threshold; }
    static unsigned int text_words_count() { return _text_words_count; }
    static unsigned int image_bucket_count() { return _image_bucket_count; }
    static unsigned int image_max_diff() { return _image_max_diff; }
    static unsigned int image_img_size() { return _image_img_size; }
    static bool image_precise() { return _image_precise; }
};

#endif
