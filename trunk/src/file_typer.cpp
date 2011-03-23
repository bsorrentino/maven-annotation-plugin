#include "file_typer.h"

enum match_options
  { match_first
  , match_all
  };

typedef category::kleisli::end< boost::shared_ptr<file_type::base> > base_type;
typedef category::kleisli::sink< boost::shared_ptr<file_type::base> > sink_type;

template<match_options o, typename F, typename T> struct try_file_types;
template<match_options o, typename F>
struct try_file_types<o, F, nil> {
    static void try_file(const F& file, base_type& cont) {
        cont.next(file);
    }
};
template<match_options o>
struct try_file_types<o, fs::path, nil> {
    static void try_file(const fs::path& file, base_type& cont) { }
};
template<match_options o, typename F, typename H, typename T>
struct try_file_types<o, F, cons<H, T> > {
    static void try_file(const F& file, base_type& cont) {
        typedef boost::shared_ptr<H> H_ref;
        H_ref value = H::try_file(file);
        if (value) {
            try_file_types<o, H_ref, typename filter<H, T>::descendant>::try_file(value, cont);
        }
        if (o == match_all || !value) {
            try_file_types<o, F, typename filter<H, T>::not_descendant>::try_file(file, cont);
        }
    }
};

void file_typer_match_first::next(const fs::path& p) {
    try_file_types<match_first, fs::path, file_types_list>::try_file(p, sink_type::continuation());
}

void file_typer_match_all::next(const fs::path& p) {
    try_file_types<match_all, fs::path, file_types_list>::try_file(p, sink_type::continuation());
}
