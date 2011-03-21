#ifndef _FILE_TYPER_H_
#define _FILE_TYPER_H_

#include <boost/shared_ptr.hpp>

#include "filesystem.h"
#include "kleisli.h"
#include "file_types.h"

struct file_typer_match_first: category::kleisli::arr<fs::path, boost::shared_ptr<file_type::base> > {
    void next(const fs::path& p);
};

struct file_typer_match_all: category::kleisli::arr<fs::path, boost::shared_ptr<file_type::base> > {
    void next(const fs::path& p);
};

#endif
