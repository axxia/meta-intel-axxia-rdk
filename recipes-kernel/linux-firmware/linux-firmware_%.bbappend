FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:"

SRC_URI_append_axxiax86-64 = " file://${RDK_KLM_ARCHIVE}"

do_install_append () {
    # Remove upstream QAT firmware files and install the specific Axxia KLM ones
    rm -f ${D}${nonarch_base_libdir}/firmware/qat*.bin
    install -m 0644 ${WORKDIR}/qat/qat/fw/*.bin ${D}${nonarch_base_libdir}/firmware/
}

PACKAGES =+ "${PN}-klm-qat-license ${PN}-klm-qat"

# For INTEL Axxia KLM QAT
LICENSE_${PN}-klm-qat = "Firmware-qat"
LICENSE_${PN}-klm-qat-license = "Firmware-qat"

FILES_${PN}-klm-qat = "${nonarch_base_libdir}/firmware/qat*.bin"
FILES_${PN}-klm-qat-license = "${nonarch_base_libdir}/firmware/LICENCE.qat_firmware"

RDEPENDS_${PN}-klm-qat += "${PN}-klm-qat-license"
