#include "img.h"
#include "../kleisli.h"
#include "../logger.h"
#include "../program_options.h"

namespace file_type {

static const unsigned int PIX_IN_INT = sizeof(unsigned int) >= sizeof(Magick::PixelPacket)
    ? sizeof(unsigned int) / sizeof(Magick::PixelPacket) : 1;
static const float EPSILON = 0.00001;

img::img(const fs::path& p, const Magick::Image& image): base(p), _pixels(0) {
    const Magick::PixelPacket* pix = image.getConstPixels(0, 0, image.size().width(), image.size().height());
    static const unsigned int BUCKET_SIZE = 65536 / program_options::image_bucket_count();
    for (unsigned int j = 0; j < program_options::image_bucket_count(); ++j) {
        for (unsigned int k = 0; k < HISTOGRAM_COUNT; ++k) bucket[k].push_back(0);
    }
    unsigned int size = image.size().width() * image.size().height();
    for (unsigned int i = 0; i < size; ++i, ++pix) {
        for (unsigned int j = 0, c = BUCKET_SIZE;
            j < program_options::image_bucket_count(); ++j, c += BUCKET_SIZE) {
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
    for (unsigned int j = 0; j < program_options::image_bucket_count(); ++j) {
        for (unsigned int k = 0; k < HISTOGRAM_COUNT; ++k) {
            bucket[k][j] /= size;
        }
    }
}

img::~img() {
    if (_pixels != 0) {
        delete [] _pixels;
    }
}

boost::shared_ptr<img> img::try_file(const boost::shared_ptr<base>& file) {
    if (file->check_type(program_options::image_formats())) {
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
    {   double res = 0;
        for (unsigned int i = 0; i < program_options::image_bucket_count(); ++i) {
            for (unsigned int k = 0; k < HISTOGRAM_COUNT; ++k) {
                res += abs(bucket[k][i] - a->bucket[k][i]);
            }
        }
        if (res > program_options::image_threshold()) {
            return boost::shared_ptr<img>();
        }
    }
    if (program_options::image_precise()) {
        init_pixels();
        a->init_pixels();
        unsigned int res = 0;
        const unsigned int width0 = width(), width1 = a->width();
        const unsigned int height0 = height(), height1 = a->height();
        const unsigned int k = width0 > width1 ? width0 / width1 : 1;
        const unsigned int k1 = width1 > width0 ? width1 / width0 : 1;
        const unsigned int m = height0 > height1 ? height0 / height1 : 1;
        const unsigned int m1 = height1 > height0 ? height1 / height0 : 1;
        for (unsigned int i = 0, i1 = 0; i < width0 && i1 < width1; i += k, i1 += k1) {
            for (unsigned int j = 0, j1 = 0; j < height0 && j1 < height1; j += m, j1 += m1) {
                unsigned int p = i + j * width0, p1 = i1 + j1 * width1;
                res += abs(_pixels[p].red - a->_pixels[p1].red)
                    + abs(_pixels[p].green - a->_pixels[p1].green)
                    + abs(_pixels[p].blue - a->_pixels[p1].blue);
                if (res >= program_options::image_max_diff()) {
                    return boost::shared_ptr<img>();
                }
            }
        }
    }
    return _a;
}

comparison_result img::precompare(const boost::shared_ptr<base>& a) const {
    float diff = _aspect_ratio - static_cast<img*>(a.get())->_aspect_ratio;
    if (diff < -EPSILON) {
        return less;
    }
    if (diff > EPSILON) {
        return greater;
    }
    return equal;
}

static unsigned int round(unsigned int x) {
    unsigned int i = 1;
    for (; i < x; i <<= 1) ;
    return i - x > x - (i >> 1) ? i >> 1 : i;
}

void img::init_pixels() const {
    if (_pixels == 0) {
        Magick::Image im;
        im.read(path().string()); 
        #define CALC_SIZE(x) ((x) >= program_options::image_img_size() \
            ? program_options::image_img_size() : round(x))
        im.scale(Magick::Geometry(CALC_SIZE(im.size().width()), CALC_SIZE(im.size().height())));
        const Magick::PixelPacket* pix = im.getConstPixels(0, 0, im.size().width(), im.size().height());
        _pixels = new Magick::PixelPacket[im.size().width() * im.size().height() + 2 * PIX_IN_INT];
        *reinterpret_cast<unsigned int*>(_pixels) = im.size().width();
        *(reinterpret_cast<unsigned int*>(_pixels) + 1) = im.size().height();
        memcpy(_pixels += 2, pix, im.size().width() * im.size().height());
    }
}

}
