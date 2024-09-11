DESCRIPTION = "Flash helper scripts"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"

inherit native

SRC_URI = " \
    file://tegra186-flash-helper.sh \
    file://tegra194-flash-helper.sh \
    file://tegra210-flash-helper.sh \
    file://nvflashxmlparse.py \
    file://make-sdcard.sh \
    file://tegra-signimage-helper.sh \
    file://find-jetson-usb.sh \
    file://rewrite-tegraflash-args.py \
    file://initrd-flash.sh \
    file://brcid-to-uid.py \
"

S = "${WORKDIR}/source"

do_install() {
    install -d ${D}${bindir}/tegra186-flash ${D}${bindir}/tegra210-flash
    install -m 0755 ${WORKDIR}/sources-unpack/tegra186-flash-helper.sh ${D}${bindir}/tegra186-flash/
    install -m 0755 ${WORKDIR}/sources-unpack/tegra194-flash-helper.sh ${D}${bindir}/tegra186-flash/
    install -m 0755 ${WORKDIR}/sources-unpack/tegra210-flash-helper.sh ${D}${bindir}/tegra210-flash/
    install -m 0755 ${WORKDIR}/sources-unpack/nvflashxmlparse.py ${D}${bindir}/tegra210-flash/nvflashxmlparse
    install -m 0755 ${WORKDIR}/sources-unpack/make-sdcard.sh ${D}${bindir}/tegra210-flash/make-sdcard
    install -m 0755 ${WORKDIR}/sources-unpack/nvflashxmlparse.py ${D}${bindir}/tegra186-flash/nvflashxmlparse
    install -m 0755 ${WORKDIR}/sources-unpack/make-sdcard.sh ${D}${bindir}/tegra186-flash/make-sdcard
    install -m 0755 ${WORKDIR}/sources-unpack/tegra-signimage-helper.sh ${D}${bindir}/tegra186-flash/tegra-signimage-helper
    for soc in 186 210; do
	install -m 0755 ${WORKDIR}/sources-unpack/find-jetson-usb.sh ${D}${bindir}/tegra$soc-flash/find-jetson-usb
	install -m 0755 ${WORKDIR}/sources-unpack/rewrite-tegraflash-args.py ${D}${bindir}/tegra$soc-flash/rewrite-tegraflash-args
	install -m 0755 ${WORKDIR}/sources-unpack/initrd-flash.sh ${D}${bindir}/tegra$soc-flash/initrd-flash
	install -m 0755 ${WORKDIR}/sources-unpack/brcid-to-uid.py ${D}${bindir}/tegra$soc-flash/brcid-to-uid
    done
}
