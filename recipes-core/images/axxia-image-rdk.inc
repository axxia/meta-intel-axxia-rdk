RDK_USERSPACE_PKG = " \
rdk-tools \
rdk-tools-dev"

IMAGE_INSTALL_append_axxiax86-64 += " \
linux-firmware-klm-qat \
${@bb.utils.contains('DISTRO_FEATURES', 'rdk-userspace', \
                     '${RDK_USERSPACE_PKG}', '', d)}"
