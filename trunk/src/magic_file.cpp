#include <magic.h>

#ifdef HAVE_LIBMAGIC
#ifdef HAVE_MAGIC_H
#define HAVE_MAGIC
#endif
#endif

#ifdef HAVE_MAGIC
#include "magic_file.h"
#endif

static magic_t magic_cookie = 0;

namespace magic {

void initialize() {
#ifdef HAVE_MAGIC
    if ((magic_cookie = magic_open(MAGIC_MIME_TYPE))) {
        magic_load(magic_cookie, 0);
    }
#endif
}

void destroy() {
#ifdef HAVE_MAGIC
    if (magic_cookie) {
        magic_close(magic_cookie);
    }
#endif
}

const char* mime_type(const char* s) {
#ifdef HAVE_MAGIC
    return magic_cookie ? magic_file(magic_cookie, s) : 0;
#else
    return 0;
#endif
}

}
