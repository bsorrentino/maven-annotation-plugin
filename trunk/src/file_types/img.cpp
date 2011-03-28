#include "img.h"
#include "../kleisli.h"
#include "../logger.h"

namespace file_type {

const double THRESHOLD = 0.005;

img::img(const fs::path& p, const Magick::Image& image): base(p), _image(image) {
    const Magick::PixelPacket* pix = image.getConstPixels(0, 0, image.size().width(), image.size().height());
    static const unsigned int BUCKET_SIZE = 65536 / BUCKET_COUNT;
    for (unsigned int j = 0; j < BUCKET_COUNT; ++j) {
        bucket[0][j] = bucket[1][j] = bucket[2][j] = 0;
    }
    unsigned int size = image.size().width() * image.size().height();
    for (unsigned int i = 0; i < size; ++i, ++pix) {
        for (unsigned int j = 0, c = BUCKET_SIZE; j < BUCKET_COUNT; ++j, c += BUCKET_SIZE) {
            if (pix->red < c) {
                ++bucket[0][j];
            }
            if (pix->green < c) {
                ++bucket[1][j];
            }
            if (pix->blue < c) {
                ++bucket[2][j];
            }
        }
    }
    for (unsigned int j = 0; j < BUCKET_COUNT; ++j) {
        for (unsigned int k = 0; k < HISTOGRAM_COUNT; ++k) {
            bucket[k][j] /= size;
        }
    }
}

boost::shared_ptr<img> img::try_file(const boost::shared_ptr<base>& file) {
    static const std::string mimes[] = { "image/jpeg", "image/png" };
    static const std::string exts[] = { ".jpg", ".png" };
    if (file->check_type(mimes, mimes + sizeof(mimes)/sizeof(std::string),
      exts, exts + sizeof(exts)/sizeof(std::string))) {
        try { 
            Magick::Image image;
            image.read(file->path().string()); 
            return boost::make_shared<img>(file->path(), image);
        } catch (Magick::Exception& e) { 
            logger::std(e.what());
        } 
    }
    return boost::shared_ptr<img>();
}

static double abs(double x) { return x >= 0 ? x : -x; }

boost::shared_ptr<base> img::compare(const boost::shared_ptr<base>& _a) const {
    const img* a = static_cast<const img*>(_a.get());
    double res = 0;
    for (unsigned int i = 0; i < BUCKET_COUNT; ++i) {
        for (unsigned int k = 0; k < HISTOGRAM_COUNT; ++k) {
            res += abs(bucket[k][i] - a->bucket[k][i]);
        }
    }
    return res > THRESHOLD ? boost::shared_ptr<img>() : _a;
}

inline comparison_result img::precompare(const boost::shared_ptr<base>& a) const {
    return equal;
    // return static_cast<const img*>(a.get())->bucket[0][0] - bucket[0][0] > THRESHOLD ? less : equal;
}

}
