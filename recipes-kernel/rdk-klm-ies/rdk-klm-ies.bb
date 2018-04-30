SUMMARY = "kernel module that supports Intel(R) Ethernet Switch. "
DESCRIPTION="kernel module that supports Intel(R) Ethernet Switch "
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=e2643b73e47fa4b29cdfad24ee42bed6"

SRC_URI = "file://${RDK_KLM_ARCHIVE}"
SRC_URI += "file://ies-build.patch"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

inherit module

S = "${WORKDIR}/ies"
export VERSION_FILE="${includedir}/linux/version.h"
export CONFIG_FILE="${STAGING_KERNEL_BUILDDIR}/include/generated/autoconf.h"

do_compile() {
	oe_runmake noisy 'KSRC=${KERNEL_SRC}' 'INSTALL_MOD_PATH=${D}'
}

do_install() {
	oe_runmake modules_install 'KSRC=${KERNEL_SRC}' 'INSTALL_MOD_PATH=${D}'
}
