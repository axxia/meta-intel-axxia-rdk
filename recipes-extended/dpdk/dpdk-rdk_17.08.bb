FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

require recipes-extended/dpdk/dpdk.inc

STABLE = "-stable"
BRANCH = "17.08"
SRCREV = "02657b4adcb8af773e26ec061b01cd7abdd3f0b6"

SRC_URI = "git://dpdk.org/dpdk${STABLE};branch=${BRANCH} \
           file://dpdk-16.04-add-RTE_KERNELDIR_OUT-to-split-kernel-bu.patch \
           file://${RDK_DPDK_PATCH} \
           file://examples-eventdev-pipeline-Fix-implicit-declaration.patch \
           file://${RDK_USER_ARCHIVE} "

LICENSE = "LGPLv2 & GPLv2 & BSD"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;md5=751419260aa954499f7abaabaa882bbe \
                    file://LICENSE.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSE.BSD;md5=701db1808cef1c74d6d226be6fa5be17"

DEPENDS += "libpcap openssl"

export SYSROOT="${WORKDIR}/recipe-sysroot"

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

# make sure compile target support SSE4.2 for auto-vectorizing
TARGET_CFLAGS_append_axxiax86-64 = " -msse4.2 "

# Extra flags required by ies_api_install target from rdk_user
IES_EXTRA_FLAGS = "host_alias=x86_64-intelaxxia-linux"

# qat_lib target from rdk_user don't support random ordering
# of some operations, thus force it make to run single-threaded
QAT_PARALLEL_MAKE = "-j1"

do_compile_prepend () {
	cd ${WORKDIR}
	oe_runmake cpk-ae-lib
	oe_runmake ${QAT_PARALLEL_MAKE} qat_lib
	oe_runmake ${IES_EXTRA_FLAGS} ies_api_install
}

do_install_append () {
	install -m 0755 ${IES_API_DIR}/bin/cli ${D}${bindir}
	install -m 0755 ${QAT_DIR}/install/bin/adf_ctl ${D}${bindir}

	install -m 0755 ${LIB_CPKAE_DIR}/libae_client.so ${D}${libdir}
	install -m 0755 ${QAT_DIR}/install/lib/libipsec_inline.so ${D}${libdir}
	install -m 0755 ${IES_API_DIR}/lib/libies_sdk-*.so ${D}${libdir}
	ln -sf libies_sdk-5.0.5.11.so ${D}${libdir}/libies_sdk.so
	install -m 0755 ${IES_API_DIR}/lib/libies_sdk.la ${D}${libdir}

	install -m 0644 ${LIB_CPKAE_DIR}/uapi/linux/ice_sw_ae*.h ${D}${includedir}
	install -m 0644 ${LIB_CPKAE_DIR}/libae_client*.h ${D}${includedir}
	install -m 0644 ${IES_API_DIR}/include/*.h ${D}${includedir}
	install -m 0644 ${QAT_DIR}/include/*.h ${D}${includedir}

	rm -rf ${D}/usr/share/mk
	rm -rf ${D}/usr/share/${RTE_TARGET}/app

	# remove local rpath to pass QA testing
	chrpath -d ${D}/${bindir}/cli

	chown -R root:root ${D}
}

INSANE_SKIP_${PN}-dev = "ldflags dev-elf"
