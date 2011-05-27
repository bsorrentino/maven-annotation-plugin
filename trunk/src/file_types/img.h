#ifndef _FILE_TYPE_IMG_H_
#define _FILE_TYPE_IMG_H_

#include <vector>
#include <boost/shared_ptr.hpp>
#include <Magick++.h>

#include "base.h"
#include "../type_list.h"

namespace file_type {

struct img: base {
    img(const fs::path& p, const Magick::Image& image);
    virtual ~img();
    static boost::shared_ptr<img> try_file(const boost::shared_ptr<base>& file);
    boost::shared_ptr<base> compare(const boost::shared_ptr<base>& a) const;
    comparison_result precompare(const boost::shared_ptr<base>& a) const;
private:
    static const unsigned int HISTOGRAM_COUNT = 3;
    std::vector<double> bucket[HISTOGRAM_COUNT];
    mutable Magick::PixelPacket* _pixels;
    float _aspect_ratio;
    void init_pixels() const;
    unsigned int width() const { return *(reinterpret_cast<unsigned int*>(_pixels) - 2); }
    unsigned int height() const { return *(reinterpret_cast<unsigned int*>(_pixels) - 1); }
};

}

#endif
