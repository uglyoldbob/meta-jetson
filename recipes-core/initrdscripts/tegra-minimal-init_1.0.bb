DESCRIPTION = "Minimal initramfs init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://init-boot.sh \
    file://platform-preboot.sh \
    ${@'file://platform-preboot-cboot.sh' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''} \
    ${@'file://platform-mount-boot-part.sh' if d.getVar('TNSPEC_BOOTDEV') != 'mmcblk0p1' and not d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''} \
"
COMPATIBLE_MACHINE = "(tegra)"

TNSPEC_BOOTDEV ??= "mmcblk0p1"

S = "${WORKDIR}/source"

do_install() {
    install -m 0755 ${UNPACKDIR}/init-boot.sh ${D}/init
    install -m 0555 -d ${D}/proc ${D}/sys
    install -m 0755 -d ${D}/dev ${D}/mnt ${D}/run ${D}/usr
    install -m 1777 -d ${D}/tmp
    mknod -m 622 ${D}/dev/console c 5 1
    install -d ${D}${sysconfdir}
    if [ -e ${UNPACKDIR}/platform-preboot-cboot.sh ]; then
        cat ${UNPACKDIR}/platform-preboot-cboot.sh ${UNPACKDIR}/platform-preboot.sh > ${UNPACKDIR}/platform-preboot.tmp
        install -m 0644 ${UNPACKDIR}/platform-preboot.tmp ${D}${sysconfdir}/platform-preboot
        rm ${UNPACKDIR}/platform-preboot.tmp
    else
	install -m 0644 ${UNPACKDIR}/platform-preboot.sh ${D}${sysconfdir}/platform-preboot
    fi
    sed -i -e"s,@TNSPEC_BOOTDEV@,${TNSPEC_BOOTDEV},g" ${D}${sysconfdir}/platform-preboot
    if [ -e ${UNPACKDIR}/platform-mount-boot-part.sh ]; then
        install -m 0644 ${UNPACKDIR}/platform-mount-boot-part.sh ${D}${sysconfdir}/platform-mount-boot-part
    fi
}

RDEPENDS:${PN} = "util-linux-blkid"
FILES:${PN} = "/"
PACKAGE_ARCH = "${MACHINE_ARCH}"
