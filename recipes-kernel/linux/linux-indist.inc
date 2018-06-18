FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:${THISDIR}/frags:\
${THISDIR}/patches:"

SRC_URI_append_axxiax86-64 = " \
    file://Backport-ice-linux-Fix-IDC-ADK-TX-path.patch \
    file://${RDK_KLM_ARCHIVE} \
    file://rdk-modules.scc "

META_INTEL_AXXIA_RDK_LAYER_PATH:="${THISDIR}"
export META_INTEL_AXXIA_RDK_LAYER_PATH

do_patch_rdk_modules () {
    cd ${STAGING_KERNEL_DIR}
    if [ ! -z "$(git status --porcelain)" ]; then
        git stash
        CHANGES=yes
    fi

    RDK_KLM_VERSION=$(git -C ${META_INTEL_AXXIA_RDK_LAYER_PATH} describe --dirty)
    echo ${RDK_KLM_VERSION}

    for KLMC in cpk cpk-ae hqm ies qat
    do
        ${WORKDIR}/${KLMC}/scripts/patch_kernel_tree.sh ${STAGING_KERNEL_DIR}
        git add -A

        if [ "qat" = "${KLMC}" ]; then
            # Fix a few things the QAT patch script doesn't handle...
            # 1> Remove the extraneous '$' directory it creates.
            rm -rf \$
            # 2> Add the -asn1 files to the commit.
            git add -f drivers/crypto/qat/qat_common/*-asn1.[ch]
        fi
 
        TIPC="$(echo ${KLMC} | tr '[:lower:]' '[:upper:]') ${RDK_KLM_VERSION}"
        git commit --author="Intel <axxia@intel.com>" -m "${TIPC}" -m "NOT FOR RELEASE"
    done

    if [ "yes" = "${CHANGES}" ]; then
        git stash apply
    fi
}

addtask do_patch_rdk_modules after do_kernel_metadata before do_patch
do_patch[depends] += "${PN}:do_patch_rdk_modules"
