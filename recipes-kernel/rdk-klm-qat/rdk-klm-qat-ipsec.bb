SUMMARY = "Inline IPSEC kernel module"
DESCRIPTION="Inline IPSEC kernel module"
LICENSE = "GPLv2 & BSD"
LIC_FILES_CHKSUM = "file://${WORKDIR}/qat/LICENSE.GPL;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "file://${RDK_ARCHIVE}"
SRC_URI += "file://Makefile-inline.patch;patchdir=${S}/inline"

FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

inherit module

# ipsec_inline module load requires parameters
module_conf_ipsec_inline = "options ipsec_inline type=1"
KERNEL_MODULE_PROBECONF += "ipsec_inline"


S = "${WORKDIR}/qat"
export ICP_ROOT = "${S}"
export KDIR = "${STAGING_KERNEL_BUILDDIR}"
export SYSROOT = "${D}"

do_configure() {
  oe_runmake  clean_inline
}

do_compile() {
  oe_runmake  inline_drivers 'INSTALL_MOD_PATH=${D}'
}

do_install() {
  oe_runmake inline_modules_install 'INSTALL_MOD_PATH=${D}'
}

BBCLASSEXTEND = "native nativesdk"
