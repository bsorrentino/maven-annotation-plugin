#ifndef _FILE_TYPE_H_
#define _FILE_TYPE_H_

#include <vector>
#include <iostream>
#include <typeinfo>

#include "filesystem.h"
#include "kleisli.h"
#include "file_types.h"

enum match_options
  { match_first
  , match_all
  };

template<match_options o> class file_typer: public category::kleisli::arr<fs::path, file_type::base> {
    template<typename F, typename T> struct try_file_types;
    template<typename F>
    struct try_file_types<F, nil> {
        static void try_file(const F& file, category::kleisli::arr<fs::path, file_type::base>& cont) {
            cont(file);
        }
    };
    template<typename F, typename H, typename T>
    struct try_file_types<F, cons<H, T> > {
        static void try_file(const F& file, category::kleisli::arr<fs::path, file_type::base>& cont) {
            //TODO: smart ptrs
            H value;
            if (H::try_file(file, &value)) {
                try_file_types<H, typename filter<H, T>::descendant>::try_file(value, cont);
                if (o == match_all) {
                    try_file_types<F, typename filter<H, T>::not_descendant>::try_file(file, cont);
                }
            } else {
                try_file_types<F, T>::try_file(file, cont);
            }
        }
    };
public:
    void next(const fs::path& p) {
        try_file_types<fs::path, file_types_list>::try_file(p, *this);
    }
};

#endif
