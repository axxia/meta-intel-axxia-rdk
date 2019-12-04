SUMMARY = "Intel RDK userspace tools"
DESCRIPTION = "Intel RDK package containing all userspace (API and CLI) sources."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "${RDK_TOOLS_ARCHIVE}"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

BB_STRICT_CHECKSUM_axxiax86-64 = "0"

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

# Overwrite IES_API_CFLAGS to unset global FORTIFY_SOURCE flag
export IES_API_CFLAGS = "-g -Wno-unused-result -U_FORTIFY_SOURCE \
			 -D_FORTIFY_SOURCE=0 -Wno-address-of-packed-member"

# qat_lib target from the main Makefile don't support random ordering
# of some operations, thus force it make to run single-threaded
QAT_PARALLEL_MAKE = "-j1"

# Don't remove libtool *.la files
REMOVE_LIBTOOL_LA = "0"

# Add new include path for KLM headers exported for userspace
CXXFLAGS += " -I${SYSROOT}/usr/kernel-headers/include/klm "

do_compile () {
	cd ${WORKDIR}/rdk
	oe_runmake cpk-ae-lib netd-lib
	oe_runmake ${QAT_PARALLEL_MAKE} qat_lib
	oe_runmake ${IES_EXTRA_FLAGS} ies_api_install
	oe_runmake cli
}

do_install () {
	oe_runmake -C ${WORKDIR}/rdk install

	install -d ${D}${bindir} ${D}${libdir}
	install -d ${D}${includedir} ${D}${sysconfdir}
	cp -r ${WORKDIR}/rdk/install/bin/* ${D}${bindir}
	cp -r ${WORKDIR}/rdk/install/lib/* ${D}${libdir}
	cp -r ${WORKDIR}/rdk/install/include/* ${D}${includedir}
	cp -r ${WORKDIR}/rdk/install/etc/* ${D}${sysconfdir}

	# recreate symlinks which were removed on install by "rsync -L --no-l"
	IES_LIB_NAME=$(basename ${D}${libdir}/libies_sdk-*.so\.[0-9]\.[0-9]*\.[0-9]*)
	ln -sf ${IES_LIB_NAME} ${D}${libdir}/libies_sdk.so
	ln -sf ${IES_LIB_NAME} ${D}${libdir}/libies_sdk-50sm.so.2

	# remove local rpath from bindir to pass QA testing
	test -f ${D}/${bindir}/ies_cli && chrpath -d ${D}/${bindir}/ies_cli
	test -f ${D}/${bindir}/iesserver && chrpath -d ${D}/${bindir}/iesserver
	test -f ${D}/${bindir}/cli && chrpath -d ${D}/${bindir}/cli

	# replace local rpaths to pass QA testing
	sed -i "s#libdir=.*#libdir=\'${libdir}\'#g" \
		${D}${libdir}/libies_sdk.la
}

FILES_${PN}-dev = " ${includedir} \
	${libdir}/libies_sdk.so \
	${libdir}/libies_sdk.la "

FILES_${PN} = " ${bindir} ${sysconfdir} ${libdir}"

INSANE_SKIP_${PN} = "ldflags"
INSANE_SKIP_${PN}-dev = "ldflags"

BBCLASSEXTEND = "native nativesdk"
