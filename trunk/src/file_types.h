#ifndef _FILE_TYPES_H_
#define _FILE_TYPES_H_

#include "type_list.h"

#include "file_types/text.h"
#include "file_types/img.h"
#include "file_types/base.h"

typedef
    cons< file_type::base,
    cons< file_type::text,
    cons< file_type::img,
    nil > > > unsorted_file_types_list;

typedef type_sort<unsorted_file_types_list>::result file_types_list;

#endif
