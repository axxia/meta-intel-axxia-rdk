SUMMARY = "kernel module that supports configuration and use of the HQM device"
DESCRIPTION="kernel module that supports configuration and use of the HQM device"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "file://${RDK_ARCHIVE}"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

inherit module

S = "${WORKDIR}/hqm"
export KSRC="${KERNEL_SRC}"


# Development package with header files
FILES_${PN}-dev += "/usr/include/uapi/linux/hqm_user.h"
SYSROOT_DIRS += "/usr/include/uapi/linux"
PROVIDES_${PN} += "${PN}-dev"

do_install_append() {
	install -d ${D}${includedir}/uapi/linux
	install -m 0755 ${S}/uapi/linux/hqm_user.h ${D}${includedir}/uapi/linux
}

BBCLASSEXTEND = "native nativesdk"
