#include <Magick++.h>

#include "img.h"
#include "../kleisli.h"
#include "../logger.h"

namespace file_type {

boost::shared_ptr<img> img::try_file(const boost::shared_ptr<base>& file) {
    static const fs::path exts[] = { ".jpg", ".gif", ".jpeg", ".bmp", ".png" };
    static const fs::path* exts_end = exts + sizeof(exts)/sizeof(fs::path);
    if (find(exts, exts_end, file->path().extension()) != exts_end) {
        return boost::make_shared<img>(file->path());
    }
    return boost::shared_ptr<img>();
}

boost::shared_ptr<base> img::compare(const boost::shared_ptr<base>& a) const {
    Magick::Image image, image2; 
    try { 
        image.read(path().string()); 
        image2.read(a->path().string()); 
        image.scale("16x16");
        image2.scale("16x16");
        if (image.compare(image2)) {
            return a;
        }
    } catch (Magick::Exception &e) { 
        logger::std(e.what());
    } 
    return boost::shared_ptr<img>();
}

}
