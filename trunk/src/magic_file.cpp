#include <magic.h>

#include "magic_file.h"

static magic_t magic_cookie = 0;

namespace magic {

void initialize() {
    if ((magic_cookie = magic_open(MAGIC_MIME_TYPE))) {
        magic_load(magic_cookie, 0);
    }
}

void destroy() {
    if (magic_cookie) {
        magic_close(magic_cookie);
    }
}

const char* mime_type(const char* s) {
    return magic_cookie ? magic_file(magic_cookie, s) : 0;
}

}
