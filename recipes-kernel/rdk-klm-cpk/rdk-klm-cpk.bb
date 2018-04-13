SUMMARY = "kernel module that supports Network Interface and Scheduler (ice_sw)... "
DESCRIPTION="kernel module that supports Network Interface and Scheduler (ice_sw)..."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a0f4197bee94e470f89d986fcba24e41"

SRC_URI = "file://${RDK_ARCHIVE}"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

inherit module

S = "${WORKDIR}/cpk"
export KSRC="${KERNEL_SRC}"

do_compile() {
  oe_runmake
}

BBCLASSEXTEND = "native nativesdk"

