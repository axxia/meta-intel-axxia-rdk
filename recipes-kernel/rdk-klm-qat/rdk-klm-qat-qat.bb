SUMMARY = "QAT kernel module"
DESCRIPTION="QAT kernel module"
LICENSE = "GPLv2 & BSD"
LIC_FILES_CHKSUM = "file://${WORKDIR}/qat/LICENSE.GPL;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "file://${RDK_KLM_ARCHIVE}"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

inherit module

S = "${WORKDIR}/qat"
export ICP_ROOT = "${S}"
export KDIR = "${STAGING_KERNEL_BUILDDIR}"
export SYSROOT = "${D}"

FILES_${PN} += "/lib/firmware/qat_c4xxx_mmp.bin"
FILES_${PN} += "/lib/firmware/qat_c4xxx.bin"

do_configure() {
  oe_runmake clean_qat
}

do_compile() {
  oe_runmake  qat_drivers
}


do_install() {
  oe_runmake qat_modules_install
  install -d ${D}/lib/firmware
  install -m 644 ${S}/qat/fw/qat_c4xxx_mmp.bin  ${D}/lib/firmware/qat_c4xxx_mmp.bin
  install -m 644 ${S}/qat/fw/qat_c4xxx.bin  ${D}/lib/firmware/qat_c4xxx.bin
}

BBCLASSEXTEND = "native nativesdk"
