#ifndef _FILE_TYPES_H_
#define _FILE_TYPES_H_

#include "../config.h"
#include "type_list.h"

#include "file_types/text.h"
#include "file_types/base.h"

#ifndef DISABLE_IMAGES
#include "file_types/img.h"
#endif

typedef
    cons< file_type::base,
    cons< file_type::text,
#ifndef DISABLE_IMAGES
    cons< file_type::img,
#endif
    nil
#ifndef DISABLE_IMAGES
    >
#endif
    > > unsorted_file_types_list;

typedef type_sort<unsorted_file_types_list>::result file_types_list;

#endif
