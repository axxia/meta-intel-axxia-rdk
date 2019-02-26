SUMMARY = "Intel RDK userspace tools"
DESCRIPTION = "Intel RDK package containing all userspace (API and CLI) sources."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "file://${RDK_TOOLS_ARCHIVE}"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

RDK_TOOLS_VERSION ?= "unknown_release_info"
PR = "${RDK_TOOLS_VERSION}"

DEPENDS = "virtual/kernel libnl libpcap openssl rsync-native"

inherit autotools

export SYSROOT="${STAGING_DIR_HOST}"

export LIB_CPKAE_DIR = "${WORKDIR}/rdk/user_modules/cpk-ae-lib"

export QAT_DIR = "${WORKDIR}/rdk/user_modules/qat"
export LIB_QAT18_DIR = "${QAT_DIR}"

export IES_API_DIR = "${WORKDIR}/rdk/user_modules/ies-api"
export IES_API_BUILD_DIR = "${IES_API_DIR}"
export IES_API_OUTPUT_DIR = "${IES_API_DIR}"
export IES_API_CORE_DIR = "user_modules/ies-api/core"

# Extra flags required by ies_api_install target from the main Makefile
IES_EXTRA_FLAGS = "host_alias=x86_64-intelaxxia-linux"

# qat_lib target from the main Makefile don't support random ordering
# of some operations, thus force it make to run single-threaded
QAT_PARALLEL_MAKE = "-j1"

# Don't remove libtool *.la files
REMOVE_LIBTOOL_LA = "0"

# Add new include path for KLM headers exported for userspace
CXXFLAGS += " -I${SYSROOT}/usr/kernel-headers/include/klm "

do_compile () {
	cd ${WORKDIR}/rdk
	oe_runmake cpk-ae-lib
	oe_runmake ${QAT_PARALLEL_MAKE} qat_lib
	oe_runmake ${IES_EXTRA_FLAGS} ies_api_install
	oe_runmake cli
}

do_install () {
	oe_runmake -C ${WORKDIR}/rdk install

	install -d ${D}${bindir} ${D}${libdir}
	install -m 0755 ${WORKDIR}/rdk/install/bin/* ${D}${bindir}
	install -m 0755 ${WORKDIR}/rdk/install/lib/* ${D}${libdir}

	install -d ${D}${includedir} ${D}${includedir}/linux ${D}${includedir}/pub
	install -m 0644 ${WORKDIR}/rdk/install/include/*.h ${D}${includedir}
	install -m 0644 ${WORKDIR}/rdk/install/include/Makefile ${D}${includedir}
	install -m 0644 ${WORKDIR}/rdk/install/include/linux/* ${D}${includedir}/linux
	install -m 0644 ${WORKDIR}/rdk/install/include/pub/* ${D}${includedir}/pub

	if [ -d ${WORKDIR}/rdk/install/etc ]; then
	   install -d ${D}${sysconfdir}
	   install -m 0644 ${WORKDIR}/rdk/install/etc/* ${D}${sysconfdir}
	fi

	# libies_sdk.so shoud be a symlink to the versioned lib
	ln -sf $(basename ${D}${libdir}/libies_sdk-*.so) ${D}${libdir}/libies_sdk.so

	# remove local rpath to pass QA testing
	chrpath -d ${D}/${bindir}/cli

	# Remove path to workdir from libtool file to pass QA testing
	sed -i '/libdir=/d' ${D}${libdir}/libies_sdk.la
}

FILES_${PN} = " ${bindir} ${sysconfdir} \
	${libdir}/libae_client.so \
	${libdir}/libipsec_inline.so \
	${libdir}/libies_sdk-*.so "

FILES_${PN}-dev = " ${includedir} \
	${libdir}/libies_sdk.so \
	${libdir}/libies_sdk.la "

INSANE_SKIP_${PN} = "ldflags"

BBCLASSEXTEND = "native nativesdk"
