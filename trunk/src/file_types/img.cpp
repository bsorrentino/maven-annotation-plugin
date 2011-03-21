#include "img.h"
#include "../kleisli.h"

namespace file_type {

boost::shared_ptr<img> img::try_file(const boost::shared_ptr<base>& file) {
    return boost::shared_ptr<img>();
}

}
