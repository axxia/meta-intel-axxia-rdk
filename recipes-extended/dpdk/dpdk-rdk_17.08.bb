FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

require recipes-extended/dpdk/dpdk.inc

STABLE = "-stable"
BRANCH = "17.08"
SRCREV = "02657b4adcb8af773e26ec061b01cd7abdd3f0b6"

SRC_URI = "git://dpdk.org/dpdk${STABLE};branch=${BRANCH} \
           file://dpdk-16.04-add-RTE_KERNELDIR_OUT-to-split-kernel-bu.patch \
           file://${RDK_DPDK_PATCH} \
           file://examples-eventdev-pipeline-Fix-implicit-declaration.patch "

LICENSE = "LGPLv2 & GPLv2 & BSD"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;md5=751419260aa954499f7abaabaa882bbe \
                    file://LICENSE.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSE.BSD;md5=701db1808cef1c74d6d226be6fa5be17"

DEPENDS_append_axxiax86-64 = " rdk-user"

export SYSROOT="${WORKDIR}/recipe-sysroot"
export LIB_CPKAE_DIR = "${SYSROOT}/usr/lib"
export IES_API_DIR = "${SYSROOT}/usr/lib"
export LIB_QAT18_DIR = "${SYSROOT}/usr/lib"

# make sure compile target support SSE4.2 for auto-vectorizing
TARGET_CFLAGS_append_axxiax86-64 = "-msse4.2 "

do_install_append () {
	rm -rf ${D}/usr/share/mk
	rm -rf ${D}/usr/share/${RTE_TARGET}/app

	chown -R root:root ${D}
}
