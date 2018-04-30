SUMMARY = "kernel module for Intel(R) 100G switch mode application extension driver."
DESCRIPTION="kernel module for Intel(R) 100G switch mode application extension driver."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI  = "file://${RDK_KLM_ARCHIVE}"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

inherit module

S = "${WORKDIR}/cpk-ae"
export KSRC="${KERNEL_SRC}"

BBCLASSEXTEND = "native nativesdk"

