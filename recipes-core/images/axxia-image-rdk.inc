DPDK_SUPPORT = " \
dpdk-rdk \
dpdk-rdk-dev \
dpdk-rdk-examples \
dpdk-rdk-test"

USERSPACE_SUPPORT = " \
rdk-user \
rdk-user-dev"

IMAGE_INSTALL_axxiax86-64 += " \
${@bb.utils.contains('DISTRO_FEATURES', 'userspace', \
                     '${USERSPACE_SUPPORT}', '', d)} \
${@bb.utils.contains('DISTRO_FEATURES', 'dpdk', \
                     '${DPDK_SUPPORT} ${USERSPACE_SUPPORT}', '', d)}"
