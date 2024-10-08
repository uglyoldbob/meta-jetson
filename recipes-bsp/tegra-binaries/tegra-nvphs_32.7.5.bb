DESCRIPTION = "nvphs startup files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nvphs.init \
    file://nvphs.service \
"

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}/source"

do_install() {
    install -d ${D}${systemd_system_unitdir} ${D}${sysconfdir}/init.d
    install -m 0644 ${UNPACKDIR}/nvphs.service ${D}${systemd_system_unitdir}
    install -m 0755 ${UNPACKDIR}/nvphs.init ${D}${sysconfdir}/init.d/nvphs
    sed -i -e's,/usr/sbin,${sbindir},g' ${D}${systemd_system_unitdir}/nvphs.service
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvphs"
INITSCRIPT_PARAMS = "defaults 05"
SYSTEMD_SERVICE:${PN} = "nvphs.service"

RDEPENDS:${PN} = "tegra-nvphs-base"
