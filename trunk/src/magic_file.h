#ifndef _MAGIC_FILE_H_
#define _MAGIC_FILE_H_

namespace magic {
    void initialize();
    void destroy();
    const char* mime_type(const char* s);
}

#endif
