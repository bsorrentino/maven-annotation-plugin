bin_PROGRAMS = pn

pn_SOURCES = \
    src/logger.cpp \
    src/file_types/img.cpp \
    src/file_types/base.cpp \
    src/file_types/text.cpp \
    src/program_options.cpp \
    src/magic_file.cpp \
    src/filesystem.cpp \
    src/default_main.cpp \
    src/main.cpp \
    src/file_typer.cpp \
    src/clusterization.cpp \
    src/logger.h \
    src/file_types/img.h \
    src/file_types/text.h \
    src/file_types/base.h \
    src/kleisli.h \
    src/type_list.h \
    src/program_options.h \
    src/magic_file.h \
    src/comparator.h \
    src/file_types.h \
    src/clusterization.h \
    src/file_typer.h \
    src/filesystem.h

file_conf = file.conf
pkgdata_DATA = $(file_conf)

uninstall: uninstall-am
	@echo " ( rmdir '$(pkgdatadir)' )"
	-@rmdir '$(pkgdatadir)'

pn_CXXFLAGS = -I/usr/include/ImageMagick -DDATA_DIR='"$(pkgdatadir)"' -DFILE_CONF='"$(file_conf)"'
pn_LDADD = -lMagick++ -lboost_program_options -lboost_filesystem -lboost_system
