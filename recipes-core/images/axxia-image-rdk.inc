USERSPACE_SUPPORT = " \
rdk-user \
rdk-user-dev"

IMAGE_INSTALL_append_axxiax86-64 += " \
linux-firmware-klm-qat \
${@bb.utils.contains('DISTRO_FEATURES', 'userspace', \
                     '${USERSPACE_SUPPORT}', '', d)} \
