SUMMARY = "Intel RDK user-space modules"
DESCRIPTION = "Intel RDK is a set of user-space modules on top of the base DPDK package"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "file://${RDK_USER_ARCHIVE}"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

RDK_USER_VERSION ?= "unknown_release_info"
PR = "${RDK_USER_VERSION}"

DEPENDS = "libnl libpcap openssl"

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
export QAT_DRV_SRC = "${KSRC}"
export HQM_DRIVER = "${KSRC}/drivers/misc/hqm"
export ICE_SW_DIR = "${KSRC}/drivers/net/ethernet/intel/ice_sw"
export ICE_SW_AE_SRC_DIR = "${KSRC}/drivers/net/ethernet/intel/ice_sw_ae"

# Extra flags required by ies_api_install target from rdk_user
IES_EXTRA_FLAGS = "host_alias=x86_64-intelaxxia-linux"

# qat_lib target from rdk_user don't support random ordering 
# of some operations, thus force it make to run single-threaded
QAT_PARALLEL_MAKE = "-j1"

do_compile () {
	cd ${WORKDIR}
	oe_runmake cpk-ae-lib
	oe_runmake ${QAT_PARALLEL_MAKE} qat_lib
	oe_runmake ${IES_EXTRA_FLAGS} ies_api_install
}

do_install () {
	install -d ${D}${bindir}
	install -m 0755 ${IES_API_DIR}/bin/cli ${D}${bindir}
	install -m 0755 ${QAT_DIR}/install/bin/adf_ctl ${D}${bindir}

	install -d ${D}${libdir}
	install -m 0755 ${LIB_CPKAE_DIR}/libae_client.so ${D}${libdir}
	install -m 0755 ${QAT_DIR}/install/lib/libipsec_inline.so ${D}${libdir}
	install -m 0755 ${IES_API_DIR}/lib/libies_sdk-*.so ${D}${libdir}
	ln -sf $(basename $(ls ${IES_API_DIR}/lib/libies_sdk-*.so)) ${D}${libdir}/libies_sdk.so

	install -d ${D}${includedir} ${D}${includedir}/linux
	install -m 0644 ${LIB_CPKAE_DIR}/uapi/linux/ice_sw_ae_* ${D}${includedir}/linux
	install -m 0644 ${LIB_CPKAE_DIR}/libae_client*.h ${D}${includedir}
	install -m 0644 ${IES_API_DIR}/include/*.h ${D}${includedir}
	install -m 0644 ${QAT_DIR}/include/*.h ${D}${includedir}

	# remove local rpath to pass QA testing
	chrpath -d ${D}/${bindir}/cli
}

FILES_${PN} = " ${bindir} \
	${libdir}/libae_client.so \
	${libdir}/libipsec_inline.so \
	${libdir}/libies_sdk-*.so "

FILES_${PN}-dev = " ${includedir} \
	${libdir}/libies_sdk.so "

INSANE_SKIP_${PN} = "ldflags"

BBCLASSEXTEND = "native nativesdk"
