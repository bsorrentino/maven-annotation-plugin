bin_PROGRAMS = pn

FILE_CONF = file.conf
pkgdata_DATA = $(FILE_CONF)

if DISABLE_IMAGES
else
images_pn_sources = src/file_types/img.cpp src/file_types/img.h
images_pn_cxxflags = -I/usr/include/ImageMagick
images_pn_ldadd = -lMagick++
endif

pn_SOURCES = \
    src/logger.cpp \
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
    src/filesystem.h \
	$(images_pn_sources) \
    $(FILE_CONF)

pn_CXXFLAGS = $(images_pn_cxxflags) -DDATA_DIR='"$(pkgdatadir)"'
pn_LDADD = $(images_pn_ldadd) -lboost_program_options -lboost_filesystem -lboost_system

uninstall: uninstall-am
	@echo " ( rmdir '$(pkgdatadir)' )"
	-@rmdir '$(pkgdatadir)'
