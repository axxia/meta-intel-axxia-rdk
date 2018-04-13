SUMMARY = "USDM kernel module"
DESCRIPTION="USDM kernel module"
LICENSE = "GPLv2 & BSD"
LIC_FILES_CHKSUM = "file://${WORKDIR}/qat/LICENSE.GPL;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "file://${RDK_ARCHIVE}"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

inherit module

S = "${WORKDIR}/qat"
export ICP_ROOT = "${S}"
export KDIR = "${KERNEL_SRC}"
export SYSROOT = "${D}"

do_configure() {
  oe_runmake  clean_usdm
}

do_compile() {
  oe_runmake usdm_drivers
}

do_install() {
  oe_runmake usdm_modules_install
}

BBCLASSEXTEND = "native nativesdk"
