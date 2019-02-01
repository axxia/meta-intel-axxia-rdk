FILESEXTRAPATHS_prepend := "${RDK_ARCHIVE_PATH}:${THISDIR}/frags:"

SRC_URI_append_axxiax86-64 = " ${RDK_KLM_ARCHIVE} \
			       file://rdk-modules.scc "

BB_STRICT_CHECKSUM_axxiax86-64 = "0"

do_patch_rdk_modules () {
    cd ${STAGING_KERNEL_DIR}
    if [ ! -z "$(git status --porcelain)" ]; then
	git stash
	CHANGES=yes
    fi

    git tag before_rdk_commits

    if [ -d ${WORKDIR}/scripts -a -f ${WORKDIR}/scripts/patch_kernel_tree.sh ]
    then
       ${WORKDIR}/scripts/patch_kernel_tree.sh ${STAGING_KERNEL_DIR}
    fi

    for KLMC in cpk cpk-ae adk_netd hqm ies qat
    do
	if [ ! -d ${WORKDIR}/${KLMC} ]; then
	   continue
	fi

	${WORKDIR}/${KLMC}/scripts/patch_kernel_tree.sh ${STAGING_KERNEL_DIR}
	git add -A

	if [ "qat" = "${KLMC}" ]; then
	    # Add the -asn1 files to the commit.
	    if [ -d drivers/net/ethernet/intel/qat/qat_common ]; then
	       git add -f drivers/net/ethernet/intel/qat/qat_common/*-asn1.[ch]
	    elif [ -d drivers/staging/intel/qat/qat_common ]; then
	       git add -f drivers/staging/intel/qat/qat_common/*-asn1.[ch]
	    fi
	fi

	case "$(echo ${RDK_KLM_ARCHIVE} | cut -d'/' -f1)" in
	file*)
		RDK_KLM_RELEASE="$(readlink $(echo ${RDK_ARCHIVE_PATH}/${RDK_KLM_ARCHIVE} \
		| sed 's/file:\/\///g'))";;
	http*)
		RDK_KLM_RELEASE="$(echo ${RDK_KLM_ARCHIVE} | rev | cut -d'/' -f1 | rev)";;
	*)
		RDK_KLM_RELEASE="unknown_release_info";;
	esac

	TIPC="$(echo ${KLMC} | tr '[:lower:]' '[:upper:]') $(echo ${RDK_KLM_RELEASE} \
	      | cut -d'.' -f1 | sed 's/rdk_klm_src_//g')"
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

do_install_append() {
     install -d ${D}/usr/kernel-headers
     make headers_install INSTALL_HDR_PATH="${D}/usr/kernel-headers"
}

sysroot_stage_all_append() {
    KERNEL_HEADERS="${SYSROOT_DESTDIR}/usr/kernel-headers"
    KLM_HEADERS="${KERNEL_HEADERS}/include/klm"

    sysroot_stage_dir "${D}/usr/kernel-headers" "${KERNEL_HEADERS}"

    install -d ${KLM_HEADERS}
    mv ${KERNEL_HEADERS}/include/linux/adf_cfg*.h ${KLM_HEADERS}
    mv ${KERNEL_HEADERS}/include/linux/hqm_user.h ${KLM_HEADERS}
    mv ${KERNEL_HEADERS}/include/linux/ice_sw_ae*.h ${KLM_HEADERS}
}

PACKAGES += "kernel-headers"
FILES_kernel-headers = "/usr/kernel-headers"
