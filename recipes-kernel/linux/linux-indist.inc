FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:${THISDIR}/frags:"

SRC_URI_append_axxiax86-64 = " \
    file://${RDK_KLM_ARCHIVE} \
    file://rdk-modules.scc "

do_patch_rdk_modules () {
    cd ${STAGING_KERNEL_DIR}
    if [ ! -z "$(git status --porcelain)" ]; then
        git stash
        CHANGES=yes
    fi

    git tag before_rdk_commits

    for KLMC in cpk cpk-ae adk_netd hqm ies qat
    do
	if [ ! -d ${WORKDIR}/${KLMC} ]; then
	   continue
	fi

        ${WORKDIR}/${KLMC}/scripts/patch_kernel_tree.sh ${STAGING_KERNEL_DIR}
        git add -A

        if [ "qat" = "${KLMC}" ]; then
            # Add the -asn1 files to the commit.
            git add -f drivers/net/ethernet/intel/qat/qat_common/*-asn1.[ch]
        fi

        RDK_KLM_VERSION=$(readlink ${RDK_ARCHIVE_PATH}/${RDK_KLM_ARCHIVE} \
                        | cut -d'.' -f1 | sed 's/rdk_klm_src_//g')
        TIPC="$(echo ${KLMC} | tr '[:lower:]' '[:upper:]') ${RDK_KLM_VERSION}"
        git commit --no-gpg-sign --author="Intel <axxia@intel.com>" \
                   --message "${TIPC}" --message "NOT FOR UPSTREAMING"
    done

    git tag after_rdk_commits

    if [ "yes" = "${CHANGES}" ]; then
        git stash apply
    fi
}

addtask do_patch_rdk_modules after do_patch before do_kernel_configme
do_kernel_configme[depends] += "${PN}:do_patch_rdk_modules"
