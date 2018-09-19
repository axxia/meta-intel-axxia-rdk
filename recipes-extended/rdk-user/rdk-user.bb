SUMMARY = "Intel RDK user-space modules"
DESCRIPTION = "Intel RDK is a set of user-space modules"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "file://${RDK_USER_ARCHIVE}"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

RDK_USER_VERSION ?= "unknown_release_info"
PR = "${RDK_USER_VERSION}"

DEPENDS = "libnl libpcap openssl rsync-native"

inherit module

export SYSROOT="${STAGING_DIR_HOST}"

export LIB_CPKAE_DIR = "${WORKDIR}/user_modules/cpk-ae-lib"

export QAT_DIR = "${WORKDIR}/user_modules/qat"
export LIB_QAT18_DIR = "${QAT_DIR}"

export IES_API_DIR = "${WORKDIR}/user_modules/ies-api"
export IES_API_BUILD_DIR = "${IES_API_DIR}"
export IES_API_OUTPUT_DIR = "${IES_API_DIR}"
export IES_API_CORE_DIR = "user_modules/ies-api/core"

export KSRC = "${STAGING_KERNEL_DIR}"
export QAT_DRV_SRC = "${KSRC}/drivers/net/ethernet/intel/qat"
export HQM_DRIVER = "${KSRC}/drivers/misc/hqm"
export ICE_SW_DIR = "${KSRC}/drivers/net/ethernet/intel/ice_sw"
export ICE_SW_AE_SRC_DIR = "${KSRC}/drivers/net/ethernet/intel/ice_sw_ae"

# Extra flags required by ies_api_install target from rdk_user
IES_EXTRA_FLAGS = "host_alias=x86_64-intelaxxia-linux"

# qat_lib target from rdk_user don't support random ordering 
# of some operations, thus force it make to run single-threaded
QAT_PARALLEL_MAKE = "-j1"

# Don't remove libtool *.la files
REMOVE_LIBTOOL_LA = "0"

do_compile () {
	cd ${WORKDIR}
	oe_runmake cpk-ae-lib
	oe_runmake ${QAT_PARALLEL_MAKE} qat_lib
	oe_runmake ${IES_EXTRA_FLAGS} ies_api_install
}

do_install () {
	oe_runmake -C ${WORKDIR} install

	install -d ${D}${bindir} ${D}${libdir}
	install -m 0755 ${WORKDIR}/bin/* ${D}${bindir}
	install -m 0755 ${WORKDIR}/lib/* ${D}${libdir}

	install -d ${D}${includedir} ${D}${includedir}/linux ${D}${includedir}/pub
	install -m 0644 ${WORKDIR}/include/*.h ${D}${includedir}
	install -m 0644 ${WORKDIR}/include/Makefile ${D}${includedir}
	install -m 0644 ${WORKDIR}/include/linux/* ${D}${includedir}/linux
	install -m 0644 ${WORKDIR}/include/pub/* ${D}${includedir}/pub

	if [ -d ${WORKDIR}/etc ]; then
	   install -d ${D}${sysconfdir}
	   install -m 0644 ${WORKDIR}/etc/* ${D}${sysconfdir}
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
