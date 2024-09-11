SUMMARY = "Wrapper for enabling systemd services"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PR = "r6"

inherit native

SRC_URI = "file://systemctl"

S = "${WORKDIR}/source"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/sources-unpack/systemctl ${D}${bindir}
}
